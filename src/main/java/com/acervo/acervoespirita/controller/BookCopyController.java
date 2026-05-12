package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.BookCopy;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.BookCopyStatus;
import com.acervo.acervoespirita.service.BookCopyService;
import com.acervo.acervoespirita.service.BookService;
import com.acervo.acervoespirita.service.SessionService;
import com.acervo.acervoespirita.service.ShelfPositionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("books", bookService.findAll());

        return "copies/list";
    }

    // Form novo exemplar
    @GetMapping("/new")
    public String newCopy(@RequestParam(required = false) Long bookId,
                          HttpSession session,
                          Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("positions", shelfPositionService.findAll());
        model.addAttribute("copy", new BookCopy());
        model.addAttribute("books", bookService.findAll());

        if (bookId != null) {
            Book book = bookService.findById(bookId);
            model.addAttribute("book", book);
            model.addAttribute("selectedBookId", bookId);

        }

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
            bookCopyService.createBookCopy(bookId,shelfPositionId,code,loggedUser
            );
            return "redirect:/copies/book/" + bookId;

        } catch (IllegalArgumentException e) {

            Book book = bookService.findById(bookId);
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("book", book);
            model.addAttribute("selectedBookId", bookId);
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
        BookCopy copy = bookCopyService.findById(id);
        Long bookId = copy.getBook().getId();
        bookCopyService.deleteBookCopy(id, loggedUser);

        return "redirect:/copies/book/" + bookId;
    }

    @GetMapping("/book/{id}")
    public String detailsCopies(@PathVariable Long id, HttpSession session, Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        Book book = bookService.findById(id);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", book);
        model.addAttribute("copies", bookCopyService.findByBook(book));

        return "copies/details";
    }

    @GetMapping("/{id}/edit")
    public String editCopy(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        BookCopy copy = bookCopyService.findById(id);

        if (copy.getStatus() == BookCopyStatus.LOANED) {

            Long bookId = copy.getBook().getId();
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Livro emprestado, finalize o empréstimo através do sistema de empréstimos para poder editá-lo."
            );

            return "redirect:/copies/book/" + bookId;
        }

        User loggedUser = sessionService.getLoggedUser(session);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("copy", copy);
        model.addAttribute("positions", shelfPositionService.findAll());
        model.addAttribute("statuses", BookCopyStatus.values());

        return "copies/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateCopy(@PathVariable Long id,
                             @RequestParam Long shelfPositionId,
                             @RequestParam(required = false) String code,
                             @RequestParam BookCopyStatus status,
                             HttpSession session,
                             Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        try {
            bookCopyService.updateCopy(id,shelfPositionId,code,status,loggedUser);
            Long bookId = bookCopyService.findById(id).getBook().getId();

            return "redirect:/copies/book/" + bookId;

        } catch (IllegalArgumentException e) {

            BookCopy copy = bookCopyService.findById(id);

            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("copy", copy);
            model.addAttribute("positions", shelfPositionService.findAll());
            model.addAttribute("statuses", BookCopyStatus.values());
            model.addAttribute("error", e.getMessage());

            return "copies/edit";
        }
    }
}