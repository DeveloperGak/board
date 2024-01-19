package site.board.config.security.filter;

import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import site.board.config.security.exception.RefreshTokenException;
import site.board.util.JWTUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final String refreshPath;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if(path.equals(refreshPath) == false) {
            log.info("-- skip refresh token filter");
            filterChain.doFilter(request, response);

            return;
        }
        Map<String, String> tokens = parseRequestJSON(request);
        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");
        log.info("accessToken: " + accessToken);
        log.info("refreshToken: " + refreshToken);

        try{
            checkAccessToken(accessToken);
        }
        catch(RefreshTokenException e){
            e.sendResponseError(response);

            return;
        }

        Map<String, Object> refreshClaims = null;
        try {
            refreshClaims = checkRefreshToken(refreshToken);
            log.info(refreshClaims);
        }
        catch(RefreshTokenException e){
            e.sendResponseError(response);

            return;
        }

        Integer exp = (Integer)refreshClaims.get("exp");
        Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
        Date current = new Date(System.currentTimeMillis());
        long gapTime = (expTime.getTime() - current.getTime());

        String memberId = (String)refreshClaims.get("id");

        String accessTokenValue = jwtUtil.generateToken(Map.of("id", memberId), 60);

        String refreshTokenValue = tokens.get("refreshToken");

        if(gapTime < (1000 * 60 * 60 * 24 * 3) ){
            refreshTokenValue = jwtUtil.generateToken(Map.of("id", memberId), 60 * 24 * 14);
        }

        sendTokens(accessTokenValue, refreshTokenValue, response);
    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        try (Reader reader = new InputStreamReader(request.getInputStream())){
            Gson gson = new Gson();

            return  gson.fromJson(reader, Map.class);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    private void checkAccessToken(String accessToken) throws RefreshTokenException {
        try {
            jwtUtil.validateToken(accessToken);
        }
        catch (ExpiredJwtException e) {
            log.info("Access Token has expired");
        }
        catch (Exception e) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
        }
    }

    private Map<String, Object> checkRefreshToken(String refreshPath) throws RefreshTokenException {
        try {
            Map<String, Object> token = jwtUtil.validateToken(refreshPath);

            return token;
        }
        catch (ExpiredJwtException e) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
        }
        catch (MalformedJwtException e) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.BAD_REFRESH);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }
    }

    private void sendTokens(String accessToken, String refreshToken, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();
        String json = gson.toJson(Map.of(
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                )
        );

        try {
            response.getWriter().println(json);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
