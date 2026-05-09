package com.acervo.acervoespirita.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "locations", uniqueConstraints = {@UniqueConstraint(columnNames = {"shelf", "position"})})
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Location implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String shelf;

    @NotBlank
    @Column(nullable = false)
    private String position;

    public Location(String shelf, String position) {
        if (shelf == null || shelf.isBlank()) {
            throw new IllegalArgumentException("Estante não pode ser vazia");
        }
        if (position == null || position.isBlank()) {
            throw new IllegalArgumentException("Prateleira não pode ser vazia");
        }

        this.shelf = shelf.trim().toUpperCase();
        this.position = position.trim().toUpperCase();
    }

    // tratamento da criação de estante

    public String getLocation() {
        return shelf + position;
    }

    // Equals hashcode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location other = (Location) o;

        return shelf.equals(other.shelf)
                && position.equals(other.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shelf, position);
    }
}