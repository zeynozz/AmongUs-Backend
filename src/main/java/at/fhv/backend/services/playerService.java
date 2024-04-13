package at.fhv.backend.services;

import at.fhv.backend.model.game;
import at.fhv.backend.model.player;
import at.fhv.backend.model.position;
import at.fhv.backend.generators.playerRoleGenerator;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class playerService {

    public player createPlayer(String username, position position, game game) {
        return new player(username, position, game);
    }

    public player setInitialRandomRole(int numPlayers, int numImpostors, player player) {
        List<Integer> impostorsIndices = playerRoleGenerator.generatePlayerRole(numPlayers, numImpostors);
        if (impostorsIndices.contains(0)) {
            player.setRole("Impostor");
        }
        return player;
    }

    public List<player> setRandomRole(List<player> players) {
        List<Integer> impostorsIndices = playerRoleGenerator.getImpostorsIndices();
        for (int i = 0; i < players.size(); i++) {
            if (impostorsIndices.contains(i)) {
                players.get(i).setRole("Impostor");
            }
        }
        return players;
    }
}
