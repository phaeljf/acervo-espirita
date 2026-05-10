package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.BookCopy;
import com.acervo.acervoespirita.model.ShelfPosition;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.BookStatus;
import com.acervo.acervoespirita.model.enums.LogType;
import com.acervo.acervoespirita.repository.BookCopyRepository;
import com.acervo.acervoespirita.repository.BookRepository;
import com.acervo.acervoespirita.repository.ShelfPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final ShelfPositionRepository shelfPositionRepository;
    private final LogService logService;

    // Cria um novo exemplar
    @Transactional
    public BookCopy createBookCopy(Long bookId, Long shelfPositionId, String code, User createdBy) {

        if (code != null && !code.isBlank()) {
            if (bookCopyRepository.existsByCode(code.trim().toUpperCase())) {
                throw new IllegalArgumentException("Já existe um exemplar com esse código.");
            }
        }

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));

        ShelfPosition shelfPosition = shelfPositionRepository.findById(shelfPositionId)
                .orElseThrow(() -> new IllegalArgumentException("Prateleira não encontrada."));

        BookCopy bookCopy = BookCopy.builder()
                .book(book)
                .shelfPosition(shelfPosition)
                .code(code)
                .build();

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        logService.register(LogType.BOOK_COPY_CREATED, createdBy, "Exemplar " + savedBookCopy.getCode() + " do livro " + book.getTitle() + " foi criado.");
        return savedBookCopy;
    }

    // Atualiza localização do exemplar
    @Transactional
    public BookCopy updateShelfPosition(Long id, Long shelfPositionId, User updatedBy) {

        BookCopy bookCopy = findById(id);

        if (!bookCopy.isAvailable()) {
            throw new IllegalStateException("Não é possível alterar localização de exemplar emprestado.");
        }

        ShelfPosition shelfPosition = shelfPositionRepository.findById(shelfPositionId)
                .orElseThrow(() -> new IllegalArgumentException("Prateleira não encontrada."));

        bookCopy.updateShelfPosition(shelfPosition);
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

    // Lista exemplares por prateleira
    @Transactional(readOnly = true)
    public List<BookCopy> findByShelfPosition(ShelfPosition shelfPosition) {
        return bookCopyRepository.findByShelfPosition(shelfPosition);
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