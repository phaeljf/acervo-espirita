package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.Book;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.BookStatus;
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
    public Book updateBook(Long id, String title, String author, String psychographedBy, User updatedBy) {

        Book book = findById(id);

        book.setTitle(title);
        book.setAuthor(author);
        book.setPsychographedBy(psychographedBy);
        Book updatedBook = bookRepository.save(book);
        logService.register(LogType.BOOK_UPDATED, updatedBy, "Livro " + updatedBook.getTitle() + " foi atualizado.");

        return updatedBook;
    }

    // Arquiva obra
    @Transactional
    public Book archiveBook(Long id, User archivedBy) {

        Book book = findById(id);
        boolean hasLoanedCopies = book.getCopies().stream().anyMatch(copy -> !copy.isAvailable());

        if (hasLoanedCopies) {
            throw new IllegalStateException("Não é possível arquivar livro com exemplares emprestados.");
        }

        book.setStatus(BookStatus.ARCHIVED);
        Book updatedBook = bookRepository.save(book);
        logService.register(LogType.BOOK_DEACTIVATED, archivedBy, "Livro " + updatedBook.getTitle() + " foi arquivado.");

        return updatedBook;
    }

    // Ativa obra
    @Transactional
    public Book activateBook(Long id, User activatedBy) {

        Book book = findById(id);
        book.setStatus(BookStatus.ACTIVE);
        Book updatedBook = bookRepository.save(book);
        logService.register(LogType.BOOK_REACTIVATED, activatedBy, "Livro " + updatedBook.getTitle() + " foi ativado.");

        return updatedBook;
    }

    // Marca como doado
    @Transactional
    public Book donateBook(Long id, User donatedBy) {

        Book book = findById(id);
        boolean hasLoanedCopies = book.getCopies().stream().anyMatch(copy -> !copy.isAvailable());
        if (hasLoanedCopies) {
            throw new IllegalStateException("Não é possível doar livro com exemplares emprestados.");
        }

        book.setStatus(BookStatus.DONATED);
        Book updatedBook = bookRepository.save(book);
        logService.register(LogType.BOOK_UPDATED, donatedBy, "Livro " + updatedBook.getTitle() + " foi marcado como doado.");

        return updatedBook;
    }

    // Deletar um livro
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
        return bookRepository.findByStatus(BookStatus.ACTIVE);
    }

    // Lista livros arquivados
    @Transactional(readOnly = true)
    public List<Book> findArchivedBooks() {
        return bookRepository.findByStatus(BookStatus.ARCHIVED);
    }

    // Lista livros doados
    @Transactional(readOnly = true)
    public List<Book> findDonatedBooks() {
        return bookRepository.findByStatus(BookStatus.DONATED);
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
                                || normalize(book.getPsychographedBy() == null ? "" : book.getPsychographedBy()).contains(normalizedBookName)
                )
                .toList();
    }
}