package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<User, Long> {

}