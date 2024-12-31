package vn.khanhduc.shoppingbackendservice.configuration;

import com.nimbusds.jose.JWSAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import vn.khanhduc.shoppingbackendservice.exception.AppException;
import vn.khanhduc.shoppingbackendservice.exception.ErrorCode;
import vn.khanhduc.shoppingbackendservice.repository.UserRepository;
import vn.khanhduc.shoppingbackendservice.service.JwtService;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    String signerKey;

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private NimbusJwtDecoder jwtDecoder;

    @Override
    public Jwt decode(String token){
        if(Objects.isNull(jwtDecoder)) {
            SecretKey secretKey = new SecretKeySpec(signerKey.getBytes(), JWSAlgorithm.HS512.toString());
            jwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKey)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        String email = jwtService.extractEmail(token);
        UserDetails userDetails = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        try {
            boolean isValidToken = jwtService.verifyToken(token, userDetails);
            if (isValidToken)
                return jwtDecoder.decode(token);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        throw new JwtException("Invalid token");
    }
}
