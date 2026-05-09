package com.acervo.acervoespirita.config;

import com.acervo.acervoespirita.model.*;
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
        // Estantes
        // =========================

        Shelf shelf1 = new Shelf("E1");
        Shelf shelf2 = new Shelf("E2");
        Shelf shelf3 = new Shelf("E3");

        ShelfPosition p1Shelf1 = new ShelfPosition("P1");
        ShelfPosition p2Shelf1 = new ShelfPosition("P2");

        ShelfPosition p1Shelf2 = new ShelfPosition("P1");
        ShelfPosition p2Shelf2 = new ShelfPosition("P2");

        ShelfPosition p1Shelf3 = new ShelfPosition("P1");

        shelf1.addPosition(p1Shelf1);
        shelf1.addPosition(p2Shelf1);

        shelf2.addPosition(p1Shelf2);
        shelf2.addPosition(p2Shelf2);

        shelf3.addPosition(p1Shelf3);

        // =========================
        // Localizações
        // =========================

        Location location1 = locationService.createLocation(
                new Location(shelf1, p1Shelf1),
                admin
        );

        Location location2 = locationService.createLocation(
                new Location(shelf2, p2Shelf2),
                admin
        );

        Location location3 = locationService.createLocation(
                new Location(shelf3, p1Shelf3),
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

        loanService.createLoan(
                admin.getId(),
                List.of(1L, 2L),
                admin
        );

        loanService.createLoan(
                staff.getId(),
                List.of(3L),
                staff
        );

        loanService.createLoan(
                user.getId(),
                List.of(4L, 5L),
                admin
        );
    }
}