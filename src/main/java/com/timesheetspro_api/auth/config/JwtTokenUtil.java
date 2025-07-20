package com.timesheetspro_api.auth.config;

import com.timesheetspro_api.common.model.users.Users;
import com.timesheetspro_api.common.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${app.JWT.SECRET_KEY}")
    public String SECRET_KEY;

    @Autowired
    private UserRepository userRepository;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        return Long.parseLong(claims.get("userId").toString());
    }

    public String extractMemberRole(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("role").toString();
    }

    public String extractCompanyId(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("companyId").toString();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(Map<String, Object> user) {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("userId", user.get("userId").toString());
        claims.put("roleId", user.get("roleId").toString());
        claims.put("roleName", user.get("roleName").toString());
        claims.put("companyId", user.get("companyId").toString());
        claims.put("userName", user.get("userName").toString());

        return createToken(claims, user.get("userName").toString());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, Map<String, Object> user) {
        final String username = extractUsername(token);
        return ((username.equals(user.get("email")) || username.equals(user.get("userName"))) && !isTokenExpired(token));
    }
}
