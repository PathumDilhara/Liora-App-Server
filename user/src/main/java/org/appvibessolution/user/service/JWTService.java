package org.appvibessolution.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Long jwtExpiration;


    // Generate token without any data/claims
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails); // it’s not calling itself — it’s calling a different version (with a different parameter list).
    }

    //  add extra information into the JWT — like roles, user ID, or anything else
    //  for userDetails - usually pass a custom class like AppUser that implements by UserDetails
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return buildToken(extraClaims, userDetails);
    }

    // Generate token using userName/email
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts.builder()
                .header().add("typ", "JWT").and()
                .claims()
                .add(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .and()
                .signWith(getKey())
                .compact();
    }

    // get the secret key
    private SecretKey getKey(){
        byte[] keyByte = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyByte);
    }

    // Extract all claims form token
    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // extract particular data from the token
    private <T> T extractParticularClaim(String token, Function<Claims, T> claimsTFunction){
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    // Use email as userName
    public String extractUserName(String token){
        return extractParticularClaim(token, Claims::getSubject);
    }

    // Check token is valid or not
    public boolean validateToken(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Check token expiration
    public boolean isTokenExpired(String token){
       return extractExpiration(token).before(new Date());
    }

    // Get the expiration in token
    public Date extractExpiration(String token){
        return extractParticularClaim(token, Claims::getExpiration);
    }
}
