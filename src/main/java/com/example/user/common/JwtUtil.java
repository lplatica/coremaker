package com.example.user.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "a55e17a5ab90722c5b3a13982faa38a4631222f8b411ec7d10bc6b2861964dcc8bfc2401d45310805ab8a6d9a04420afa6f91e97b044406902a4a7d4f3d72eb9f82fec2418da01322dfee1260300c6503a60b322d6d6384b20b854d603d445f35957a8fb5d127c1eb5bd6d33e5ed9477e2c716b7bbc1f45673732d43d8d628c712409685895163bc3d8c59c9ea56b148da108524feb4a618c2c85b74cce63bc28e130a2343f86d1c5c7e747cff55ddcbb86e7cb3f4014247d98515d72412654af37b98c4233e19182aa77724f2317284f172e35f09cdff5792209fc99145ee3a71505f6440f52fca9f9fd8527c8cd7144fb1a5b7621189fa0de9e4d30e999b84";

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
