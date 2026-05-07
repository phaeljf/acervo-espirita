package com.acervo.acervoespirita.config;

import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.UserRole;
import com.acervo.acervoespirita.service.ConfigurationService;
import com.acervo.acervoespirita.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements CommandLineRunner {

    private final ConfigurationService configurationService;
    private final UserService userService;

    @Override
    public void run(String... args) {

        configurationService.initializeDefaultConfiguration();

        User admin = User.builder()
                .name("Administrador")
                .username("admin")
                .email("admin@email.com")
                .phone("31999999999")
                .role(UserRole.ADMIN)
                .build();

        User savedAdmin = userService.createUser(admin, admin);

        User user = User.builder()
                .name("Raphael")
                .username("rapha")
                .email("rapha@email.com")
                .phone("31988888888")
                .role(UserRole.USER)
                .build();

        userService.createUser(user, savedAdmin);

        System.out.println("Usuários criados com sucesso.");
    }
}