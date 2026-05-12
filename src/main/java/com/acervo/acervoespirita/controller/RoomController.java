package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.Room;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.RoomService;
import com.acervo.acervoespirita.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/locations")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final SessionService sessionService;

    // Lista salas
    @GetMapping
    public String listRooms(HttpSession session, Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("rooms", roomService.findAll());

        return "locations/list";
    }

    // Form nova sala
    @GetMapping("/new")
    public String newRoom(HttpSession session, Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("room", new Room());

        return "locations/form";
    }

    // Salvar sala
    @PostMapping("/save")
    public String saveRoom(@ModelAttribute Room room, HttpSession session, Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        try {
            roomService.createRoom(room);

            return "redirect:/locations";

        } catch (IllegalArgumentException e) {
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("room", room);
            model.addAttribute("error", e.getMessage());

            return "locations/form";
        }
    }

    // Editar sala
    @GetMapping("/edit/{id}")
    public String editRoom(@PathVariable Long id, HttpSession session, Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("room", roomService.findById(id));

        return "locations/form";
    }

    // Remover sala
    @PostMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        try {
            roomService.deleteRoom(id);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/locations";
    }
}