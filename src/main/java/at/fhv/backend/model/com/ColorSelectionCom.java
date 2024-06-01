package at.fhv.backend.model.com;

import lombok.Getter;

public class ColorSelectionCom {

    @Getter
    private int gameCode;
    @Getter
    private String color;
    @Getter
    private int playerId;

    public ColorSelectionCom(int gameCode, String color) {
        this.gameCode = gameCode;
        this.color = color;
    }
}
