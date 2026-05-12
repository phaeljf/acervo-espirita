package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.SessionService;
import com.acervo.acervoespirita.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UserService userService;
    private final SessionService sessionService;

    public LoginController(
            UserService userService,
            SessionService sessionService
    ) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @GetMapping("/")
    public String loginPage(HttpSession session,Model model) {

        if (sessionService.isLogged(session)) {
            return "redirect:/dashboard";
        }

        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,HttpSession session,Model model) {

        try {
            User user = userService.findByUsername(username);
            sessionService.login(session, user);

            return "redirect:/dashboard";

        } catch (Exception e) {
            model.addAttribute("error",e.getMessage());

            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        sessionService.logout(session);

        return "redirect:/";
    }

}