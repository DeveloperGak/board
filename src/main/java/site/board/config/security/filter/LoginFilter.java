package site.board.config.security.filter;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@Log4j2
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    public LoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if(request.getMethod().equalsIgnoreCase("GET")) {
            log.info("GET METHOD NOT SUPPORT");
            return null;
        }

        Map<String, String> jsonData = parseRequestJSON(request);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                jsonData.get("id"),
                jsonData.get("password")
        );

        Authentication authenticate = getAuthenticationManager().authenticate(authenticationToken);

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        try(Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
