package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.BookCopy;
import com.acervo.acervoespirita.model.Location;
import com.acervo.acervoespirita.model.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    // busca exemplar pelo código único
    Optional<BookCopy> findByCode(String code);

    // verifica se código já existe
    boolean existsByCode(String code);

    // busca exemplares por status
    List<BookCopy> findByStatus(BookStatus status);

    // busca exemplares de um livro
    List<BookCopy> findByBook(Book book);

    // busca exemplares disponíveis de um livro
    List<BookCopy> findByBookAndStatus(
            Book book,
            BookStatus status
    );

    // busca exemplares por localização
    List<BookCopy> findByLocation(Location location);


}