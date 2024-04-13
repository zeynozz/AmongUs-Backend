package at.fhv.backend.controller;

import at.fhv.backend.model.*;
import at.fhv.backend.model.com.gameCom;
import at.fhv.backend.model.com.joinCom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/game")
public class gameController {
    private final at.fhv.backend.services.gameService gameService;
    private final at.fhv.backend.services.playerService playerService;

    @Autowired
    public gameController(at.fhv.backend.services.gameService gameService, at.fhv.backend.services.playerService playerservice) {
        this.gameService = gameService;
        this.playerService = playerservice;
    }

    @PostMapping("/host")
    public ResponseEntity<game> host(@RequestBody gameCom gameCom) {
        System.out.println("Request from: " + gameCom.getPlayer().getUsername());

        game hosted = gameService.host(gameCom.getPlayer(), Integer.parseInt(gameCom.getNumberOfPlayers()), Integer.parseInt(gameCom.getNumberOfImpostors()));

        if (hosted != null) {
            return ResponseEntity.ok(hosted);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{gameCode}")
    public ResponseEntity<game> getGameByCode(@PathVariable String gameCode) {
        System.out.println("Code: " + gameCode);
        game game = gameService.getGameByCode(gameCode);
        if (game != null) {
            return ResponseEntity.ok(game);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @MessageMapping("/join")
    @SendTo("/topic/playerJoined")
    public ResponseEntity<?> createPlayer(@Payload joinCom joinMessage) {
        if (joinMessage == null ||
                joinMessage.getUsername() == null ||
                joinMessage.getGameCode() == null) {
            return ResponseEntity.badRequest().body("Error - creating the player");
        }

        try {
            game game = gameService.getGameByCode(joinMessage.getGameCode());

            if (game == null) {
                return ResponseEntity.notFound().build();
            }

            if (game.getPlayers().size() >= game.getNumberOfPlayers()) {
                return ResponseEntity.badRequest().body("Lobby is full :(");
            }

            player player = playerService.createPlayer(joinMessage.getUsername(), joinMessage.getPosition(), game);
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
    public game playGame(@RequestBody game gameToPlay) {
        game game = gameService.startGame(gameToPlay.getGameCode());
        gameService.setGameAttributes(gameToPlay.getGameCode(), gameToPlay.getPlayers());

        for (int i = 0; i < game.getPlayers().size(); i++) {
            System.out.println("Player id: " + game.getPlayers().get(i).getId() +
                    " Role: " + game.getPlayers().get(i).getRole());
        }
        return game;
    }
}