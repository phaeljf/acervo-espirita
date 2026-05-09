package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.BookCopy;
import com.acervo.acervoespirita.model.Configuration;
import com.acervo.acervoespirita.model.Loan;
import com.acervo.acervoespirita.model.LoanItem;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.LogType;
import com.acervo.acervoespirita.model.enums.LoanStatus;
import com.acervo.acervoespirita.repository.BookCopyRepository;
import com.acervo.acervoespirita.repository.LoanItemRepository;
import com.acervo.acervoespirita.repository.LoanRepository;
import com.acervo.acervoespirita.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanItemRepository loanItemRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    private final ConfigurationService configurationService;
    private final LogService logService;

    // Cria um novo empréstimo
    @Transactional
    public Loan createLoan(Long userId, List<Long> bookCopyIds, User handledBy) {

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (!user.canBorrow()) {
            throw new IllegalStateException("Usuário não pode realizar empréstimos.");
        }

        if (!handledBy.canHandleLoan()) {
            throw new IllegalStateException("Usuário sem permissão para realizar empréstimos.");
        }

        Configuration configuration = configurationService.getCurrentConfiguration();

        if (bookCopyIds.size() > configuration.getMaxBooksPerLoan()) {
            throw new IllegalStateException("Quantidade de livros excede o limite permitido.");
        }

        List<BookCopy> copies = new ArrayList<>();

        for (Long id : bookCopyIds) {

            BookCopy copy = bookCopyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado."));

            if (!copy.isAvailable()) {
                throw new IllegalStateException("Exemplar " + copy.getCode() + " não está disponível.");
            }

            copies.add(copy);
        }

        Loan loan = Loan.builder()
                .user(user)
                .handledBy(handledBy)
                .build();

        loan.calculateDueDate(configuration);

        Loan savedLoan = loanRepository.save(loan);

        for (BookCopy copy : copies) {

            LoanItem loanItem = new LoanItem(savedLoan, copy);

            loanItemRepository.save(loanItem);
        }

        Loan finalLoan = loanRepository.findById(savedLoan.getId()).orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado."));

        logService.register(LogType.LOAN_CREATED, handledBy, "Empréstimo #" + finalLoan.getId() + " foi criado para o usuário " + user.getUsername() + ".");

        for (BookCopy copy : copies) {
            logService.register(LogType.BOOK_LOANED, handledBy, "Exemplar " + copy.getCode() + " foi emprestado.");
        }

        return finalLoan;
    }

    // Atualiza observação do empréstimo
    @Transactional
    public Loan updateObservation(Long loanId, String observation, User updatedBy) {

        Loan loan = findById(loanId);

        loan.updateObservation(observation);

        Loan updatedLoan = loanRepository.save(loan);

        logService.register(LogType.LOAN_CLOSED, updatedBy, "Observação do empréstimo #" + updatedLoan.getId() + " foi atualizada.");

        return updatedLoan;
    }


    // Busca empréstimo por id
    @Transactional(readOnly = true)
    public Loan findById(Long id) {
        return loanRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado."));
    }

    // Busca empréstimo ativo do usuário
    @Transactional(readOnly = true)
    public Loan findActiveLoanByUser(User user) {
        return loanRepository.findByUserAndStatus(user, LoanStatus.OPEN).orElse(null);
    }

    // Lista empréstimos do usuário
    @Transactional(readOnly = true)
    public List<Loan> findByUser(User user) {
        return loanRepository.findByUser(user);
    }

    // Lista empréstimos por status
    @Transactional(readOnly = true)
    public List<Loan> findByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status);
    }

    // Lista empréstimos atrasados
    @Transactional(readOnly = true)
    public List<Loan> findOverdueLoans() {
        return loanRepository.findByDueDateBeforeAndStatus(LocalDate.now(), LoanStatus.OPEN);
    }

    // Lista todos os empréstimos
    @Transactional(readOnly = true)
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }
}