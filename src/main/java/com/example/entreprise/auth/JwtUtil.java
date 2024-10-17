package com.example.entreprise.auth;

import com.example.entreprise.entities.Entreprise;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;
import com.example.entreprise.entities.Role;


import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final String secret_key = "mysecretkeyisnotwhatyouthinkaboutbrodontworryaboutithahahahahahahahahahahahahaha";
    private final long accessTokenValidity = 10 * 60 * 60 * 1000;
    private final JwtParser jwtParser;
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";


    public JwtUtil() {
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }

    public String createToken1(UserDetails userDetails, Entreprise entreprise) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("nom", entreprise.getNom());
        claims.put("adresse", entreprise.getAdresse());
        claims.put("logo", entreprise.getLogo());
        claims.put("email", entreprise.getEmail());
        claims.put("phone", entreprise.getPhone());

        // Extraire les noms des rôles de l'entreprise
        List<String> roleNames = entreprise.getRoles()
                .stream()
                .map(Role::getName) // Assurez-vous que Role a un getName() pour obtenir le nom du rôle
                .collect(Collectors.toList());

        claims.put("roles", roleNames); // Ajoute la liste des noms de rôles aux claims

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }


    public String createPasswordToken(UserDetails user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.setAudience("password");
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MILLISECONDS.toMillis(1000*60*30));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }


    public boolean isPasswordToken(String token){
        return Objects.equals(parseJwtClaims(token).getAudience(), "password");
    }


    public String getMatriculeFromToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException{
        Claims c = parseJwtClaims(token);
        return c.getSubject();
    }


    public String getEmailFromToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException{
        Claims c = parseJwtClaims(token);
        return c.getSubject();
    }


    private Claims parseJwtClaims(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return jwtParser.parseClaimsJws(token).getBody();
    }


    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }


    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }


    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }


}