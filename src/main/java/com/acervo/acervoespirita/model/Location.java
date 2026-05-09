package com.acervo.acervoespirita.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "locations", uniqueConstraints = {@UniqueConstraint(columnNames = {"shelf_id", "shelf_position_id"})})
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"shelf", "shelfPosition"})
public class Location implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_position_id", nullable = false)
    private ShelfPosition shelfPosition;

    public Location(Shelf shelf, ShelfPosition shelfPosition) {

        if (shelf == null) {
            throw new IllegalArgumentException("Estante não pode ser vazia");
        }

        if (shelfPosition == null) {
            throw new IllegalArgumentException("Prateleira não pode ser vazia");
        }

        if (shelfPosition.getShelf() == null || !shelfPosition.getShelf().equals(shelf)) {
            throw new IllegalArgumentException("Prateleira não pertence à estante informada");
        }

        this.shelf = shelf;
        this.shelfPosition = shelfPosition;
    }

    // Métodos

    public String getLocation() {
        return shelf.getName() + shelfPosition.getName();
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Location)) return false;

        Location other = (Location) o;

        return Objects.equals(shelf, other.shelf)
                && Objects.equals(shelfPosition, other.shelfPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shelf, shelfPosition);
    }
}