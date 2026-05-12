package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.SessionService;
import com.acervo.acervoespirita.service.dashboard.DashboardService;
import com.acervo.acervoespirita.service.dashboard.dto.DashboardDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final SessionService sessionService;
    private final DashboardService dashboardService;

    public DashboardController(
            SessionService sessionService,
            DashboardService dashboardService
    ) {
        this.sessionService = sessionService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        DashboardDTO dashboard = dashboardService.getDashboardData();
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("dashboard", dashboard);

        return "dashboard/index";
    }
}