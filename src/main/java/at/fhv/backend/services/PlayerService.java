package at.fhv.backend.services;

import at.fhv.backend.model.Game;
import at.fhv.backend.model.Player;
import at.fhv.backend.model.Position;
import at.fhv.backend.generators.RoleGenerator;
import at.fhv.backend.model.Status;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PlayerService {

    private final MapService mapService;

    public PlayerService(MapService mapservice) {
        this.mapService = mapservice;
    }

    public Player createPlayer(String username, Position position, Game game, String color) {
        if (!mapService.isCellWalkable(position.getX(), position.getY())) {
            position = findWalkablePosition();
        }
        return new Player(username, position, game, color, Status.ALIVE);
    }

    private Position findWalkablePosition() {
        int[][] map = mapService.getMap();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == 1) {
                    return new Position(x, y);
                }
            }
        }
        return new Position(0, 0);
    }


    public Position calculateNewPosition(Position currentPosition, String keyCode) {
        int deltaX = 0, deltaY = 0;
        switch (keyCode) {
            case "KeyA":
                deltaX = -1;
                break;
            case "KeyW":
                deltaY = -1;
                break;
            case "KeyD":
                deltaX = 1;
                break;
            case "KeyS":
                deltaY = 1;
                break;
            default:
                System.out.println("Invalid key code");
                break;
        }
        return new Position(currentPosition.getX() + deltaX, currentPosition.getY() + deltaY);
    }

    public Player setInitialRandomRole(int numPlayers, int numImpostors, Player player) {
        List<Integer> impostorsIndices = RoleGenerator.generatePlayerRole(numPlayers, numImpostors);
        System.out.println("Impostorsindices : " + impostorsIndices);
        if (impostorsIndices.contains(player.getId())) {
            player.setRole("Impostor");
        }
        return player;
    }

    public List<Player> setRandomRole(List<Player> Players) {
        List<Integer> impostorsIndices = RoleGenerator.getImpostorsIndices();
        for (int i = 0; i < Players.size(); i++) {
            if (impostorsIndices.contains(i)) {
                Players.get(i).setRole("IMPOSTOR");
            } else {
                Players.get(i).setRole("CREWMATE");
            }
        }
        return Players;
    }

    public Position teleportToVent(Position currentPosition, int[][] map) {
        List<Position> vents = new ArrayList<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == 18) {
                    vents.add(new Position(x, y));
                }
            }
        }
        if (vents.size() > 1) {
            vents.removeIf(vent -> vent.equals(currentPosition));
            return vents.get(new Random().nextInt(vents.size()));
        }
        return currentPosition;
    }

    public void updatePlayerPosition(Player player, Position newPosition) {
        int x = newPosition.getX();
        int y = newPosition.getY();
        boolean outOfBounds =
                (x < 0) ||
                        (y < 0) ||
                        (y >= mapService.getMap().length) ||
                        (x >= mapService.getMap()[0].length);

        if (!outOfBounds && mapService.isCellWalkable(x, y)) {
            if (player.getStatus() != Status.DEAD) {
                player.setPosition(newPosition);
            }
        }
    }

}
