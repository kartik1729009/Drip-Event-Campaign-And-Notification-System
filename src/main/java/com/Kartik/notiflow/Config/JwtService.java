package com.Kartik.notiflow.Config;

import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiry}")
    private long ACCESS_TOKEN_EXPIRY;

    @Value("${jwt.refresh-token-expiry}")
    private long REFRESH_TOKEN_EXPIRY;

    public String generateWorkspaceAccessToken(WorkspaceAuth workspaceAuth) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("workspaceId", workspaceAuth.getWorkspaceId());
        claims.put("workspaceName", workspaceAuth.getWorkspaceName());
        claims.put("username", workspaceAuth.getUsername());
        claims.put("authType", "WORKSPACE");
        claims.put("tokenType", "ACCESS");
        return generateToken(
                workspaceAuth.getWorkspaceId().toString(),
                claims,
                ACCESS_TOKEN_EXPIRY);
    }

    public String generateWorkspaceRefreshToken(
            WorkspaceAuth workspace) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("workspaceName", workspace.getWorkspaceName());
        claims.put("authType", "WORKSPACE");
        claims.put("tokenType", "REFRESH");

        return generateToken(
                workspace.getWorkspaceId().toString(),
                claims,
                REFRESH_TOKEN_EXPIRY);
    }

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(
            String subject,
            Map<String, Object> claims,
            long expiryTime) {

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + expiryTime))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateUserAccessToken(UserAuth userAuth) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", userAuth.getUserAuthId());
        claims.put("userName", userAuth.getUserName());
        claims.put("email", userAuth.getEmail());

        claims.put(
                "workspaceId",
                userAuth.getWorkspace().getWorkspaceId());

        claims.put(
                "workspaceName",
                userAuth.getWorkspace().getWorkspaceName());

        claims.put(
                "role",
                userAuth.getRole().toString());

        claims.put("authType", "USER");
        claims.put("tokenType", "ACCESS");
        return generateToken(
                userAuth.getUserAuthId().toString(),
                claims,
                ACCESS_TOKEN_EXPIRY);
    }

    public String generateUserRefreshToken(
            UserAuth userAuth) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(
                "userId",
                userAuth.getUserAuthId());
        claims.put(
                "workspaceId",
                userAuth.getWorkspace().getWorkspaceId());
        claims.put("authType", "USER");
        claims.put("tokenType", "REFRESH");
        return generateToken(
                userAuth.getUserAuthId().toString(),
                claims,
                REFRESH_TOKEN_EXPIRY);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractWorkspaceId(String token) {
        Object workspaceId = extractAllClaims(token).get("workspaceId");
        if (workspaceId == null) {
            return null;
        }
        return Long.parseLong(workspaceId.toString());
    }

    public String extractWorkspaceName(String token) {
        Object workspaceName = extractAllClaims(token).get("workspaceName");
        return workspaceName != null
                ? workspaceName.toString()
                : null;
    }

    public String extractUsername(String token) {
        Object username = extractAllClaims(token).get("username");
        return username != null
                ? username.toString()
                : null;
    }

    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get("userId");
        if (userId == null) {
            return null;
        }
        return Long.parseLong(userId.toString());
    }

    public String extractUserName(String token) {
        Object userName = extractAllClaims(token).get("userName");
        return userName != null
                ? userName.toString()
                : null;
    }

    public String extractEmail(String token) {
        Object email = extractAllClaims(token).get("email");
        return email != null
                ? email.toString()
                : null;
    }

    public String extractRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role != null
                ? role.toString()
                : null;
    }

    public String extractAuthType(String token) {
        Object authType = extractAllClaims(token).get("authType");
        return authType != null
                ? authType.toString()
                : null;
    }

    public String extractTokenType(String token) {
        Object tokenType = extractAllClaims(token).get("tokenType");
        return tokenType != null
                ? tokenType.toString()
                : null;
    }

    public Boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }
}
