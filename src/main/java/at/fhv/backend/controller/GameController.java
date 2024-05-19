package at.fhv.backend.controller;

import at.fhv.backend.model.*;
import at.fhv.backend.model.com.GameCom;
import at.fhv.backend.model.com.JoinCom;
import at.fhv.backend.model.com.MoveCom;
import at.fhv.backend.services.GameService;
import at.fhv.backend.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@Controller
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;

    @Autowired
    public GameController(GameService gameService, PlayerService playerservice) {
        this.gameService = gameService;
        this.playerService = playerservice;
    }

    @PostMapping("/host")
    public ResponseEntity<Game> host(@RequestBody GameCom GameCom) throws FileNotFoundException {
        System.out.println("Received request to create game with username: " + GameCom.getPlayer().getUsername() + " number of players: " + GameCom.getNumberOfPlayers() + " number of impostors: " + GameCom.getNumberOfImpostors() + " map: " + GameCom.getMap());

        Game createdGame = gameService.host(GameCom.getPlayer(), Integer.parseInt(GameCom.getNumberOfPlayers()), Integer.parseInt(GameCom.getNumberOfImpostors()), GameCom.getMap());

        if (createdGame != null) {
            return ResponseEntity.ok(createdGame);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{gameCode}")
    public ResponseEntity<Game> getGameByCode(@PathVariable String gameCode) {
        System.out.println("Code: " + gameCode);
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
        if (joinMessage == null ||
                joinMessage.getUsername() == null ||
                joinMessage.getGameCode() == null) {
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

            Player player = playerService.createPlayer(joinMessage.getUsername(), joinMessage.getPosition(), game);
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

        for (int i = 0; i < game.getPlayers().size(); i++) {
            System.out.println("Player id: " + game.getPlayers().get(i).getId() +
                    " Role: " + game.getPlayers().get(i).getRole());
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
            Position newPosition = playerService.calculateNewPosition(player.getPosition(), playerMoveMessage.getKeyCode());
            playerService.updatePlayerPosition(player, newPosition);
            System.out.println("Player ID: " + playerId + " moved to position: " + player.getPosition().getX() + ", " + player.getPosition().getY());
            return game;
        }

        return null;
    }

    @PostMapping("/kill")
    public ResponseEntity<Game> attemptKill(@RequestBody KillRequestMessage killRequestMessage) {
        int playerId = killRequestMessage.getPlayerId();
        Game game = gameService.getGameByCode(killRequestMessage.getGameCode());
        Player player = game.getPlayers().stream().filter(p -> p.getId() == playerId).findFirst().orElse(null);

        if (player != null) {
            Player target = game.getPlayers().stream().filter(p -> p.getId() == killRequestMessage.getTargetId()).findFirst().orElse(null);
            if (target != null) {
                // Entferne den getöteten Spieler aus dem Spiel
                game.getPlayers().remove(target);
                System.out.println("Player ID: " + playerId + " killed Player ID: " + target.getId());
                return ResponseEntity.ok(game);
            } else {
                System.out.println("Target player not found.");
            }
        }

        return ResponseEntity.notFound().build();
    }




}