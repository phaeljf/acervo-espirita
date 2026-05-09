package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.BookCopy;
import com.acervo.acervoespirita.model.Location;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.BookStatus;
import com.acervo.acervoespirita.model.enums.LogType;
import com.acervo.acervoespirita.repository.BookCopyRepository;
import com.acervo.acervoespirita.repository.BookRepository;
import com.acervo.acervoespirita.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final LocationRepository locationRepository;
    private final LogService logService;

    // Cria um novo exemplar
    @Transactional
    public BookCopy createBookCopy(Long bookId, Long locationId, String code, User createdBy) {

        if (bookCopyRepository.existsByCode(code.trim().toUpperCase())) {
            throw new IllegalArgumentException("Já existe um exemplar com esse código.");
        }

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new IllegalArgumentException("Localização não encontrada."));

        BookCopy bookCopy = BookCopy.builder()
                .book(book)
                .location(location)
                .code(code)
                .build();

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        logService.register(LogType.BOOK_COPY_CREATED, createdBy, "Exemplar " + savedBookCopy.getCode() + " do livro " + book.getTitle() + " foi criado.");

        return savedBookCopy;
    }

    // Atualiza localização do exemplar
    @Transactional
    public BookCopy updateLocation(Long id, Long locationId, User updatedBy) {

        BookCopy bookCopy = findById(id);

        if (!bookCopy.isAvailable()) {
            throw new IllegalStateException("Não é possível alterar localização de exemplar emprestado.");
        }

        Location location = locationRepository.findById(locationId).orElseThrow(() -> new IllegalArgumentException("Localização não encontrada."));

        bookCopy.updateLocation(location);

        BookCopy updatedBookCopy = bookCopyRepository.save(bookCopy);

        logService.register(LogType.BOOK_COPY_UPDATED, updatedBy, "Localização do exemplar " + updatedBookCopy.getCode() + " foi atualizada.");

        return updatedBookCopy;
    }

    // Remove exemplar
    @Transactional
    public void deleteBookCopy(Long id, User deletedBy) {

        BookCopy bookCopy = findById(id);

        if (!bookCopy.isAvailable()) {
            throw new IllegalStateException("Não é possível remover exemplar emprestado.");
        }

        bookCopyRepository.delete(bookCopy);

        logService.register(LogType.BOOK_COPY_DELETED, deletedBy, "Exemplar " + bookCopy.getCode() + " foi removido.");
    }

    // Busca exemplar por id
    @Transactional(readOnly = true)
    public BookCopy findById(Long id) {
        return bookCopyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado."));
    }

    // Busca exemplar por código
    @Transactional(readOnly = true)
    public BookCopy findByCode(String code) {
        return bookCopyRepository.findByCode(code.trim().toUpperCase()).orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado."));
    }

    // Lista exemplares por livro
    @Transactional(readOnly = true)
    public List<BookCopy> findByBook(Book book) {
        return bookCopyRepository.findByBook(book);
    }

    // Lista exemplares por localização
    @Transactional(readOnly = true)
    public List<BookCopy> findByLocation(Location location) {
        return bookCopyRepository.findByLocation(location);
    }

    // Lista exemplares disponíveis
    @Transactional(readOnly = true)
    public List<BookCopy> findAvailableCopies() {
        return bookCopyRepository.findByStatus(BookStatus.AVAILABLE);
    }

    // Lista exemplares emprestados
    @Transactional(readOnly = true)
    public List<BookCopy> findLoanedCopies() {
        return bookCopyRepository.findByStatus(BookStatus.LOANED);
    }

    // Lista todos os exemplares
    @Transactional(readOnly = true)
    public List<BookCopy> findAll() {
        return bookCopyRepository.findAll();
    }
}