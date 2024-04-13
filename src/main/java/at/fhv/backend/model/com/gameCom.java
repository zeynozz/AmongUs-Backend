package at.fhv.backend.model.com;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class gameCom {
    private at.fhv.backend.model.player player;
    private String numberOfPlayers;
    private String numberOfImpostors;

    public gameCom(at.fhv.backend.model.player player, String numberOfPlayers, String numberOfImpostors, String map) {
        this.player = player;
        this.numberOfPlayers = numberOfPlayers;
        this.numberOfImpostors = numberOfImpostors;
    }

    public gameCom() {}
}
