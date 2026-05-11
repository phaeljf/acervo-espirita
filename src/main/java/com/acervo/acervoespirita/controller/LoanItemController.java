package com.acervo.acervoespirita.controller;
import com.acervo.acervoespirita.model.LoanItem;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.LoanItemService;
import com.acervo.acervoespirita.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/loan-items")
@RequiredArgsConstructor
public class LoanItemController {

    private final LoanItemService loanItemService;
    private final SessionService sessionService;

    // Devolver exemplar individualmente
    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id,
                             @RequestParam Long shelfPositionId,
                             @RequestParam(required = false) String observation,
                             HttpSession session) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);

        LoanItem loanItem = loanItemService.findById(id);

        Long loanId = loanItem.getLoan().getId();

        loanItemService.returnBook(
                id,
                shelfPositionId,
                observation,
                loggedUser
        );

        return "redirect:/loan/" + loanId;
    }
}