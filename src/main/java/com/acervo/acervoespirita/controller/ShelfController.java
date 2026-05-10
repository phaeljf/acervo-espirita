package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.Room;
import com.acervo.acervoespirita.model.Shelf;
import com.acervo.acervoespirita.model.ShelfPosition;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.RoomService;
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
    private final RoomService roomService;

    // Lista estantes da sala
    @GetMapping
    public String listShelves(@RequestParam Long roomId, HttpSession session, Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }
        User loggedUser = sessionService.getLoggedUser(session);
        Room room = roomService.findById(roomId);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("room", room);
        model.addAttribute("shelves", room.getShelves());

        return "locations/shelf/list";
    }

    // Form nova estante
    @GetMapping("/new")
    public String newShelf(@RequestParam Long roomId, HttpSession session, Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }
        User loggedUser = sessionService.getLoggedUser(session);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("room", roomService.findById(roomId));
        model.addAttribute("shelf", new Shelf());

        return "locations/shelf/form";
    }

    // Salvar estante
    @PostMapping("/save")
    public String saveShelf(@ModelAttribute Shelf shelf, @RequestParam Long roomId, HttpSession session) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }
        Room room = roomService.findById(roomId);
        room.addShelf(shelf);
        shelfService.save(shelf);

        return "redirect:/shelves?roomId=" + roomId;
    }

    // Editar estante
    @GetMapping("/edit/{id}")
    public String editShelf(@PathVariable Long id, HttpSession session, Model model) {
        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        Shelf shelf = shelfService.findById(id);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("shelf", shelf);

        return "locations/shelf/position/list";
    }

    // Adicionar prateleira
    @PostMapping("/{id}/positions/add")
    public String addPosition(@PathVariable Long id, @RequestParam String positionName,HttpSession session) {
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
    public String deleteShelf(@PathVariable Long id,HttpSession session) {
        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }
        Shelf shelf = shelfService.findById(id);
        Long roomId = shelf.getRoom().getId();
        shelfService.deleteShelf(id);

        return "redirect:/shelves?roomId=" + roomId;
    }
}