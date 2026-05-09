package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final SessionService sessionService;

    public DashboardController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        model.addAttribute("loggedUser", loggedUser);

        return "dashboard/index";
    }
}