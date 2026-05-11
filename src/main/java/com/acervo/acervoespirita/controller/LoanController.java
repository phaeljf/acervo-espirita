package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.Loan;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.LoanStatus;
import com.acervo.acervoespirita.model.enums.UserStatus;
import com.acervo.acervoespirita.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;
    private final BookCopyService bookCopyService;
    private final ShelfPositionService shelfPositionService;
    private final SessionService sessionService;
    private final BookService bookService;


    @GetMapping
    public String listLoans(@RequestParam(required = false) String search,HttpSession session,Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        if (search != null && !search.isBlank()) {
            model.addAttribute("loans", loanService.findOpenLoansByUserName(search));
        } else {
            model.addAttribute("loans", loanService.findByStatus(LoanStatus.OPEN));
        }

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("search", search);

        return "loan/list";
    }

    @GetMapping("/{id}")
    public String detailsLoan(@PathVariable Long id,HttpSession session,Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        Loan loan = loanService.findById(id);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("loan", loan);
        model.addAttribute("positions", shelfPositionService.findAll());

        return "loan/details";
    }

    @GetMapping("/new")
    public String newLoan(HttpSession session,Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("books", bookService.findAll());
        model.addAttribute("copies", bookCopyService.findAvailableCopies());

        return "loan/form";
    }

    @PostMapping
    public String createLoan(
            @RequestParam Long userId,
            @RequestParam List<Long> bookCopyIds,
            HttpSession session
    ) {

        User loggedUser = sessionService.getLoggedUser(session);

        loanService.createLoan(
                userId,
                bookCopyIds,
                loggedUser
        );

        return "redirect:/loan";
    }

    @GetMapping("/{id}/return")
    public String returnLoanPage(@PathVariable Long id,Model model,HttpSession session) {

        Loan loan = loanService.findById(id);
        model.addAttribute("loan", loan);

        model.addAttribute(
                "positions",
                shelfPositionService.findAll()
        );

        model.addAttribute(
                "loggedUser",
                sessionService.getLoggedUser(session)
        );

        return "loan/return";

    }

}
