package MicroserviceUser.MicroserviceUser.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class TempRestController {

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> getTest() {
        return ResponseEntity.ok(Map.of("response", "ok"));
    }

}