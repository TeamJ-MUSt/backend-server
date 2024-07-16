package TeamJ.MUSt.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if(session == null || session.getAttribute("memberId") == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println(Map.of("success", "false", "message", "need to log in"));
            return false;

        }
        return true;
    }

}
