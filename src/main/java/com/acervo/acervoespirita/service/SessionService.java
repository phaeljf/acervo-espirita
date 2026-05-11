package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static final String LOGGED_USER = "loggedUser";

    public void login(HttpSession session, User user) {

        if (user == null) {
            throw new IllegalArgumentException("Usuário inválido");
        }
        if (!user.canHandleLoan()) {
            throw new IllegalArgumentException(
                    "Usuário sem permissão para acessar o sistema"
            );
        }
        if (!user.isActive()) {
            throw new IllegalArgumentException(
                    "Usuário inativo"
            );
        }

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

    public boolean isAdmin(HttpSession session) {

        User user = getLoggedUser(session);

        return user != null
                && user.getRole() == UserRole.ADMIN;
    }

    public boolean isStaff(HttpSession session) {

        User user = getLoggedUser(session);

        return user != null
                && user.getRole() == UserRole.STAFF;
    }

    public boolean canAccessSystem(HttpSession session) {

        User user = getLoggedUser(session);

        return user != null
                && (
                user.getRole() == UserRole.ADMIN
                        || user.getRole() == UserRole.STAFF
        );
    }

    public void validateAccess(HttpSession session) {

        User user = getLoggedUser(session);

        if (user == null) {
            throw new IllegalStateException(
                    "Usuário não autenticado"
            );
        }

        if (!user.canHandleLoan()) {
            throw new IllegalStateException(
                    "Usuário sem permissão"
            );
        }

        if (!user.isActive()) {
            throw new IllegalStateException(
                    "Usuário inativo"
            );
        }

    }

}