package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.Log;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.LogType;
import com.acervo.acervoespirita.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    // Registra um novo log no sistema
    @Transactional
    public Log register(LogType type, User user, String description) {

        if (type == null) {
            throw new IllegalArgumentException("Tipo do log é obrigatório.");
        }

        if (user == null) {
            throw new IllegalArgumentException("Usuário responsável é obrigatório.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Descrição do log é obrigatória.");
        }

        Log log = Log.builder()
                .type(type)
                .user(user)
                .description(description)
                .build();

        return logRepository.save(log);
    }

    // Busca log por id
    @Transactional(readOnly = true)
    public Log findById(Long id) {

        return logRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Log não encontrado."));
    }

    // Busca logs por usuário
    @Transactional(readOnly = true)
    public List<Log> findByUser(User user) {

        return logRepository.findByUser(user);
    }

    // Busca logs por tipo
    @Transactional(readOnly = true)
    public List<Log> findByType(LogType type) {

        return logRepository.findByType(type);
    }

    // Busca logs por usuário e tipo
    @Transactional(readOnly = true)
    public List<Log> findByUserAndType(User user, LogType type) {

        return logRepository.findByUserAndType(user, type);
    }

    // Busca logs entre datas
    @Transactional(readOnly = true)
    public List<Log> findByPeriod(Instant start, Instant end) {

        if (start == null || end == null) {
            throw new IllegalArgumentException("Período inválido.");
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Data final não pode ser anterior à inicial.");
        }

        return logRepository.findByDateTimeBetween(start, end);
    }

    // Lista logs ordenados do mais recente para o mais antigo
    @Transactional(readOnly = true)
    public List<Log> findAllOrdered() {

        return logRepository.findAllByOrderByDateTimeDesc();
    }
}