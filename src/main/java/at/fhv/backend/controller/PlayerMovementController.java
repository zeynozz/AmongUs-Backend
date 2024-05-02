package at.fhv.backend.controller;

import at.fhv.backend.model.PlayerMovement;
import at.fhv.backend.model.player;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerMovementController {

    @PostMapping("/api/movePlayers")
    public boolean movePlayers(@RequestBody String[] keys, player player){
        return PlayerMovement.movePlayers(keys, player);
    }
}
