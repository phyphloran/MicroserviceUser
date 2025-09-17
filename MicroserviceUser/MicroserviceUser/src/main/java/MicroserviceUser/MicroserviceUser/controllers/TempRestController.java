package MicroserviceUser.MicroserviceUser.controllers;


import MicroserviceUser.MicroserviceUser.Security.Jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class TempRestController {

    @Autowired
    private JwtService jwtService;

    @GetMapping("/test")
    public ResponseEntity<?> testToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (jwtService.validateJwtDate(token)) {
            return ResponseEntity.ok(Map.of("response", "test - ok"));
        } else {
            return ResponseEntity.status(403).body("Invalid or expired token");
        }
    }
}