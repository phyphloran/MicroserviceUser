package MicroserviceUser.MicroserviceUser.Security.Jwt;


import MicroserviceUser.MicroserviceUser.Dto.JwtAuthDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Component
public class JwtService {

    private static final Logger LOGGER = LogManager.getLogger(JwtService.class);

    private String jwtSecret = "saergvsrtghdrUJHKGVuyoaeuryhfbuoaeyrfbuyawergfauergf6u34gruhvbHGFVIYVth456JYGVYi65";

    public JwtAuthDto generateAuthToken(String email) {
        JwtAuthDto jwtAuthDto = new JwtAuthDto();
        jwtAuthDto.setToken(generateJwtToken(email));
        jwtAuthDto.setRefreshToken(generateRefreshToken(email));
        return jwtAuthDto;
    }

    public JwtAuthDto refreshBaseToken(String email, String refreshToken) {
        JwtAuthDto jwtAuthDto = new JwtAuthDto();
        jwtAuthDto.setToken(generateJwtToken(email));
        jwtAuthDto.setRefreshToken(refreshToken);
        return jwtAuthDto;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (ExpiredJwtException jwtException) {
            LOGGER.error("Expired JwtException", jwtException);
        } catch (UnsupportedJwtException jwtException) {
            LOGGER.error("Unsupported JwtException", jwtException);
        } catch (MalformedJwtException jwtException) {
            LOGGER.error("Malformed JwtException", jwtException);
        } catch (SecurityException jwtException) {
            LOGGER.error("Security Exception", jwtException);
        } catch (Exception exception) {
            LOGGER.error("Exception", exception);
        }
        return false;
    }

    private String generateJwtToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private String generateRefreshToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
