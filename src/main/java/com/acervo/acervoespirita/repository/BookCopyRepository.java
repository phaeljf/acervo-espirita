package com.acervo.acervoespirita.repository;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.BookCopy;
import com.acervo.acervoespirita.model.ShelfPosition;
import com.acervo.acervoespirita.model.enums.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {


    // busca exemplar pelo código
    Optional<BookCopy> findById(Long id);

    // busca exemplar pelo código
    Optional<BookCopy> findByCode(String code);

    // verifica se código já existe
    boolean existsByCode(String code);

    // busca exemplares por status
    List<BookCopy> findByStatus(BookCopyStatus status);

    // busca exemplares de um livro
    List<BookCopy> findByBook(Book book);

    // busca exemplares por localização
    List<BookCopy> findByShelfPosition(ShelfPosition shelfPosition);



}