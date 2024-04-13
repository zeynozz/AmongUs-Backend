package at.fhv.backend.model;

public class player {
    private static int idCounter = 1;
    private int id;
    private String username;
    private at.fhv.backend.model.position position;
    private at.fhv.backend.model.game game;
    private String role;

    public player(String username, at.fhv.backend.model.position position, at.fhv.backend.model.game game) {
        this.id = idCounter++;
        this.username = username;
        this.position = position;
        this.game = game;
        this.role = "Crewmate";
    }

    public player() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public at.fhv.backend.model.position getPosition() {
        return position;
    }

    public void setPosition(at.fhv.backend.model.position position) {
        this.position = position;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
