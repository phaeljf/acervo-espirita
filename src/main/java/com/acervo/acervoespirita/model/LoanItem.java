package com.acervo.acervoespirita.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tb_loanItem")
@Getter
@Setter
@ToString(exclude = {"loan", "bookCopy"})
@NoArgsConstructor
public class LoanItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Loan loan;
    private BookCopy bookCopy;
    private LocalDate returnDate;

    public LoanItem(Loan loan, BookCopy bookCopy, LocalDate returnDate) {
        this.loan = loan;
        this.bookCopy = bookCopy;
        this.returnDate = returnDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LoanItem loanItem = (LoanItem) o;
        return Objects.equals(id, loanItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
