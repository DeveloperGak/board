package site.board.config.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import site.board.config.security.exception.AccessTokenException;
import site.board.config.security.service.CustomUserDetailsService;
import site.board.util.JWTUtil;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        log.info("path: " + path);
        if(!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);

            return;
        }

        log.info("--- Token Check ---");
        log.info("jwtUtil: " + jwtUtil);

        try {
            Map<String, Object> payload = validateAccessToken(request);

            String memberId = (String) payload.get("id");
            log.info("payload MemberId: " + memberId);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(memberId);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }
        catch (AccessTokenException e) {
            e.sendResponseError(response);
        }

    }

    private Map<String ,Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {
        String header = request.getHeader("Authorization");
        if(header == null || header.length() < 8) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        String tokenType = header.substring(0, 6);
        if(tokenType.equalsIgnoreCase("Bearer") == false) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        String token = header.substring(7);
        try {
            Map<String, Object> result = jwtUtil.validateToken(token);

            return result;
        }
        catch (ExpiredJwtException e) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }
        catch (MalformedJwtException e){
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        }
        catch (SignatureException e){
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        }
    }
}
