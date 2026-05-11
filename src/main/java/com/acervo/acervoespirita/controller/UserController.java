package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.UserRole;
import com.acervo.acervoespirita.service.SessionService;
import com.acervo.acervoespirita.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;

    @GetMapping
    public String listUsers(@RequestParam(required = false) String search, HttpSession session, Model model) {

        sessionService.validateAccess(session);

        User loggedUser = sessionService.getLoggedUser(session);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("search", search);

        if (search != null && !search.isBlank()) {
            model.addAttribute("users", userService.findByName(search));
        } else {
            model.addAttribute("users", userService.findAll());
        }

        return "users/list";
    }

    @GetMapping("/new")
    public String newUser(HttpSession session, Model model) {

        sessionService.validateAccess(session);

        User loggedUser = sessionService.getLoggedUser(session);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("user", new User());

        if (loggedUser.isAdmin()) {
            model.addAttribute("roles", UserRole.values());
        } else {
            model.addAttribute("roles", new UserRole[]{UserRole.USER});
        }

        return "users/form";
    }

    @PostMapping
    public String createUser(@RequestParam String name,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam(required = false) String phone,
                             @RequestParam UserRole role,
                             HttpSession session,
                             Model model) {

        sessionService.validateAccess(session);

        User loggedUser = sessionService.getLoggedUser(session);

        try {

            User user = User.builder()
                    .name(name)
                    .username(username)
                    .email(email)
                    .phone(phone)
                    .role(role)
                    .build();

            userService.createUser(user, loggedUser);

            return "redirect:/users";

        } catch (Exception e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("user", new User());

            if (loggedUser.isAdmin()) {
                model.addAttribute("roles", UserRole.values());
            } else {
                model.addAttribute("roles", new UserRole[]{UserRole.USER});
            }

            return "users/form";
        }

    }

    @GetMapping("/{id}")
    public String detailsUser(@PathVariable Long id, HttpSession session, Model model) {

        sessionService.validateAccess(session);

        User loggedUser = sessionService.getLoggedUser(session);

        User user = userService.findById(id);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("user", user);

        return "users/details";
    }

    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable Long id, HttpSession session, Model model) {

        sessionService.validateAccess(session);

        User loggedUser = sessionService.getLoggedUser(session);

        User user = userService.findById(id);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("user", user);

        if (loggedUser.isAdmin()) {
            model.addAttribute("roles", UserRole.values());
        } else {
            model.addAttribute("roles", new UserRole[]{UserRole.USER});
        }

        return "users/form";
    }
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam(required = false) String phone,
                             @RequestParam(required = false) UserRole role,
                             HttpSession session,
                             Model model) {

        sessionService.validateAccess(session);

        User loggedUser = sessionService.getLoggedUser(session);

        try {

            userService.updateUser(
                    id,
                    name,
                    email,
                    phone,
                    role,
                    loggedUser
            );

            return "redirect:/users/" + id;

        } catch (Exception e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("user", userService.findById(id));

            if (loggedUser.isAdmin()) {
                model.addAttribute("roles", UserRole.values());
            } else {
                model.addAttribute("roles", new UserRole[]{UserRole.USER});
            }

            return "users/form";
        }

    }

    @PostMapping("/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id, HttpSession session) {
        sessionService.validateAccess(session);
        User loggedUser = sessionService.getLoggedUser(session);
        userService.deactivateUser(id, loggedUser);
        return "redirect:/users";
    }

    @PostMapping("/{id}/reactivate")
    public String reactivateUser(@PathVariable Long id, HttpSession session) {
        sessionService.validateAccess(session);
        User loggedUser = sessionService.getLoggedUser(session);
        userService.reactivateUser(id, loggedUser);
        return "redirect:/users";
    }

}