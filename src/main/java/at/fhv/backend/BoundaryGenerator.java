package at.fhv.backend;

import com.google.gson.Gson;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoundaryGenerator {
    public static void main(String[] args) throws IOException {
        final String filePath = "./src/main/java/at/fhv/backend/LobbyMask.png";
        final int boundaryColor = 0x00B44B;

        File file = new File(filePath);
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();

        List<List<Integer>> result = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            List<Integer> row = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                int color = image.getRGB(x, y) & 0x00FFFFFF; // Ignore the alpha channel
                if (color == boundaryColor) {
                    row.add(x);
                }
            }
            if (!row.isEmpty()) {
                result.add(row);
            }
        }

        String jsonResult = new Gson().toJson(result);
        Path path = Paths.get("mapBounds.json"); // Adjust the output file path
        Files.write(path, Collections.singleton(jsonResult), StandardCharsets.UTF_8);
    }
}
