package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Log;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.LogType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {

    // busca logs por usuário responsável
    List<Log> findByUser(User user);

    // busca logs por tipo
    List<Log> findByType(LogType type);

    // busca logs entre datas
    List<Log> findByDateTimeBetween(
            Instant start,
            Instant end
    );

    // lista logs ordenados do mais recente para o mais antigo
    List<Log> findAllByOrderByDateTimeDesc();

    // busca logs do usuário por tipo
    List<Log> findByUserAndType(
            User user,
            LogType type
    );




}