package at.fhv.backend.controller;

import at.fhv.backend.model.Game;
import at.fhv.backend.model.VoteMessage;
import at.fhv.backend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class VotingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GameService gameService;

    private Map<String, Map<String, Integer>> votes = new HashMap<>();

    @MessageMapping("/castVote")
    public void castVote(@Payload VoteMessage voteMessage) {
        String gameCode = voteMessage.getGameCode();
        String votedPlayer = voteMessage.getVotedPlayer();

        votes.putIfAbsent(gameCode, new HashMap<>());
        Map<String, Integer> gameVotes = votes.get(gameCode);
        gameVotes.put(votedPlayer, gameVotes.getOrDefault(votedPlayer, 0) + 1);

        String playerToRemove = determinePlayerToRemove(gameVotes);

        if (playerToRemove != null) {
            gameService.removePlayer(gameCode, playerToRemove);
            votes.remove(gameCode);
            messagingTemplate.convertAndSend("/topic/" + gameCode + "/votingResults", playerToRemove);
        }
    }

    private String determinePlayerToRemove(Map<String, Integer> gameVotes) {
        return gameVotes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
