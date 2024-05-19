package at.fhv.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;

import java.util.Random;

public class Player {
    private static int idCounter = 1;

    private static final String[] COLORS = {"white", "green", "blue", "pink", "purple"};

    private static final Random RANDOM = new Random();
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

    public Player(String username, Position position, Game game) {
        this.id = idCounter++;
        this.username = username;
        this.position = position;
        this.game = game;
        this.role = "Crewmate";
        this.color = COLORS[RANDOM.nextInt(COLORS.length)];
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

    public void setColor(String color) {
        this.color = color;
    }

    public Game getGame() {
        return this.game;
    }
}
