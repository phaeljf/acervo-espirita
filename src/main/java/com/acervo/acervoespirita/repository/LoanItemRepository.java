package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.BookCopy;
import com.acervo.acervoespirita.model.Loan;
import com.acervo.acervoespirita.model.LoanItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanItemRepository extends JpaRepository<LoanItem, Long> {

    // lista itens de um empréstimo
    List<LoanItem> findByLoan(Loan loan);

    // busca item pelo exemplar físico
    Optional<LoanItem> findByBookCopy(BookCopy bookCopy);

    // lista itens ainda não devolvidos
    List<LoanItem> findByReturnDateIsNull();

    // lista itens devolvidos
    List<LoanItem> findByReturnDateIsNotNull();

    // busca item de um empréstimo específico
    Optional<LoanItem> findByLoanAndBookCopy(
            Loan loan,
            BookCopy bookCopy
    );

    LoanItem save(LoanItem loanItem);

    boolean existsByBookCopyBook(Book book);

}