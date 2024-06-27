package at.fhv.common.model.com;

import at.fhv.common.model.Position;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinCom {
    private String username;
    private Position position;
    private String gameCode;
    private String color;

    public JoinCom() {
    }

    public JoinCom(String username, Position position) {
        this.username = username;
        this.position = position;
    }

}
