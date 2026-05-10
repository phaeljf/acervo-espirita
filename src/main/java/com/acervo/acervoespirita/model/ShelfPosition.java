package com.acervo.acervoespirita.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ShelfPosition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;

    public ShelfPosition(String positionName) {
        if (positionName == null || positionName.isBlank()) {
            throw new IllegalArgumentException("Insira um nome para a prateleira");
        }
        this.name = positionName.trim().toUpperCase();
    }

    // Métodos

    public String getFullPosition() {
        if (shelf == null) {
            return name;
        }
        return shelf.getRoom().getName()
                + " → "
                + shelf.getName()
                + " → "
                + name;
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) return false;

        ShelfPosition that = (ShelfPosition) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}