package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    // busca localização específica
    Optional<Location> findByShelfAndPosition(
            String shelf,
            String position
    );

    // verifica se localização já existe
    boolean existsByShelfAndPosition(
            String shelf,
            String position
    );

    // lista ordenada das localizações
    List<Location> findAllByOrderByShelfAscPositionAsc();

}