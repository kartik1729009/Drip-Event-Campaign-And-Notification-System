package com.Kartik.notiflow.Security;

import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.Kartik.notiflow.Config.JwtService;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.UserAuth.UserAuthRepository;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuthRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final WorkspaceAuthRepository workspaceRepository;
    private final UserAuthRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;

        // Read accessToken cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!"ACCESS".equals(jwtService.extractTokenType(token))) {
            filterChain.doFilter(request, response);
            return;
        }

        String authType = jwtService.extractAuthType(token);

        if ("WORKSPACE".equals(authType)) {

            Long workspaceId = Long.valueOf(jwtService.extractSubject(token));

            WorkspaceAuth workspace = workspaceRepository
                    .findById(workspaceId)
                    .orElse(null);

            if (workspace != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        workspace,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_WORKSPACE")));

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } else if ("USER".equals(authType)) {

            Long userId = Long.valueOf(jwtService.extractSubject(token));

            UserAuth user = userRepository
                    .findById(userId)
                    .orElse(null);

            if (user != null
                    && Boolean.TRUE.equals(user.getActive())
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")));

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}