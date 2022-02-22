package com.example.yoto.handlerInterceptor;

import com.example.yoto.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import static com.example.yoto.util.Util.LOGGED;
import static com.example.yoto.util.Util.LOGGED_FROM;

@Component
public class LoggerInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (Objects.equals(request.getServletPath(), "/users/register")
                || Objects.equals(request.getServletPath(), "/users/log_in")
                || Objects.equals(request.getServletPath(), "/users/logout")
                || Objects.equals(request.getServletPath(), "/users/search")
                || Objects.equals(request.getServletPath(), "/users")
                || Objects.equals(request.getServletPath(), "/users/forgotten_password")
                || Objects.equals(request.getServletPath(), "/users/{id}")
                || Objects.equals(request.getServletPath(), "/videos/search_by_title")
                || Objects.equals(request.getServletPath(), "/videos/get_all")
                || Objects.equals(request.getServletPath(), "/videos/{id:[\\d]+}")
                || Objects.equals(request.getServletPath(), "/playlists/{id:[\\d]+}")
                || Objects.equals(request.getServletPath(), "/playlists/search_by_title")
                || Objects.equals(request.getServletPath(), "/playlists/get_all")
                || Objects.equals(request.getServletPath(), "/comments/{id}")
                || Objects.equals(request.getServletPath(), "/comments/sub_comments")
                || Objects.equals(request.getServletPath(), "/comments/{id}")
                || Objects.equals(request.getServletPath(), "/categories")
                || Objects.equals(request.getServletPath(), "/categories/{id}")) {
            return true;
        }
        HttpSession session = request.getSession();
        boolean newSession = session.isNew();
        boolean logged = session.getAttribute(LOGGED) != null && ((Boolean) session.getAttribute(LOGGED));
        boolean sameIp = request.getRemoteAddr().equals(session.getAttribute(LOGGED_FROM));
        if (newSession || !logged || !sameIp) {
            System.out.println("E");
            throw new UnauthorizedException("You have to log in!");
        }
        return true;
    }
}
