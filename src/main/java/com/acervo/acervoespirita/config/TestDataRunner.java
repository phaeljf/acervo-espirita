package com.acervo.acervoespirita.config;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.BookCopy;
import com.acervo.acervoespirita.model.Location;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.UserRole;
import com.acervo.acervoespirita.repository.UserRepository;
import com.acervo.acervoespirita.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements CommandLineRunner {

    private final ConfigurationService configurationService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final LocationService locationService;
    private final BookService bookService;
    private final BookCopyService bookCopyService;
    private final LoanService loanService;

    @Override
    public void run(String... args) {

        configurationService.initializeDefaultConfiguration();

        // =========================
        // Usuários
        // =========================

        User admin = userRepository.save(
                User.builder()
                        .name("Administrador")
                        .username("admin")
                        .email("admin@acervo.com")
                        .phone("31999999999")
                        .role(UserRole.ADMIN)
                        .build()
        );

        User staff = userService.createUser(
                User.builder()
                        .name("Trabalhador")
                        .username("staff")
                        .email("staff@acervo.com")
                        .phone("31888888888")
                        .role(UserRole.STAFF)
                        .build(),
                admin
        );

        User user = userService.createUser(
                User.builder()
                        .name("Raphael")
                        .username("rapha")
                        .email("rapha@acervo.com")
                        .phone("31777777777")
                        .role(UserRole.USER)
                        .build(),
                admin
        );

        // =========================
        // Localizações
        // =========================

        Location location1 = locationService.createLocation(
                new Location("E1", "P1"),
                admin
        );

        Location location2 = locationService.createLocation(
                new Location("E2", "P2"),
                admin
        );

        Location location3 = locationService.createLocation(
                new Location("E3", "P1"),
                admin
        );

        // =========================
        // Livros
        // =========================

        Book book1 = new Book("O Livro dos Espíritos", "Allan Kardec");
        book1.setCategory("Base Espírita");

        Book savedBook1 = bookService.createBook(book1, admin);

        Book book2 = new Book("O Livro dos Médiuns", "Allan Kardec");
        book2.setCategory("Base Espírita");

        Book savedBook2 = bookService.createBook(book2, admin);

        Book book3 = new Book("O Evangelho Segundo o Espiritismo", "Allan Kardec");
        book3.setCategory("Base Espírita");

        Book savedBook3 = bookService.createBook(book3, admin);

        // =========================
        // Cópias Livro 1
        // =========================

        bookCopyService.createBookCopy(savedBook1.getId(), location1.getId(), "OLE-001", admin);

        // =========================
        // Cópias Livro 2
        // =========================

        bookCopyService.createBookCopy(savedBook2.getId(), location1.getId(), "OLM-001", admin);

        bookCopyService.createBookCopy(savedBook2.getId(), location2.getId(), "OLM-002", admin);

        bookCopyService.createBookCopy(savedBook2.getId(), location3.getId(), "OLM-003", admin);

        // =========================
        // Cópias Livro 3
        // =========================

        bookCopyService.createBookCopy(savedBook3.getId(), location1.getId(), "ESE-001", admin);

        bookCopyService.createBookCopy(savedBook3.getId(), location1.getId(), "ESE-002", admin);

        bookCopyService.createBookCopy(savedBook3.getId(), location2.getId(), "ESE-003", admin);

        bookCopyService.createBookCopy(savedBook3.getId(), location2.getId(), "ESE-004", admin);

        bookCopyService.createBookCopy(savedBook3.getId(), location3.getId(), "ESE-005", admin);

        System.out.println("Dados de teste criados com sucesso.");

        // =========================
        // Empréstimos
        // =========================

        // Admin pega livros para ele mesmo
                loanService.createLoan(
                        admin.getId(),
                        List.of(1L, 2L),
                        admin
                );

        // Trabalhador pega livro para ele mesmo
                loanService.createLoan(
                        staff.getId(),
                        List.of(3L),
                        staff
                );

        // Admin empresta livro para usuário
                loanService.createLoan(
                        user.getId(),
                        List.of(4L, 5L),
                        admin
        );
    }


}