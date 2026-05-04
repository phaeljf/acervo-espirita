package com.acervo.acervoespirita.model;

import com.acervo.acervoespirita.model.enums.LoanStatus;
import com.acervo.acervoespirita.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="loans")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"handledBy","user","items"})
public class Loan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by_id", nullable = false)
    private User handledBy;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    private Instant loanDate;

    private LocalDate dueDate;

    @Builder
    public Loan(User user, User handledBy) {
        if (user == null){
            throw new IllegalArgumentException("É necessário informar o usuário!");
        }
        if (handledBy == null){
            throw new IllegalArgumentException("É necessário informar o  trabalhador!");
        }

        this.user = user;
        this.handledBy = handledBy;
        this.loanDate = Instant.now();
        this.status = LoanStatus.OPEN;
    }


    //Comportamento
    public void calculateDueDate(Configuration config) {
        if (config == null || config.getLoanDaysLimit() == 0) {
            this.dueDate = LocalDate.of(2099, 12, 31);
        } else {
            // Usa a data do empréstimo (convertida para LocalDate) como base
            this.dueDate = LocalDate.now().plusDays(config.getLoanDaysLimit());
        }
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    public void close() {
        this.status = LoanStatus.CLOSED;
    }

    public boolean isActive() {
        return status == LoanStatus.OPEN;
    }



    //Equals e Hashcode

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
