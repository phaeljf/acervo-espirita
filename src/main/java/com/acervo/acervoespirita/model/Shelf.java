package com.acervo.acervoespirita.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Shelf implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<ShelfPosition> positions = new ArrayList<>();

    public Shelf(String shelfName) {
        if (shelfName == null || shelfName.isBlank()) {
            throw new IllegalArgumentException("Insira um nome para sua Estante");
        }
        this.name = shelfName.trim().toUpperCase();
    }

    // Métodos

    public void addPosition(ShelfPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Prateleira inválida");
        }
        positions.add(position);
        position.setShelf(this);
    }

    public void removePosition(ShelfPosition position) {
        positions.remove(position);
        position.setShelf(null);
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Shelf shelf = (Shelf) o;
        return Objects.equals(id, shelf.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}