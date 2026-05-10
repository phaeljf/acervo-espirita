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
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<Shelf> shelves = new ArrayList<>();

    public Room(String roomName) {
        if (roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("Insira um nome para a sala");
        }
        this.name = roomName.trim().toUpperCase();
    }

    // Métodos
    public void addShelf(Shelf shelf) {

        if (shelf == null) {
            throw new IllegalArgumentException("Estante inválida");
        }

        shelves.add(shelf);
        shelf.setRoom(this);
    }

    public void removeShelf(Shelf shelf) {

        shelves.remove(shelf);
        shelf.setRoom(null);
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}