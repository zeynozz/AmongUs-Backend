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
                        case 'T':
                            mapCells[row][col] = 3;
                            break;
                        case 'A':
                            mapCells[row][col] = 4;
                            break;
                        case 'B':
                            mapCells[row][col] = 5;
                            break;
                        case 'L':
                            mapCells[row][col] = 6;
                            break;
                        case 'W':
                            mapCells[row][col] = 7;
                            break;
                        case 'C':
                            mapCells[row][col] = 8;
                            break;
                        case 'G':
                            mapCells[row][col] = 9;
                            break;
                        case 'O':
                            mapCells[row][col] = 10;
                            break;
                        case 'E':
                            mapCells[row][col] = 11;
                            break;
                        case 'D':
                            mapCells[row][col] = 12;
                            break;
                        case 'V':
                            mapCells[row][col] = 13;
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
                    } else if (cell == 3) {
                        System.out.print('T');
                    }
                    else {
                        System.out.print('-');
                    }
                }
                System.out.println();
            }
        }
    }
}
