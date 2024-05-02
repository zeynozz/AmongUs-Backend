package at.fhv.backend.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

@RequiredArgsConstructor
public class PlayerMovement {

    private static final int PLAYER_SPRITE_WIDTH = 84;
    private static final int PLAYER_SPRITE_HEIGHT = 128;
    private static final int PLAYER_HEIGHT = 50;
    private static final int PLAYER_WIDTH = 37;
    private static final int PLAYER_START_X = 330;
    private static final int PLAYER_START_Y = 100;
    private static final int PLAYER_SPEED = 15;
    private static final int SHIP_WIDTH = 2160;
    private static final int SHIP_HEIGHT = 1166;

    private static List<List<Integer>> mapBounds;
    private final ResourceLoader resourceLoader;


    @PostConstruct
    public void init() throws Exception{
        Resource resource = resourceLoader.getResource("classpath:mapBounds.json");
        ObjectMapper objectMapper = new ObjectMapper();
        mapBounds = objectMapper.readValue(resource.getInputStream(), new TypeReference<>(){
        });
    }

    public static boolean movePlayers(String[] keys, player player) {
        boolean playerMoved = false;
        int absPlayerX = player.getPosition().getX() + SHIP_WIDTH / 2;
        int absPlayerY = player.getPosition().getY() + SHIP_HEIGHT / 2 + 20;



        if (contains(keys, "ArrowUp") &&
                isWithinMovementBoundaries(absPlayerX, absPlayerY - PLAYER_SPEED)) {
            playerMoved = true;
            player.setPosition(player.getPosition().getY() - PLAYER_SPEED);
        }
        if (contains(keys, "ArrowDown") &&
                isWithinMovementBoundaries(absPlayerX, absPlayerY + PLAYER_SPEED)) {
            playerMoved = true;
            player.setPosition(player.getPosition().getY() + PLAYER_SPEED);
        }
        if (contains(keys, "ArrowLeft") &&
                isWithinMovementBoundaries(absPlayerX - PLAYER_SPEED, absPlayerY)) {
            playerMoved = true;
            player.setPosition(player.getPosition().getX() - PLAYER_SPEED);
        }
        if (contains(keys, "ArrowRight") &&
                isWithinMovementBoundaries(absPlayerX + PLAYER_SPEED, absPlayerY)) {
            playerMoved = true;
            player.setPosition(player.getPosition().getX() + PLAYER_SPEED);
        }

        return playerMoved;
    }

    private static boolean isWithinMovementBoundaries(int x, int y) {
        if (mapBounds == null || mapBounds.size() <= y || mapBounds.get(y) == null) {
            return true;
        }
        return !mapBounds.get(y).contains(x);
    }

    private static boolean contains(String[] array, String target) {
        for (String s : array) {
            if (s.equals(target)) {
                return true;
            }
        }
        return false;
    }
}



