package com.acervo.acervoespirita.model;

import com.acervo.acervoespirita.model.enums.BookStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "book_copies")
@Getter
@NoArgsConstructor
@ToString(exclude = {"book", "location"})
public class BookCopy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;

    @Column(nullable = false, unique = true)
    private String code;

    @Builder
    public BookCopy(Book book, BookStatus status, Location location, String code) {
        if (book == null) {
            throw new IllegalArgumentException("Livro é obrigatório");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Código da cópia é obrigatório");
        }

        this.book = book;
        this.location = location;
        this.code = code.trim().toUpperCase();
        this.status = BookStatus.AVAILABLE;
    }

    // Metodo de verificação

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

    public void markAsLoaned() {
        if (!isAvailable()) {
            throw new IllegalStateException("Cópia não está disponível");
        }
        this.status = BookStatus.LOANED;
        this.location = null;
    }

    public void markAsReturned(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Localização obrigatória");
        }

        this.status = BookStatus.AVAILABLE;
        this.location = location;
    }

    public void updateLocation(Location location) {
        this.location = location;
    }

    // Equals hashcode
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
