package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.ShelfPosition;
import com.acervo.acervoespirita.repository.ShelfPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelfPositionService {

    private final ShelfPositionRepository shelfPositionRepository;

    // Busca prateleira por id
    @Transactional(readOnly = true)
    public ShelfPosition findById(Long id) {

        return shelfPositionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prateleira não encontrada."));
    }

    // Lista todas prateleiras
    @Transactional(readOnly = true)
    public List<ShelfPosition> findAll() {

        return shelfPositionRepository.findAll();
    }
}