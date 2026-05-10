package com.acervo.acervoespirita.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "loan_items")
@Getter
@NoArgsConstructor
@ToString(exclude = {"loan", "bookCopy"})
public class LoanItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    private LocalDate returnDate;

    public LoanItem(Loan loan, BookCopy bookCopy) {
        if (loan == null) {
            throw new IllegalArgumentException("Loan é obrigatório");
        }
        if (bookCopy == null) {
            throw new IllegalArgumentException("BookCopy é obrigatório");
        }

        this.loan = loan;
        this.bookCopy = bookCopy;

        bookCopy.markAsLoaned();
    }

    // Métodos

    public boolean isReturned() {
        return returnDate != null;
    }

    public void markAsReturned(ShelfPosition shelfPosition) {

        if (isReturned()) {
            throw new IllegalStateException("Livro já devolvido");
        }
        this.returnDate = LocalDate.now();
        this.bookCopy.markAsReturned(shelfPosition);
    }

    // Equals and HashCode
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