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
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;
    private final Map<String, Game> games = new HashMap<>();

    @Autowired
    public GameController(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
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

    @MessageMapping("/updatePlayerColor")
    @SendTo("/topic/colorChange")
    public Player updatePlayerColor(@Payload Player updatedPlayer) {
        Game game = games.get(updatedPlayer.getGame().getGameCode());
        if (game != null) {
            for (Player player : game.getPlayers()) {
                if (player.getId() == updatedPlayer.getId()) {
                    player.setColor(updatedPlayer.getColor());
                    break;
                }
            }
            gameService.notifyColorChange(game, updatedPlayer);
        }
        return updatedPlayer;
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
        if (joinMessage == null || joinMessage.getUsername() == null || joinMessage.getGameCode() == null) {
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
            Position newPosition = playerService.calculateNewPosition(player.getPosition(), playerMoveMessage.getKeyCode());
            playerService.updatePlayerPosition(player, newPosition);
            System.out.println("Player ID: " + playerId + " moved to position: " + player.getPosition().getX() + ", " + player.getPosition().getY());
            return game;
        }

        return null;
    }
}
