package at.fhv.backend.services;

import org.springframework.stereotype.Service;
import at.fhv.backend.model.Map;

@Service
public class MapService {

    private Map map;

    public MapService() {
    }

    public MapService(String mapName) {
        getInitialMap(mapName);
    }

    public int[][] getMap(){
        return map.getMap();
    }

    public Map getInitialMap(String mapName) {
        if (map == null) {
            map = new Map();
            map.setInitialMap(mapName);
        }
        return map;
    }

    public void setMap(int[][] mapArray) {
        map.setMap(mapArray);
    }

    public void setMapbyPosition(int x, int y, int value) {
        map.setMapbyPosition(x, y, value);
    }

    public boolean isCellWalkable(int x, int y) {
        return map.getCellValue(x, y) == 1;
    }

    public static void main(String[] args) {
    }
}
