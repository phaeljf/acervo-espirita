package com.acervo.acervoespirita.model;

import com.acervo.acervoespirita.model.enums.LoanStatus;
import com.acervo.acervoespirita.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name="tb_loan")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"handledBy","user"})
public class Loan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by_id")
    private User handledBy;

    private LocalDate loanDate;

    private LoanStatus status;



    @Builder
    public Loan(User user, LocalDate loanDate, User handledBy) {
        this.user = user;
        this.loanDate = loanDate;
        this.handledBy = handledBy;
    }

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

    public boolean isActive() {
        return status == LoanStatus.OPEN;
    }
}
