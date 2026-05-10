package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.BookService;
import com.acervo.acervoespirita.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookController {

    private final BookService bookService;
    private final SessionService sessionService;

    public BookController(BookService bookService, SessionService sessionService) {
        this.bookService = bookService;
        this.sessionService = sessionService;
    }

    @GetMapping("/books")
    public String books(@RequestParam(required = false) String bookName, HttpSession session, Model model) {
        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        if (bookName != null && !bookName.isBlank()) {
            model.addAttribute("books", bookService.findByTitleNormalize(bookName));
        } else {
            model.addAttribute("books", bookService.findAll());
        }

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("search", bookName);

        return "books/list";
    }

    @GetMapping("/books/new")
    public String newBook(HttpSession session, Model model) {
        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        model.addAttribute("loggedUser", loggedUser);

        return "books/form";
    }

    @GetMapping("/books/{id}/edit")
    public String editBook(@PathVariable Long id, HttpSession session, Model model) {
        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        model.addAttribute("loggedUser", loggedUser);

        return "books/form";
    }

    @PostMapping("/books")
    public String saveBook(@RequestParam(required = false) Long id,
                           @RequestParam String title,
                           @RequestParam String author,
                           @RequestParam(required = false) String psychographedBy,
                           @RequestParam(required = false) String category,
                           HttpSession session,
                           Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        try {
            if (id == null) {
                Book book = new Book(title, author, psychographedBy);
                book.setCategory(category);
                bookService.createBook(book, loggedUser);
            } else {
                bookService.updateBook(id, title, author, psychographedBy, category, loggedUser);
            }
            return "redirect:/books";

        } catch (IllegalArgumentException e) {

            Book book = new Book();
            book.setId(id);
            book.setTitle(title);
            book.setAuthor(author);
            book.setPsychographedBy(psychographedBy);
            book.setCategory(category);

            model.addAttribute("book", book);
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("error", e.getMessage());

            return "books/form";
        }
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, HttpSession session) {
        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }
        User loggedUser = sessionService.getLoggedUser(session);
        bookService.deleteBook(id, loggedUser);

        return "redirect:/books";
    }
}