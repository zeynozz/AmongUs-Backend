package at.fhv.backend.storage;

import at.fhv.backend.model.game;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class gameStorage {
    private final HashMap<String, game> games = new HashMap<>();

    public void save(game game) {
        games.put(game.getGameCode(), game);
        for (at.fhv.backend.model.game g : games.values()) {
            System.out.println("Your Game Code: " + g.getGameCode());
        }
    }

    public game findByGameCode(String gameCode) {
        return games.get(gameCode);
    }

    public void deleteByGameCode(String gameCode) {
        games.remove(gameCode);
    }

    public boolean existsByGameCode(String gameCode) {
        return games.containsKey(gameCode);
    }

}
