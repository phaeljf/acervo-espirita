package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    boolean existsByName(String name);

    Optional<Shelf> findByName(String name);

}