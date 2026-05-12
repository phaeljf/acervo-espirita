package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Loan;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // busca empréstimo ativo do usuário
    Optional<Loan> findByUserAndStatus(
            User user,
            LoanStatus status
    );

    // verifica se usuário possui empréstimo ativo
    boolean existsByUserAndStatus(
            User user,
            LoanStatus status
    );

    // lista empréstimos por status
    List<Loan> findByStatus(LoanStatus status);

    // lista empréstimos do usuário
    List<Loan> findByUser(User user);

    // lista empréstimos atrasados
    List<Loan> findByDueDateBeforeAndStatus(
            LocalDate dueDate,
            LoanStatus status
    );

    // Busca pro usuario
    List<Loan> findByUser_NameContainingIgnoreCaseAndStatus(String name, LoanStatus status);

    List<Loan> findByUser_NameContainingIgnoreCase(String name);



}


