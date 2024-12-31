package vn.khanhduc.shoppingbackendservice.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.khanhduc.shoppingbackendservice.entity.User;
import vn.khanhduc.shoppingbackendservice.exception.AppException;
import vn.khanhduc.shoppingbackendservice.exception.ErrorCode;
import vn.khanhduc.shoppingbackendservice.service.JwtService;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @Override
    public String generateAccessToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("tiki")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(2, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    @Override
    public String generateRefreshToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("tiki")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(14, ChronoUnit.DAYS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();
        var payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    @Override
    public boolean verifyToken(String token, UserDetails userDetails) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        if(signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date())){
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        String email = signedJWT.getJWTClaimsSet().getSubject();
        if(StringUtils.isEmpty(email) ||  !Objects.equals(email, userDetails.getUsername())){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        try {
            return signedJWT.verify(new MACVerifier(SIGNER_KEY));
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public String extractEmail(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
    }

    @Override
    public Date extractExpiryTime(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getExpirationTime();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    @Override
    public <R> R extractClaim(String token, Function<JWTClaimsSet, R> claimsResolver) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return claimsResolver.apply(signedJWT.getJWTClaimsSet());
        } catch (ParseException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

}
