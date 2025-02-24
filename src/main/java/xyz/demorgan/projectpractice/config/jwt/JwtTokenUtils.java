package xyz.demorgan.projectpractice.config.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import xyz.demorgan.projectpractice.store.entity.Company;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;

@Component
public class JwtTokenUtils {

    private final Duration lifetime = Duration.ofDays(30);
    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    public String generateToken(Company company) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("role", company.getRoles());
        claims.put("id", company.getId());
        claims.put("email", company.getEmail());
        claims.put("name", company.getName());
        claims.put("is_student_company", company.isStudentCompany());

        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + lifetime.toMillis());

        return Jwts.builder()
                .subject(company.getEmail())
                .issuer("project-practice")
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            throw new JwtException("Problem while getting email from token");
        }
        return claims.getSubject();
    }

    public Integer getIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            throw new JwtException("Invalid token");
        }
        return claims.get("id", Integer.class);
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            throw new JwtException("Invalid token");
        }


        List<?> rolesList = claims.get("roles", List.class);

        if (rolesList == null) {
            return Collections.emptyList();
        }

        List<String> roles = new ArrayList<>();
        for (Object roleObj : rolesList) {
            if (roleObj instanceof String) {
                roles.add((String) roleObj);
            } else {
                throw new JwtException("Role is not a String");
            }
        }

        return roles;
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            return null;
        }
    }
}
