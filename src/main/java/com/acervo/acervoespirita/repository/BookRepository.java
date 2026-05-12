package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // busca parcial por título
    List<Book> findByTitleContainingIgnoreCase(String title);

    // busca parcial por autor
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // busca parcial por psicografado
    List<Book> findByPsychographedByContainingIgnoreCase(String psychographedBy);

    // busca por categoria
    List<Book> findByCategoryContainingIgnoreCase(String category);

    // busca por status
    List<Book> findByStatus(BookStatus status);
}