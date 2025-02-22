package xyz.demorgan.projectpractice.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.demorgan.projectpractice.store.entity.Company;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;

@Component
public class JwtTokenUtils {

    private final Duration lifetime = Duration.ofMinutes(30);
    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    public String generateToken(Company company) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", "ROLE_COMPANY");
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
        return Objects.requireNonNull(getAllClaimsFromToken(token)).getSubject();
    }

    public String getIdFromToken(String token) {
        return Objects.requireNonNull(getAllClaimsFromToken(token)).get("id", String.class);
    }

    public List<String> getRolesFromToken(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }

    private Claims getAllClaimsFromToken(String token) {
        try {return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            return null;
        }
    }
}
