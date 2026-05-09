package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Location;
import com.acervo.acervoespirita.model.Shelf;
import com.acervo.acervoespirita.model.ShelfPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    // busca localização específica
    Optional<Location> findByShelfAndShelfPosition(Shelf shelf, ShelfPosition shelfPosition);

    // verifica se localização já existe
    boolean existsByShelfAndShelfPosition(Shelf shelf, ShelfPosition shelfPosition);

    // lista ordenada das localizações
    List<Location> findAllByOrderByShelf_NameAscShelfPosition_NameAsc();

}