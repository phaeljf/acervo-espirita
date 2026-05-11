package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.*;
import com.acervo.acervoespirita.model.enums.LogType;
import com.acervo.acervoespirita.model.enums.LoanStatus;
import com.acervo.acervoespirita.repository.*;
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
    private final ShelfPositionRepository shelfPositionRepository;

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

    // Finaliza empréstimo devolvendo múltiplos itens
    @Transactional
    public Loan finishLoan(Long loanId, List<Long> loanItemIds, Long shelfPositionId, String observation, User handledBy) {

        Loan loan = findById(loanId);

        if (!loan.isActive()) {
            throw new IllegalStateException("Empréstimo já está encerrado.");
        }

        if (loanItemIds == null || loanItemIds.isEmpty()) {
            throw new IllegalArgumentException("Nenhum item foi selecionado para devolução.");
        }

        ShelfPosition shelfPosition = shelfPositionRepository.findById(shelfPositionId)
                .orElseThrow(() -> new IllegalArgumentException("Prateleira não encontrada."));

        for (Long loanItemId : loanItemIds) {

            LoanItem loanItem = loanItemRepository.findById(loanItemId).orElseThrow(() -> new IllegalArgumentException("Item do empréstimo não encontrado."));

            if (!loanItem.getLoan().equals(loan)) {
                throw new IllegalStateException("Item não pertence ao empréstimo informado.");
            }

            if (!loanItem.isReturned()) {
                loanItem.markAsReturned(shelfPosition);
                loanItemRepository.save(loanItem);
                logService.register(LogType.BOOK_RETURNED,handledBy,"Exemplar " + loanItem.getBookCopy().getCode() + " foi devolvido.");
            }
        }

        boolean allReturned = loan.getItems().stream().allMatch(LoanItem::isReturned);

        if (!allReturned) {
            throw new IllegalStateException("Ainda existem livros pendentes neste empréstimo.");
        }

        loan.close(observation);

        Loan updatedLoan = loanRepository.save(loan);

        logService.register(
                LogType.LOAN_CLOSED,
                handledBy,
                "Empréstimo #" + updatedLoan.getId() + " foi encerrado."
        );

        return updatedLoan;
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

    // Busca empréstimos ativos pelo nome do usuário
    @Transactional(readOnly = true)
    public List<Loan> findOpenLoansByUserName(String name) {
        return loanRepository.findByUser_NameContainingIgnoreCaseAndStatus(name, LoanStatus.OPEN);
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