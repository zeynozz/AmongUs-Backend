package at.fhv.backend.model;

import lombok.Getter;

public class Player {
    private static int idCounter = 1;
    @Getter
    private int id;
    @Getter
    private String username;
    @Getter
    private Position position;
    private Game game;
    @Getter
    private String role;

    public Player(String username, Position position, Game game) {
        this.id = idCounter++;
        this.username = username;
        this.position = position;
        this.game = game;
        this.role = "Crewmate";
    }

    public Player() {
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
}
