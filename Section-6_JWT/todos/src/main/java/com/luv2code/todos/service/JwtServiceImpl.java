package com.luv2code.todos.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService{

    @Value("${spring.jwt.secret}")
    private String SECRET_KEY;

    @Value("${spring.jwt.expiration}")
    private long JWT_EXPIRATION;

    // =============================== TOKEN PARSING ===============================

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()

                // Use the secret key to verify the token.
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // =============================== TOKEN VALIDATION ===============================

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokeExpired(token) ;
    }

    private boolean isTokeExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // =============================== TOKEN GENERATION ===============================

    @Override
    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                // Claim: This is additional data that you want to include in the token.
                .claims(claims)

                // Subject: Token Owner
                .subject(userDetails.getUsername())

                // IssuedAt: When were the tokens created?
                .issuedAt(new Date(System.currentTimeMillis()))

                // Expiration: When were the tokens expiration (15m)
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))


                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    // Create SecretKey
    // Include:
    //      + Header (Algorithm)
    //      + Payload (Data)
    //      + Signature (Sign)
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
