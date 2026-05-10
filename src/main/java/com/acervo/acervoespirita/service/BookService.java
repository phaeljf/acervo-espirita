package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.LogType;
import com.acervo.acervoespirita.repository.BookCopyRepository;
import com.acervo.acervoespirita.repository.BookRepository;
import com.acervo.acervoespirita.repository.LoanItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LoanItemRepository loanItemRepository;
    private final LogService logService;

    // Cria uma nova obra
    @Transactional
    public Book createBook(Book book, User createdBy) {

        Book savedBook = bookRepository.save(book);

        logService.register(LogType.BOOK_CREATED, createdBy, "Livro " + savedBook.getTitle() + " foi criado.");

        return savedBook;
    }

    // Atualiza dados da obra
    @Transactional
    public Book updateBook(Long id, String title, String author, String psychographedBy, String category, User updatedBy) {

        Book book = findById(id);

        book.setTitle(title);
        book.setAuthor(author);
        book.setPsychographedBy(psychographedBy);
        book.setCategory(category);

        Book updatedBook = bookRepository.save(book);

        logService.register(LogType.BOOK_UPDATED, updatedBy, "Livro " + updatedBook.getTitle() + " foi atualizado.");

        return updatedBook;
    }

    // Inativa obra
    @Transactional
    public Book deactivateBook(Long id, User deactivatedBy) {

        Book book = findById(id);

        boolean hasLoanedCopies = book.getCopies().stream().anyMatch(copy -> !copy.isAvailable());

        if (hasLoanedCopies) {
            throw new IllegalStateException("Não é possível inativar livro com exemplares emprestados.");
        }

        book.deactivate();

        Book updatedBook = bookRepository.save(book);

        logService.register(LogType.BOOK_DEACTIVATED, deactivatedBy, "Livro " + updatedBook.getTitle() + " foi inativado.");

        return updatedBook;
    }

    // Reativa obra
    @Transactional
    public Book activateBook(Long id, User activatedBy) {

        Book book = findById(id);

        book.activate();

        Book updatedBook = bookRepository.save(book);

        logService.register(LogType.BOOK_REACTIVATED, activatedBy, "Livro " + updatedBook.getTitle() + " foi reativado.");

        return updatedBook;
    }

    //Deletar um livro
    @Transactional
    public void deleteBook(Long id, User deletedBy) {

        Book book = findById(id);

        if (book.hasCopies()) {
            throw new IllegalStateException("Não é possível remover livro que possui exemplares.");
        }

        bookRepository.delete(book);

        logService.register(LogType.BOOK_DELETED, deletedBy, "Livro " + book.getTitle() + " foi removido.");
    }

    // Busca livro por id
    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));
    }

    // Busca livros por título
    @Transactional(readOnly = true)
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    // Busca livros por autor
    @Transactional(readOnly = true)
    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    // Busca livros por categoria
    @Transactional(readOnly = true)
    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategoryContainingIgnoreCase(category);
    }

    // Lista livros ativos
    @Transactional(readOnly = true)
    public List<Book> findActiveBooks() {
        return bookRepository.findByActiveTrue();
    }

    // Lista livros inativos
    @Transactional(readOnly = true)
    public List<Book> findInactiveBooks() {
        return bookRepository.findByActiveFalse();
    }

    // Lista todos os livros
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    public List<Book> findByTitleNormalize(String bookName) {
        String normalizedBookName = normalize(bookName);
        return findAll().stream()
                .filter(book ->
                        normalize(book.getTitle()).contains(normalizedBookName)
                                || normalize(book.getAuthor()).contains(normalizedBookName)
                                || normalize(book.getPsychographedBy()).contains(normalizedBookName)
                )
                .toList();
    }
}