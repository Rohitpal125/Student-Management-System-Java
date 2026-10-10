package main.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import util.TokenUtil;

// Runs before every /api/students request.
// Reading (GET) is open to everyone. Changing data needs an admin token.
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String method = request.getMethod();

        if (method.equals("GET") || method.equals("HEAD") || method.equals("OPTIONS")) {
            return true;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String username = TokenUtil.verify(header.substring(7));

            if (username != null) {
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("Admin login required");
        return false;
    }
}