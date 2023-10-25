package by.zhukovsky.expensestracker.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class JWTUtil {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    @Value("${secret.jwt.secretKey}")
    private String secretKey;
    @Value("${secret.jwt.issuer}")
    private String issuer;
    @Value("${secret.jwt.tokenValidityDays}")
    private int tokenValidityDays;

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, List<String> scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, Map<String, Object> claims) {
        String token = buildToken(subject, claims);
        logger.info("JWT token issued for subject: " + subject);
        return token;
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String jwt, String username) {
        Claims claims = getClaims(jwt);
        return username.equals(claims.getSubject()) && !isTokenExpired(claims);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private boolean isTokenExpired(Claims claims) {
        Date expirationTime = claims.getExpiration();
        return expirationTime.before(Date.from(Instant.now()));
    }

    private String buildToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(tokenValidityDays, DAYS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
