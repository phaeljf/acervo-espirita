package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.UserRole;
import com.acervo.acervoespirita.model.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // localização do e-mail para login
    Optional<User> findByEmail(String email);

    // busca por nome mesmo parcial
    List<User> findByNameContainingIgnoreCase(String name);

    // verificação se o username já não foi criado
    Optional<User> findByUsername(String username);

    //métodos criados para verificar se email ou username já existem
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    // lista todos os usuários de acordo com status
    List<User> findByStatus(UserStatus status);

    // encontra usuários pela função
    List<User> findByRole(UserRole role);

}