package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByName(String name);

    Optional<Room> findByName(String name);

}