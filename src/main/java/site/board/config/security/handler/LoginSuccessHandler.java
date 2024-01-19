package site.board.config.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import site.board.util.JWTUtil;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> claim = Map.of("id", authentication.getName());

        String accessToken = jwtUtil.generateToken(claim, 60);
        String refreshToken = jwtUtil.generateToken(claim, 60 * 24 * 14);

        Gson gson = new Gson();
        Map<String, String> tokenMap = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );

        String json = gson.toJson(tokenMap);
        response.getWriter().println(json);
    }
}
