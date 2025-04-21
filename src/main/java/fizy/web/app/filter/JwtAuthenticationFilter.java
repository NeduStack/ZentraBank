package fizy.web.app.filter;

import fizy.web.app.entity.User;
import fizy.web.app.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
                // Extract the JWT token from the request header
                String jwtToken = request.getHeader("Authorization");
                if (jwtToken != null && !jwtService.isTokenValid(jwtToken.substring(7))) {
                    filterChain.doFilter(request, response);
                    return;
                }
                // Extract the username from the token
                jwtToken = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
                String subject = jwtService.extractSubject(jwtToken);
                User user = (User) userDetailsService.loadUserByUsername(subject);
                var context = SecurityContextHolder.getContext();
                if (user != null) {
                    var authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authenticationToken.setDetails(request);
                    context.setAuthentication(authenticationToken);
                }
                filterChain.doFilter(request, response);


    }
}
