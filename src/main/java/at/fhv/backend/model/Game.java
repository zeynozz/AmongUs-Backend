package at.fhv.backend.model;

import lombok.Getter;
import lombok.Setter;
import at.fhv.backend.services.MapService;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
public class Game {
    private String gameCode;
    private int numberOfPlayers;
    private int numberOfImpostors;
    private Map map;

    @JsonManagedReference
    private List<Player> players;
    private int gameID = 0;

    public Game(String gameCode, int numberOfPlayers, int numberOfImpostors, String map, List<Player> players) throws FileNotFoundException {
        this.gameCode = gameCode;
        this.numberOfPlayers = numberOfPlayers;
        this.numberOfImpostors = numberOfImpostors;
        this.map = new Map();
        this.map.setInitialMap(map);
        System.out.println("Map: " + Arrays.toString(this.map.getMap()[0]));
        this.players = players;
        setGameID();
    }

    public Game(String gameCode, int numberOfPlayers, int numberOfImpostors, String map, MapService mapService) throws FileNotFoundException {
        this(gameCode, numberOfPlayers, numberOfImpostors, map, new ArrayList<>());
        mapService.setMap(mapService.getInitialMap(map).getMap());
        setGameID();
    }

    public static Player findNearbyCrewmate(Player impostor, List<Player> players, int proximity){
        for (Player player : players) {
            if (player.isAlive() && !player.getRole().equals("Impostor") && impostor.isNearby(player, proximity)) {
                return player;
            }
        }
        return null;
    }

    public Game() {
        this.map = new Map();
    }

    private void setGameID() {
        this.gameID = gameID + 1;
    }

    public int[][] getMap() {
        return map.getMap();
    }

    public void setMap(int[][] map) {
        this.map.setMap(map);
    }
}
