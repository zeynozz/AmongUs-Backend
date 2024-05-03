package at.fhv.backend.services;

import at.fhv.backend.model.Game;
import at.fhv.backend.generators.CodeGenerator;
import at.fhv.backend.model.Player;
import at.fhv.backend.memory.GameMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;

@Service
public class GameService {
    private final GameMemory gameRepository;
    private final PlayerService playerService;

    private final MapService mapService;
    @Autowired
    public GameService(GameMemory gameRepository, PlayerService playerService, MapService mapService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
        this.mapService = mapService;

    }

    public Game host(Player player, int numberOfPlayers, int numberOfImpostors, String map) throws FileNotFoundException {
        Game game = new Game(gameCodeGenerator(), numberOfPlayers, numberOfImpostors, map, mapService);

        System.out.println("Game Code: " + game.getGameCode());
        Player p = playerService.createPlayer(player.getUsername(), player.getPosition(), game);

        p = playerService.setInitialRandomRole(game.getNumberOfPlayers(), game.getNumberOfImpostors(), p);
        game.getPlayers().add(p);
        gameRepository.save(game);

        for (int i = 0; i < game.getPlayers().size(); i++) {
            System.out.println("Player ID: " + game.getPlayers().get(i).getId() + " Game Role: " + game.getPlayers().get(i).getRole());
        }

        return game;
    }

    private String gameCodeGenerator() {
        return CodeGenerator.generateGameCode();
    }

    public Game getGameByCode(String gameCode) {
        return gameRepository.findByGameCode(gameCode);
    }

    public Game startGame(String gameCode) {
        Game game = gameRepository.findByGameCode(gameCode);
        if (game != null) {
            gameRepository.save(game);
        }
        return game;
    }

    public Game setGameAttributes(String gameCode, List<Player> Players) {
        Game game = gameRepository.findByGameCode(gameCode);
        if (game != null) {
            game.setPlayers(Players);
            gameRepository.save(game);
        }
        return game;
    }
}
