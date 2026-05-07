package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository
        extends JpaRepository<Configuration, Long> {

}