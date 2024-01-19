package site.board.config.security.exception;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class AccessTokenException extends RuntimeException {

    TOKEN_ERROR token_error;

    public enum TOKEN_ERROR {
        UNACCEPT(401, "Token value is invalid"),
        BADTYPE(401, "Token type is invalid"),
        MALFORM(403, "Malformed Token"),
        BADSIGN(403, "Bad signature"),
        EXPIRED(403, "Expired Token");

        private final int status;
        private final String message;

        TOKEN_ERROR(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public AccessTokenException(TOKEN_ERROR error) {
        super(error.name());
        this.token_error = error;
    }

    public void sendResponseError(HttpServletResponse response) {
        response.setStatus(token_error.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String json = gson.toJson(Map.of("message", token_error.getMessage(), "time", new Date()));

        try {
            response.getWriter().println(json);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
