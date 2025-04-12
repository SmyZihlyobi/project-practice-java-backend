package xyz.demorgan.projectpractice.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import xyz.demorgan.projectpractice.exceptions.JwtValidationException;
import xyz.demorgan.projectpractice.store.entity.Company;
import xyz.demorgan.projectpractice.store.entity.Student;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;

@Component
public class JwtTokenUtils {

    private final Duration lifetime = Duration.ofDays(30);
    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    public String generateStudentToken(Student student, boolean rememberMe) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", student.getRoles());
        claims.put("username", student.getUsername());

        Date issuedAt = new Date();
        Date expiration;
        if (rememberMe) {
            expiration = new Date(issuedAt.getTime() + lifetime.toMillis());
        } else {
            expiration = new Date(issuedAt.getTime() + Duration.ofMinutes(15L).toMillis());
        }

        return Jwts.builder()
                .subject(student.getUsername())
                .issuer("project-practice")
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String generateCompanyToken(Company company) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", company.getRoles());
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
            throw new JwtValidationException("Claims is null");
        }
        return claims.getSubject();
    }

    public Integer getIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            throw new JwtValidationException("Claims is null");
        }
        return claims.get("id", Integer.class);
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            throw new JwtValidationException("Claims is null");
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
                throw new JwtValidationException("Role is not a String");
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
            throw new JwtValidationException("Invalid token", e);
        }
    }
}
