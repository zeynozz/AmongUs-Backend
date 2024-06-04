package at.fhv.backend.model;

import lombok.Getter;
import lombok.Setter;
public class VoteMessage {

    @Getter
    @Setter
    private String gameCode;
    @Getter
    @Setter
    private String votedPlayer;

    public VoteMessage() {
    }

    public VoteMessage(String gameCode, String votedPlayer) {
        this.gameCode = gameCode;
        this.votedPlayer = votedPlayer;
    }
}

