package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Shelf;
import com.acervo.acervoespirita.model.ShelfPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShelfPositionRepository extends JpaRepository<ShelfPosition, Long> {

    List<ShelfPosition> findByShelf(Shelf shelf);

    boolean existsByShelfAndName(Shelf shelf, String name);

}