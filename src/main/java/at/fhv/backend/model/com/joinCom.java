package at.fhv.backend.model.com;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class joinCom {
    private String username;
    private at.fhv.backend.model.position position;
    private String gameCode;

    public joinCom() {
    }

    public joinCom(String username, at.fhv.backend.model.position position) {
        this.username = username;
        this.position = position;
    }

}