package at.fhv.backend.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MapLoader {
    private static final String pathForMap = "src/main/java/at/fhv/backend/maps/";

    public static int[][] loadMapFromFile(String map) {
        int[][] mapCells = null;
        String newPathForMap = pathForMap + map.toLowerCase() + ".txt";
        int rows = 0;
        int cols = 0;

        try (Scanner scanner = new Scanner(new File(newPathForMap))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                rows++;
                cols = Math.max(cols, line.length());
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + newPathForMap);
            e.printStackTrace();
            return null;
        }

        mapCells = new int[rows][cols];

        try (Scanner scanner = new Scanner(new File(newPathForMap))) {
            int row = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                for (int col = 0; col < line.length(); col++) {
                    char ch = line.charAt(col);
                    switch (ch) {
                        case '+':
                            mapCells[row][col] = 1;
                            break;
                        case '#':
                            mapCells[row][col] = 2;
                            break;
                        case '-':
                            mapCells[row][col] = 0;
                            break;
                        default:
                            mapCells[row][col] = 0;
                            break;
                    }
                }
                row++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Failed to open the file on the second read: " + newPathForMap);
            e.printStackTrace();
        }

        return mapCells;
    }

    public static void main(String[] args) {
        int[][] map = loadMapFromFile("exampleMap");
        if (map != null) {
            for (int[] row : map) {
                for (int cell : row) {
                    if (cell == 1) {
                        System.out.print('+');
                    } else if (cell == 2) {
                        System.out.print('#');
                    } else {
                        System.out.print('-');
                    }
                }
                System.out.println();
            }
        }
    }
}
