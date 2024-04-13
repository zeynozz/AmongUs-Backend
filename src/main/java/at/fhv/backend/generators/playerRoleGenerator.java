package at.fhv.backend.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class playerRoleGenerator {
    public static List<Integer> impostors = new ArrayList<>();

    public static List<Integer> generatePlayerRole(int numPlayers, int numImpostors) {
        impostors = new ArrayList<>();
        Random random = new Random();

        while (impostors.size() < numImpostors) {
            int randomIndex = random.nextInt(numPlayers);
            if (!impostors.contains(randomIndex)) {
                impostors.add(randomIndex);
            }
        }
        return impostors;
    }

    public static List<Integer> getImpostorsIndices() {
        return impostors;
    }

}