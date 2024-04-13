package at.fhv.backend.services;

import at.fhv.backend.model.game;
import at.fhv.backend.generators.gameCodeGenerator;
import at.fhv.backend.model.player;
import at.fhv.backend.storage.gameStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class gameService {
    private final gameStorage gameRepository;
    private final at.fhv.backend.services.playerService playerService;

    @Autowired
    public gameService(gameStorage gameRepository, at.fhv.backend.services.playerService playerService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
    }

    public game host(player player, int numberOfPlayers, int numberOfImpostors) {
        game game = new game(generateGameCode(), numberOfPlayers, numberOfImpostors);


        System.out.println("Game Code: " + game.getGameCode());
        at.fhv.backend.model.player p = playerService.createPlayer(player.getUsername(), player.getPosition(), game);

        game.getPlayers().add(p);
        gameRepository.save(game);

        for (int i = 0; i < game.getPlayers().size(); i++) {
            System.out.println("Player ID: " + game.getPlayers().get(i).getId() + " Game Role: " + game.getPlayers().get(i).getRole());
        }

        return game;
    }

    private String generateGameCode() {
        return gameCodeGenerator.generateGameCode();
    }

    public game getGameByCode(String gameCode) {
        return gameRepository.findByGameCode(gameCode);
    }

    public game startGame(String gameCode) {
        game game = gameRepository.findByGameCode(gameCode);
        if (game != null) {
            gameRepository.save(game);
        }
        return game;
    }

    public game setGameAttributes(String gameCode, List<player> players) {
        game game = gameRepository.findByGameCode(gameCode);
        if (game != null) {
            game.setPlayers(players);
            gameRepository.save(game);
        }
        return game;
    }
}
