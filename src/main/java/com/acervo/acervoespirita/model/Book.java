package com.acervo.acervoespirita.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Book implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String author;

    @Column
    private String psychographedBy;

    @Column
    private String category;

    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<BookCopy> copies = new ArrayList<>();

    public Book(String title, String author, String psychographedBy) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Título da obra não pode ser em branco");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Nome do Autor não pode ser em branco");
        }

        this.title = title.trim();
        this.author = author.trim();
        this.psychographedBy = psychographedBy != null ? psychographedBy.trim() : null;
        this.active = true;
    }

    // Controle do relacionamento
    public void addCopy(BookCopy copy) {
        copies.add(copy);
        copy.setBook(this);
    }

    public void removeCopy(BookCopy copy) {
        copies.remove(copy);
        copy.setBook(null);
    }

    public List<BookCopy> getCopies() {
        return Collections.unmodifiableList(copies);
    }

    // Controle do Livro
    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    // Controle de cópias
    public boolean hasCopies() {
        return !copies.isEmpty();
    }

    public long getTotalCopies() {
        return copies.size();
    }

    public long getAvailableCopies() {
        return copies.stream()
                .filter(BookCopy::isAvailable)
                .count();
    }

    public boolean hasAvailableCopies() {
        return getAvailableCopies() > 0;
    }

    // Equals e HashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book other = (Book) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}