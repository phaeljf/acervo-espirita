package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.Room;
import com.acervo.acervoespirita.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public Room createRoom(Room room) {
        if (roomRepository.existsByName(room.getName())) {
            throw new IllegalArgumentException("Já existe uma sala com esse nome.");
        }
        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public Room findById(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Sala não encontrada."));
    }

    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = findById(id);
        if (!room.getShelves().isEmpty()) {
            throw new IllegalStateException("Não é possível remover sala com estantes.");
        }
        roomRepository.delete(room);
    }
}