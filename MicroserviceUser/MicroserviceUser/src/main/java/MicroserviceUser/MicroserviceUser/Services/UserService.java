package MicroserviceUser.MicroserviceUser.Services;


import MicroserviceUser.MicroserviceUser.Dto.JwtAuthDto;
import MicroserviceUser.MicroserviceUser.Role;
import MicroserviceUser.MicroserviceUser.Dto.RefreshTokenDto;
import MicroserviceUser.MicroserviceUser.Dto.UserCredentialsDto;
import MicroserviceUser.MicroserviceUser.Dto.UserDto;
import MicroserviceUser.MicroserviceUser.Repositories.UserRepository;
import MicroserviceUser.MicroserviceUser.Security.Jwt.JwtService;
import MicroserviceUser.MicroserviceUser.models.User;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class UserService implements UserDetailsService  {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        System.out.println("Loaded user: " + user.getUsername() + " with role: " + user.getRole());
        return user;
    }

    public String addUser(UserDto userDto){
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setVerifiedEmail(false);
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
        return "User added";
    }

    public JwtAuthDto singIn(UserCredentialsDto userCredentialsDto) throws Exception {
        User user = findByCredentials(userCredentialsDto);
        return jwtService.generateAuthToken(user.getEmail());
    }

    public JwtAuthDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        throw new JwtException("Invalid refresh token");
    }

    private User findByCredentials(UserCredentialsDto userCredentialsDto) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(userCredentialsDto.getEmail());
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (passwordEncoder.matches(userCredentialsDto.getPassword(), user.getPassword())){
                return user;
            }
        }
        throw new Exception("Email or password is not correct");
    }

    private User findByEmail(String email) throws Exception {
        return userRepository.findByEmail(email).orElseThrow(()->
                new Exception(String.format("User with email %s not found", email)));
    }


}
