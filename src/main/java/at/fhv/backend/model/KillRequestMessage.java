package at.fhv.backend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KillRequestMessage {
    private int playerId;
    private String gameCode;
    private int targetId; // Hier fügen wir die Ziel-ID hinzu

}

