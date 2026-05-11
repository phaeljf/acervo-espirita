package com.acervo.acervoespirita.model;

import com.acervo.acervoespirita.model.enums.BookCopyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "book_copies")
@Getter
@NoArgsConstructor
@ToString(exclude = {"book", "shelfPosition"})
public class BookCopy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_position_id")
    private ShelfPosition shelfPosition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookCopyStatus status;

    @Column(unique = true)
    private String code;

    @Builder
    public BookCopy(Book book, ShelfPosition shelfPosition, String code) {

        if (book == null) {
            throw new IllegalArgumentException("Livro é obrigatório");
        }
        this.book = book;
        this.shelfPosition = shelfPosition;
        this.code = (code == null || code.isBlank()) ? null : code.trim().toUpperCase();
        this.status = BookCopyStatus.AVAILABLE;
    }

    // Métodos

    public boolean isAvailable() {
        return status == BookCopyStatus.AVAILABLE;
    }

    public void markAsLoaned() {

        if (!isAvailable()) {
            throw new IllegalStateException("Cópia não está disponível");
        }

        this.status = BookCopyStatus.LOANED;
        this.shelfPosition = null;
    }

    public void markAsReturned(ShelfPosition shelfPosition) {

        if (shelfPosition == null) {
            throw new IllegalArgumentException("Localização obrigatória");
        }

        this.status = BookCopyStatus.AVAILABLE;
        this.shelfPosition = shelfPosition;
    }

    public void updateData(ShelfPosition shelfPosition, String code, BookCopyStatus status) {
        if (shelfPosition == null && status == BookCopyStatus.AVAILABLE) {
            throw new IllegalArgumentException("Exemplar disponível precisa possuir localização.");
        }
        if (status == BookCopyStatus.LOANED) {
            throw new IllegalArgumentException("Para emprestar um exemplar utilize o sistema de empréstimos.");
        }

        this.shelfPosition = shelfPosition;
        this.code = (code == null || code.isBlank())
                ? null
                : code.trim().toUpperCase();
        this.status = status;
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) return false;

        BookCopy bookCopy = (BookCopy) o;

        return Objects.equals(id, bookCopy.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}