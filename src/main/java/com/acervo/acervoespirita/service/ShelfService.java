package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.Shelf;
import com.acervo.acervoespirita.repository.ShelfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelfService {

    private final ShelfRepository shelfRepository;

    @Transactional
    public Shelf createShelf(Shelf shelf) {

        if (shelfRepository.existsByName(shelf.getName())) {
            throw new IllegalArgumentException("Já existe uma estante com esse nome.");
        }

        return shelfRepository.save(shelf);
    }

    @Transactional(readOnly = true)
    public List<Shelf> findAll() {
        return shelfRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Shelf findById(Long id) {
        return shelfRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Estante não encontrada."));
    }

    @Transactional
    public Shelf save(Shelf shelf) {
        return shelfRepository.save(shelf);
    }

    @Transactional
    public void deleteShelf(Long id) {
        Shelf shelf = findById(id);
        if (!shelf.getPositions().isEmpty()) {
            throw new IllegalStateException("Não é possível remover uma estante com prateleiras.");
        }
        shelfRepository.delete(shelf);
    }

}