package at.fhv.backend.model.com;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class moveCom {
    private int id;
    private String keyCode;
    private String gameCode;

    public moveCom(int id, String keyCode, String gameCode) {
        this.id = id;
        this.keyCode = keyCode;
        this.gameCode = gameCode;
    }
}