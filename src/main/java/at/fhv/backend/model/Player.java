package at.fhv.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

public class Player {
    private static int idCounter = 1;

    @Getter
    private int id;
    @Getter
    private String username;
    @Getter
    private Position position;
    @JsonBackReference
    private Game game;
    @Getter
    private String role;
    @Getter
    private String color;
    @Getter
    @Setter
    private Status status;
    @Getter
    @Setter
    private String chosenColor;

    public Player(String username, Position position, Game game, String color, Status status) {
        this.id = idCounter++;
        this.username = username;
        this.position = position;
        this.game = game;
        this.role = "Crewmate";
        this.color = color;
        this.status = status.ALIVE;

    }



    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public Game getGame() {
        return this.game;
    }
}
