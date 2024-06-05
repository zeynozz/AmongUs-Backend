package at.fhv.backend.controller;

import at.fhv.backend.model.*;
import at.fhv.backend.model.com.GameCom;
import at.fhv.backend.model.com.JoinCom;
import at.fhv.backend.model.com.KillCom;
import at.fhv.backend.model.com.MoveCom;
import at.fhv.backend.services.GameService;
import at.fhv.backend.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;
    private final SimpMessagingTemplate messagingTemplate; // Injected messaging template
    private final Map<String, Game> games = new HashMap<>();

    @Autowired
    public GameController(GameService gameService, PlayerService playerService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/host")
    public ResponseEntity<Game> host(@RequestBody GameCom gameCom) throws FileNotFoundException {
        Game createdGame = gameService.host(gameCom.getPlayer(), Integer.parseInt(gameCom.getNumberOfPlayers()), Integer.parseInt(gameCom.getNumberOfImpostors()), gameCom.getMap());
        if (createdGame != null) {
            games.put(createdGame.getGameCode(), createdGame);
            return ResponseEntity.ok(createdGame);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{gameCode}")
    public ResponseEntity<Game> getGameByCode(@PathVariable String gameCode) {
        Game game = gameService.getGameByCode(gameCode);
        if (game != null) {
            return ResponseEntity.ok(game);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @MessageMapping("/join")
    @SendToUser("/topic/playerJoined")
    public ResponseEntity<?> createPlayer(@Payload JoinCom joinMessage) {
        if (joinMessage == null || joinMessage.getUsername() == null || joinMessage.getGameCode() == null || joinMessage.getColor() == null) {
            return ResponseEntity.badRequest().body("Error - creating the player");
        }

        try {
            Game game = gameService.getGameByCode(joinMessage.getGameCode());

            if (game == null) {
                return ResponseEntity.notFound().build();
            }

            if (game.getPlayers().size() >= game.getNumberOfPlayers()) {
                return ResponseEntity.badRequest().body("Lobby is full :(");
            }

            Player player = playerService.createPlayer(joinMessage.getUsername(), joinMessage.getPosition(), game, joinMessage.getColor());
            game.getPlayers().add(player);

            game.setPlayers(playerService.setRandomRole(game.getPlayers()));
            return ResponseEntity.ok()
                    .header("playerId", String.valueOf(player.getId()))
                    .body(game);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating player: " + e.getMessage());
        }
    }

    @MessageMapping("/{gameCode}/play")
    @SendTo("/topic/{gameCode}/play")
    public Game playGame(@RequestBody Game gameToPlay) {
        Game game = gameService.startGame(gameToPlay.getGameCode());
        gameService.setGameAttributes(gameToPlay.getGameCode(), gameToPlay.getPlayers());

        for (Player player : game.getPlayers()) {
            System.out.println("Player id: " + player.getId() + " Role: " + player.getRole());
        }
        return game;
    }

    @MessageMapping("/move")
    @SendTo("/topic/positionChange")
    public Game movePlayer(@Payload MoveCom playerMoveMessage) {
        int playerId = playerMoveMessage.getId();
        Game game = gameService.getGameByCode(playerMoveMessage.getGameCode());
        Player player = game.getPlayers().stream().filter(p -> p.getId() == playerId).findFirst().orElse(null);

        if (player != null) {
            if (player.getStatus() == Status.ALIVE){
            Position newPosition = playerService.calculateNewPosition(player, playerMoveMessage.getKeyCode());
            playerService.updatePlayerPosition(player, newPosition);
            System.out.println("Player ID: " + playerId + " moved to position: " + player.getPosition().getX() + ", " + player.getPosition().getY());
            return game;
        } else {
                System.out.println("Move attempted by player ID: " + playerId + " who is DEAD.");
            }
        }
        return null;
    }

    @MessageMapping("/kill")
    public void killPlayer(@Payload KillCom killCom) {
        Game game = games.get(killCom.getGameCode());
        if (game != null) {
            Player killer = game.getPlayers().stream()
                    .filter(p -> p.getId() == killCom.getKillerId())
                    .findFirst()
                    .orElse(null);
            Player victim = game.getPlayers().stream()
                    .filter(p -> p.getId() == killCom.getVictimId())
                    .findFirst()
                    .orElse(null);
            if (killer != null && victim != null && "IMPOSTOR".equals(killer.getRole()) && victim.getStatus() == Status.ALIVE) {
                if (isAdjacent(killer.getPosition(), victim.getPosition())) {
                    victim.setStatus(Status.DEAD);
                    victim.setColor(victim.getChosenColor() + "Ghost"); // Update color to ghost color
                    System.out.println("Player " + victim.getUsername() + " has been killed");

                    // Send kill animation to the impostor and the victim
                    messagingTemplate.convertAndSendToUser(killer.getUsername(), "/queue/killAnimation", "KILL_ANIMATION");
                    messagingTemplate.convertAndSendToUser(victim.getUsername(), "/queue/killAnimation", "KILL_ANIMATION");

                    // Update game state for all players
                    messagingTemplate.convertAndSend("/topic/playerKilled", game);

                    sendPlayerRemovedMessage(killCom.getGameCode(), killCom.getVictimId());
                } else {
                    System.err.println("Victim is not adjacent to the impostor");
                }
            }
        } else {
            System.err.println("Kill action failed");
        }
    }



    private void sendPlayerRemovedMessage(String gameCode, int playerId) {
        Map<String, Object> message = new HashMap<>();
        message.put("gameCode", gameCode);
        message.put("removedPlayerId", playerId);

        messagingTemplate.convertAndSend("/topic/playerRemoved", message);
    }

    @MessageMapping("/checkAdjacent")
    public void checkAdjacent(PositionCheckRequest request) {
        boolean isAdjacent = isAdjacent(request.getPos1(), request.getPos2());
        messagingTemplate.convertAndSend("/topic/checkAdjacentResult", new PositionCheckResponse(request.getPlayerId(), isAdjacent));
    }

    private boolean isAdjacent(Position pos1, Position pos2) {
        int xDiff = Math.abs(pos1.getX() - pos2.getX());
        int yDiff = Math.abs(pos1.getY() - pos2.getY());
        return (xDiff == 1 && yDiff == 0) || (xDiff == 0 && yDiff == 1);
    }



    @MessageMapping("/emergency")
    public void handleEmergency(@Payload String gameCode) {
        gameService.triggerEmergency(gameCode);
    }

    @MessageMapping("/sabotage")
    public void handleSabotage(@Payload String gameCode) {
        gameService.triggerSabotage(gameCode);
    }

    @MessageMapping("/vent")
    @SendTo("/topic/positionChange")
    public Game ventPlayer(@Payload MoveCom playerMoveMessage) {
        int playerId = playerMoveMessage.getId();
        Game game = gameService.getGameByCode(playerMoveMessage.getGameCode());
        Player player = game.getPlayers().stream().filter(p -> p.getId() == playerId).findFirst().orElse(null);

        if (player != null) {
            Position newPosition = playerService.teleportToVent(player.getPosition(), game.getMap());
            playerService.updatePlayerPosition(player, newPosition);
            System.out.println("Player ID: " + playerId + " teleported to position: " + newPosition.getX() + ", " + newPosition.getY());
            game.getPlayers().stream().filter(p -> p.getId() == playerId).findFirst().ifPresent(p -> p.setPosition(newPosition));
            return game;
        }
        return null;
    }





}
