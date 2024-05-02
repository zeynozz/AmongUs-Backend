package at.fhv.backend.model;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    @MessageMapping("/movePlayer")
    @SendTo("/topic/movePlayer")
    public Move movePlayer(Move move) {
        return move;
    }

}
