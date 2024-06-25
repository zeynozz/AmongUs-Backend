package at.fhv.playerservice.controller;

import at.fhv.common.services.GameService;
import at.fhv.common.services.PlayerService;
import org.springframework.stereotype.Controller;

@Controller
public class PlayerController {

    private final PlayerService playerService;
    private final GameService gameService;

    public PlayerController(PlayerService playerService, GameService gameService) {
        this.playerService = playerService;
        this.gameService = gameService;
    }
}