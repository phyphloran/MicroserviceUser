package MicroserviceUser.MicroserviceUser.controllers;


import MicroserviceUser.MicroserviceUser.Dto.JwtAuthDto;
import MicroserviceUser.MicroserviceUser.Dto.UserDto;
import MicroserviceUser.MicroserviceUser.Dto.RefreshTokenDto;
import MicroserviceUser.MicroserviceUser.Dto.UserCredentialsDto;
import MicroserviceUser.MicroserviceUser.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.naming.AuthenticationException;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/sing-in")
    public ResponseEntity<JwtAuthDto> singIn(@RequestBody UserCredentialsDto userCredentialsDto) {
        try {
            JwtAuthDto jwtAuthenticationDto = userService.singIn(userCredentialsDto);
            return ResponseEntity.ok(jwtAuthenticationDto);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/refresh")
    public JwtAuthDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return userService.refreshToken(refreshTokenDto);
    }

    @PostMapping("/registration")
    public String createUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }
}