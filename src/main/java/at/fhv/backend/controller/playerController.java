package at.fhv.backend.controller;

import org.springframework.stereotype.Controller;

@Controller
public class playerController {

    private final at.fhv.backend.services.playerService playerService;
    private final at.fhv.backend.services.gameService gameService;

    public playerController(at.fhv.backend.services.playerService playerService, at.fhv.backend.services.gameService gameService) {
        this.playerService = playerService;
        this.gameService = gameService;
    }
}