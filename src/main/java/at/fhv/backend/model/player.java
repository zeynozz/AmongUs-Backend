package at.fhv.backend.model;

public class player {
    private static int idCounter = 1;
    private int id;
    private String username;
    private position position;
    private game game;
    private String role;

    public player(String username, position position, game game) {
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

    public void setPosition(int position) {
        this.position = position;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public at.fhv.backend.model.game getGame() {
        return game;
    }

    public void setGame(at.fhv.backend.model.game game) {
        this.game = game;
    }
}

