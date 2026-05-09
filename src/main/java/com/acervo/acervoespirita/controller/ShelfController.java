package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.Shelf;
import com.acervo.acervoespirita.model.ShelfPosition;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.SessionService;
import com.acervo.acervoespirita.service.ShelfService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shelves")
@RequiredArgsConstructor
public class ShelfController {

    private final ShelfService shelfService;
    private final SessionService sessionService;

    // Lista estantes
    @GetMapping
    public String listShelves(HttpSession session, Model model) {
        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }
        User loggedUser = sessionService.getLoggedUser(session);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("shelves", shelfService.findAll());

        return "shelves/list";
    }

    // Form nova estante
    @GetMapping("/new")
    public String newShelf(HttpSession session, Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }
        User loggedUser = sessionService.getLoggedUser(session);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("shelf", new Shelf());

        return "shelves/form";
    }

    // Salvar estante
    @PostMapping("/save")
    public String saveShelf(@ModelAttribute Shelf shelf, HttpSession session) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        shelfService.createShelf(shelf);

        return "redirect:/shelves";
    }

    // Form editar estante
    @GetMapping("/edit/{id}")
    public String editShelf(@PathVariable Long id, HttpSession session, Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("shelf", shelfService.findById(id));

        return "shelves/form";
    }

    // Adicionar prateleira
    @PostMapping("/{id}/positions/add")
    public String addPosition(@PathVariable Long id, @RequestParam String positionName, HttpSession session) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        Shelf shelf = shelfService.findById(id);

        shelf.addPosition(new ShelfPosition(positionName));

        shelfService.save(shelf);

        return "redirect:/shelves/edit/" + id;
    }

    // Remover prateleira
    @PostMapping("/{shelfId}/positions/remove/{positionId}")
    public String removePosition(@PathVariable Long shelfId, @PathVariable Long positionId, HttpSession session) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        Shelf shelf = shelfService.findById(shelfId);

        ShelfPosition positionToRemove = shelf.getPositions()
                .stream()
                .filter(position -> position.getId().equals(positionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Prateleira não encontrada"));

        shelf.removePosition(positionToRemove);

        shelfService.save(shelf);

        return "redirect:/shelves/edit/" + shelfId;
    }

    // Remover estante
    @PostMapping("/delete/{id}")
    public String deleteShelf(@PathVariable Long id, HttpSession session) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        shelfService.deleteShelf(id);

        return "redirect:/shelves";
    }
}