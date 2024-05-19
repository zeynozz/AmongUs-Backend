package at.fhv.backend.model;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Getter
@Setter
public class Player {
    private static int idCounter = 1;
    private int id;
    private String username;
    private Position position;

    @JsonBackReference
    private Game game;
    private String role;
    private boolean isAlive;

    public Player(String username, Position position, Game game) {
        this.id = idCounter++;
        this.username = username;
        this.position = position;
        this.game = game;
        this.role = "Crewmate";
        this.isAlive = true;
    }

    public Player() {
        this.isAlive = true;
    }

    public boolean isNearby(Player other, int proximity) {
        // Assuming Position class has getX() and getY() methods
        return Math.abs(this.position.getX() - other.position.getX()) <= proximity &&
                Math.abs(this.position.getY() - other.position.getY()) <= proximity;
    }

    public void kill() {
        this.isAlive = false;
    }
}
