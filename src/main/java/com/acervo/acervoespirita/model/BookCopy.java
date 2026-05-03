package com.acervo.acervoespirita.model;

import com.acervo.acervoespirita.model.enums.BookStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "book_copies")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"book", "location"})
public class BookCopy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Book book;
    private BookStatus status;
    private Location location;
    private String code;

    @Builder
    public BookCopy(Book book, BookStatus status, Location location, String code) {
        this.book = book;
        this.status = status;
        this.location = location;
        this.code = code;
    }

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

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }
}
