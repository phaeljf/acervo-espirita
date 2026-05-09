package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static final String LOGGED_USER = "loggedUser";

    public void login(HttpSession session, User user) {
        session.setAttribute(LOGGED_USER, user);
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute(LOGGED_USER);
    }

    public boolean isLogged(HttpSession session) {
        return getLoggedUser(session) != null;
    }
}