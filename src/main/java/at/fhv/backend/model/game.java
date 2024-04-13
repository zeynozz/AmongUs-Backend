package at.fhv.backend.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class game {
    private String gameCode;
    private int numberOfPlayers;
    private int numberOfImpostors;
    private List<player> players;
    private int gameID = 0;

    public game(String gameCode, int numberOfPlayers, int numberOfImpostors, String map, List<player> players) {
        this.gameCode = gameCode;
        this.numberOfPlayers = numberOfPlayers;
        this.numberOfImpostors = numberOfImpostors;
        this.players = players;
        setGameID();
    }

    public game(String gameCode, int numberOfPlayers, int numberOfImpostors) {
        this(gameCode, numberOfPlayers, numberOfImpostors, null, new ArrayList<>());
    }

    public game() {
    }

    private void setGameID() {
        this.gameID = gameID + 1;
    }

}
