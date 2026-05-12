package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.Configuration;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.ConfigurationService;
import com.acervo.acervoespirita.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/configuration")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;
    private final SessionService sessionService;

    @GetMapping
    public String form(HttpSession session, Model model) {

        sessionService.validateAccess(session);

        User loggedUser = sessionService.getLoggedUser(session);
        Configuration configuration = configurationService.getCurrentConfiguration();
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("configuration", configuration);

        return "configuration/form";
    }

    @PostMapping
    public String update(@RequestParam Integer maxBooksPerLoan,
                         @RequestParam Integer loanDaysLimit,
                         @RequestParam Boolean allowRenewal,
                         HttpSession session,
                         Model model) {

        sessionService.validateAccess(session);
        User loggedUser = sessionService.getLoggedUser(session);

        try {
            configurationService.updateMaxBooksPerLoan(maxBooksPerLoan);
            configurationService.updateLoanDaysLimit(loanDaysLimit);
            configurationService.updateAllowRenewal(allowRenewal);
            model.addAttribute("success","Configurações atualizadas com sucesso.");

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("configuration", configurationService.getCurrentConfiguration());

        return "configuration/form";
    }

}