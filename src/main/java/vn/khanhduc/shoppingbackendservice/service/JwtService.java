package vn.khanhduc.shoppingbackendservice.service;

import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.core.userdetails.UserDetails;
import vn.khanhduc.shoppingbackendservice.entity.User;
import java.text.ParseException;
import java.util.Date;
import java.util.function.Function;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    boolean verifyToken(String token, UserDetails userDetails) throws ParseException;

    String extractEmail(String token);

    Date extractExpiryTime(String token);

    <R> R extractClaim(String token, Function<JWTClaimsSet, R> claimsResolver);
}
