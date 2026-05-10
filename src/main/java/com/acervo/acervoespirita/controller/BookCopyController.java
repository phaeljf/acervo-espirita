package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.BookCopy;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.BookCopyService;
import com.acervo.acervoespirita.service.BookService;
import com.acervo.acervoespirita.service.SessionService;
import com.acervo.acervoespirita.service.ShelfPositionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/copies")
@RequiredArgsConstructor
public class BookCopyController {

    private final BookCopyService bookCopyService;
    private final BookService bookService;
    private final ShelfPositionService shelfPositionService;
    private final SessionService sessionService;

    // Lista exemplares
    @GetMapping
    public String listCopies(HttpSession session, Model model) {
        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("copies", bookCopyService.findAll());

        return "copies/list";
    }

    // Form novo exemplar
    @GetMapping("/new")
    public String newCopy(HttpSession session, Model model) {
        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("books", bookService.findAll());
        model.addAttribute("positions", shelfPositionService.findAll());
        model.addAttribute("copy", new BookCopy());

        return "copies/form";
    }

    // Salvar exemplar
    @PostMapping
    public String saveCopy(@RequestParam Long bookId, @RequestParam Long shelfPositionId, @RequestParam String code, HttpSession session, Model model) {
        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        try {
            bookCopyService.createBookCopy(
                    bookId,
                    shelfPositionId,
                    code,
                    loggedUser
            );
            return "redirect:/copies";

        } catch (IllegalArgumentException e) {
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("books", bookService.findAll());
            model.addAttribute("positions", shelfPositionService.findAll());
            model.addAttribute("error", e.getMessage());
            return "copies/form";
        }
    }

    // Remover exemplar
    @PostMapping("/{id}/delete")
    public String deleteCopy(@PathVariable Long id, HttpSession session) {
        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }
        User loggedUser = sessionService.getLoggedUser(session);
        bookCopyService.deleteBookCopy(id, loggedUser);

        return "redirect:/copies";
    }
}