package at.fhv.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class MapBoundsController {
    @GetMapping("/mapBounds")
    public ResponseEntity<String> getMapBounds(){
        try{
            String mapBoundsJson = new String(Files.readAllBytes(Paths.get("mapBounds.json")));
            return ResponseEntity.ok(mapBoundsJson);
        }catch(IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading mapBounds.json");
        }
    }
}
