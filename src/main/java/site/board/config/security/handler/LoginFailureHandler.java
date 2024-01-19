package site.board.config.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.util.Map;

public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        String msg = "";
        if (exception instanceof LockedException){
            msg = "이미 탈퇴한 회원입니다.";
        } else if (exception instanceof DisabledException) {
            msg = "비활성화 된 계정 입니다.";
        } else if(exception instanceof CredentialsExpiredException) {
            msg = "만료된 아이디 입니다.";
        } else if(exception instanceof BadCredentialsException) {
            msg = "아이디 또는 비밀번호가 일치하지 않습니다.";
        }else if (exception instanceof InternalAuthenticationServiceException) {
            msg = "내부 시스템 문제로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요. ";
        }  else if (exception instanceof UsernameNotFoundException) {
            msg = "아이디가 존재하지 않습니다.";
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            msg = "인증 요청이 거부되었습니다.";
        } else {
            msg = "알 수 없는 오류로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
        }

        Gson gson = new Gson();
        Map<String, String> resultMap = Map.of(
                "message", msg
        );
        String json = gson.toJson(resultMap);

        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(json);
    }
}
