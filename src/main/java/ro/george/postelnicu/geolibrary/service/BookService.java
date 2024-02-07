package ro.george.postelnicu.geolibrary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.dto.book.BookDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageDto;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;
import ro.george.postelnicu.geolibrary.exception.EntityValidationException;
import ro.george.postelnicu.geolibrary.mapper.LibraryMapper;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.repository.BookRepository;

import java.util.Objects;
import java.util.Set;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static ro.george.postelnicu.geolibrary.model.EntityName.BOOK;

@Service
public class BookService {
    private final BookRepository repository;
    private final IsbnService isbnService;
    private final AuthorService authorService;
    private final KeywordService keywordService;
    private final LanguageService languageService;

    @Autowired
    public BookService(BookRepository repository, IsbnService isbnService,
                       AuthorService authorService, KeywordService keywordService,
                       LanguageService languageService) {
        this.repository = repository;
        this.isbnService = isbnService;
        this.authorService = authorService;
        this.keywordService = keywordService;
        this.languageService = languageService;
    }

    @Transactional(propagation = REQUIRED)
    public Book create(BookDto bookDto) {
        Book book = LibraryMapper.INSTANCE.toBook(bookDto);
        if (repository.existsByNameIgnoreCase(book.getName())) {
            throw new EntityAlreadyExistException(BOOK, book.getName());
        }
        if (Objects.nonNull(book.getIsbn()) && repository.existsByIsbnIgnoreCase(book.getIsbn())) {
            throw new EntityAlreadyExistException(BOOK, Set.of(book.getName(), book.getIsbn()));
        }
        if (Objects.nonNull(book.getBarcode()) && repository.existsByBarcodeIgnoreCase(book.getBarcode())) {
            throw new EntityAlreadyExistException(BOOK, Set.of(book.getName(), book.getBarcode()));
        }
        validateNameIsContainedInFullTitle(book.getName(), book.getFullTitle());
        isbnService.isValid(book.getIsbn());

        saveAuthors(bookDto.getAuthors(), book);
        saveKeywords(bookDto.getKeywords(), book);
        saveLanguages(bookDto.getLanguages(), book);

        return repository.save(book);
    }
    @Transactional(readOnly = true, propagation = REQUIRED)
    public Book read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(BOOK, id));
    }

    @Transactional(propagation = REQUIRED)
    public Book update(Long id, BookDto updatedDto) {
        Book existingBook = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(BOOK, id));
        Book updatedBook = LibraryMapper.INSTANCE.toBook(updatedDto);
        if (!existingBook.getName().equalsIgnoreCase(updatedBook.getName()) &&
                repository.existsByNameIgnoreCase(updatedBook.getName())) {
            throw new EntityAlreadyExistException(BOOK, updatedBook.getName());
        }
        if (Objects.nonNull(updatedBook.getIsbn()) &&
                !existingBook.getIsbn().equalsIgnoreCase(updatedBook.getIsbn()) &&
                repository.existsByIsbnIgnoreCase(updatedBook.getIsbn())) {
            throw new EntityAlreadyExistException(BOOK, Set.of(updatedBook.getName(), updatedBook.getIsbn()));
        }
        if (Objects.nonNull(updatedBook.getBarcode()) &&
                !existingBook.getBarcode().equalsIgnoreCase(updatedBook.getBarcode()) &&
                repository.existsByBarcodeIgnoreCase(updatedBook.getBarcode())) {
            throw new EntityAlreadyExistException(BOOK, Set.of(updatedBook.getName(), updatedBook.getBarcode()));
        }
        validateNameIsContainedInFullTitle(updatedBook.getName(), updatedBook.getFullTitle());
        isbnService.isValid(updatedBook.getIsbn());

        existingBook.setName(updatedBook.getName());
        existingBook.setFullTitle(updatedBook.getFullTitle());
        existingBook.setDescription(updatedBook.getDescription());
        existingBook.setPublisher(updatedBook.getPublisher());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setCover(updatedBook.getCover());
        existingBook.setPublishYear(updatedBook.getPublishYear());
        existingBook.setPages(updatedBook.getPages());
        existingBook.setBarcode(updatedBook.getBarcode());
        existingBook.setStatus(updatedBook.getStatus());

        existingBook.getAuthors().clear();
        saveAuthors(updatedDto.getAuthors(), existingBook);

        existingBook.getKeywords().clear();
        saveKeywords(updatedDto.getKeywords(), existingBook);

        existingBook.getLanguages().clear();
        saveLanguages(updatedDto.getLanguages(), existingBook);

        return repository.save(existingBook);
    }

    private void saveAuthors(Set<String> authors, Book book) {
        if (authors == null || authors.isEmpty()) {
            return;
        }
        authors.stream()
                .map(AuthorDto::new)
                .map(authorService::createIfNotExisting)
                .forEach(book::addAuthor);
    }

    private void saveKeywords(Set<String> keywords, Book book) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }
        keywords.stream()
                .map(KeywordDto::new)
                .map(keywordService::createIfNotExisting)
                .forEach(book::addKeyword);
    }

    private void saveLanguages(Set<String> languages, Book book) {
        if (languages == null || languages.isEmpty()) {
            return;
        }
        languages.stream()
                .map(LanguageDto::new)
                .map(languageService::createIfNotExisting)
                .forEach(book::addLanguage);
    }

    private void validateNameIsContainedInFullTitle(String name, String fullTitle) {
        if (fullTitle != null && !fullTitle.toLowerCase().contains(name.toLowerCase())) {
            throw new EntityValidationException(BOOK, "Name is not included in full title!");
        }
    }

}
