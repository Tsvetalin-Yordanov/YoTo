package com.example.yoto.handlerInterceptor;

import com.example.yoto.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.example.yoto.util.Util.LOGGED;
import static com.example.yoto.util.Util.LOGGED_FROM;

@Component
public class LoggerInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ((request.getMethod().equals("GET")&&!request.getServletPath().contains("/users/history"))
                || request.getServletPath().contains("verify_registration")
                || Objects.equals(request.getServletPath(), "/users/register")
                || Objects.equals(request.getServletPath(), "/users/log_in")
                || Objects.equals(request.getServletPath(), "/users/logout")
                || Objects.equals(request.getServletPath(), "/users/search")
                || Objects.equals(request.getServletPath(), "/users/forgotten_password")
                || Objects.equals(request.getServletPath(), "/videos/search_by_title")
                || Objects.equals(request.getServletPath(), "/playlists/search_by_title")
                || Objects.equals(request.getServletPath(), "/comments/sub_comments")) {
            return true;
        }
        HttpSession session = request.getSession();
        boolean newSession = session.isNew();
        boolean logged = session.getAttribute(LOGGED) != null && ((Boolean) session.getAttribute(LOGGED));
        boolean sameIp = request.getRemoteAddr().equals(session.getAttribute(LOGGED_FROM));
        if (newSession || !logged || !sameIp) {
            throw new UnauthorizedException("You have to log in!");
        }
        return true;
    }
}
