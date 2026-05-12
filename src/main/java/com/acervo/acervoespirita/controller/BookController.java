package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.BookService;
import com.acervo.acervoespirita.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final SessionService sessionService;

    // Lista livros
    @GetMapping
    public String listBooks(@RequestParam(required = false) String bookName,HttpSession session,Model model) {

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

    // Details livro
    @GetMapping("/{id}")
    public String detailsBook(@PathVariable Long id,HttpSession session,Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        Book book = bookService.findById(id);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", book);

        return "books/details";
    }

    // Form novo livro
    @GetMapping("/new")
    public String newBook(HttpSession session,Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", new Book());

        return "books/form";
    }

    // Form editar livro
    @GetMapping("/{id}/edit")
    public String editBook(@PathVariable Long id,HttpSession session,Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", bookService.findById(id));

        return "books/form";
    }

    // Salvar livro
    @PostMapping
    public String saveBook(@RequestParam(required = false) Long id,
                           @RequestParam String title,
                           @RequestParam String author,
                           @RequestParam(required = false) String psychographedBy,
                           HttpSession session,
                           Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        try {
            if (id == null) {
                Book book = new Book(title, author, psychographedBy);
                bookService.createBook(book, loggedUser);
            } else {
                bookService.updateBook(id, title, author, psychographedBy, loggedUser);
            }

            return "redirect:/books";

        } catch (IllegalArgumentException e) {

            Book book = new Book();

            book.setId(id);
            book.setTitle(title);
            book.setAuthor(author);
            book.setPsychographedBy(psychographedBy);

            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("book", book);
            model.addAttribute("error", e.getMessage());

            return "books/form";
        }
    }

    // Excluir livro
    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id,HttpSession session) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        bookService.deleteBook(id, loggedUser);

        return "redirect:/books";
    }
}