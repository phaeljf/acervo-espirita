package com.acervo.acervoespirita.config;

import com.acervo.acervoespirita.model.Configuration;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.UserRole;
import com.acervo.acervoespirita.repository.ConfigurationRepository;
import com.acervo.acervoespirita.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class ProductionDataInitializer implements CommandLineRunner {

    private final ConfigurationRepository configurationRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {

        createDefaultConfiguration();

        createDefaultAdmin();
    }

    private void createDefaultConfiguration() {

        if (configurationRepository.existsById(1L)) {
            return;
        }

        configurationRepository.save(new Configuration());

        System.out.println("Configuração padrão criada.");
    }

    private void createDefaultAdmin() {

        if (userRepository.existsByUsername("admin")) {
            return;
        }

        User admin = User.builder()
                .name("Administrador")
                .username("admin")
                .email("admin@acervo.com")
                .phone("(31)99999-9999")
                .role(UserRole.ADMIN)
                .build();

        userRepository.save(admin);

        System.out.println("Usuário admin criado.");
    }
}