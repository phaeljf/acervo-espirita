package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.Loan;
import com.acervo.acervoespirita.model.LoanItem;
import com.acervo.acervoespirita.model.Location;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.LogType;
import com.acervo.acervoespirita.model.enums.LoanStatus;
import com.acervo.acervoespirita.repository.LoanItemRepository;
import com.acervo.acervoespirita.repository.LoanRepository;
import com.acervo.acervoespirita.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanItemService {

    private final LoanItemRepository loanItemRepository;
    private final LoanRepository loanRepository;
    private final LocationRepository locationRepository;
    private final LogService logService;

    // Realiza devolução de um exemplar
    @Transactional
    public LoanItem returnBook(Long loanItemId, Long locationId, String observation, User handledBy) {

        LoanItem loanItem = findById(loanItemId);

        if (loanItem.isReturned()) {
            throw new IllegalStateException("Exemplar já foi devolvido.");
        }

        Location location = locationRepository.findById(locationId).orElseThrow(() -> new IllegalArgumentException("Localização não encontrada."));

        loanItem.markAsReturned(location);

        LoanItem updatedLoanItem = loanItemRepository.save(loanItem);

        logService.register(LogType.BOOK_RETURNED, handledBy, "Exemplar " + updatedLoanItem.getBookCopy().getCode() + " foi devolvido.");

        Loan loan = updatedLoanItem.getLoan();

        boolean allReturned = loan.getItems().stream().allMatch(LoanItem::isReturned);

        if (allReturned) {

            loan.close(observation);

            loanRepository.save(loan);

            logService.register(LogType.LOAN_CLOSED, handledBy, "Empréstimo #" + loan.getId() + " foi encerrado.");
        }

        return updatedLoanItem;
    }

    // Busca item por id
    @Transactional(readOnly = true)
    public LoanItem findById(Long id) {
        return loanItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item do empréstimo não encontrado."));
    }

    // Lista itens de um empréstimo
    @Transactional(readOnly = true)
    public List<LoanItem> findByLoan(Loan loan) {
        return loanItemRepository.findByLoan(loan);
    }

    // Lista itens devolvidos
    @Transactional(readOnly = true)
    public List<LoanItem> findReturnedItems() {
        return loanItemRepository.findByReturnDateIsNotNull();
    }

    // Lista itens ainda emprestados
    @Transactional(readOnly = true)
    public List<LoanItem> findPendingItems() {
        return loanItemRepository.findByReturnDateIsNull();
    }

    // Lista todos os itens
    @Transactional(readOnly = true)
    public List<LoanItem> findAll() {
        return loanItemRepository.findAll();
    }
}