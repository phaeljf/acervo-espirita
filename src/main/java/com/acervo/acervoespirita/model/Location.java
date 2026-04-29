package com.acervo.acervoespirita.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name ="tb_location")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Location implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String shelf; //estante
    private String position; //prateleira

    @Builder
    public Location(String position, String shelf) {
        this.position = position;
        this.shelf = shelf;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
