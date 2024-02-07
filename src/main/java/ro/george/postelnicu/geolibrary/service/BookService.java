package ro.george.postelnicu.geolibrary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.dto.author.AuthorDto;
import ro.george.postelnicu.geolibrary.dto.book.BookDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageDto;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;
import ro.george.postelnicu.geolibrary.exception.EntityValidationException;
import ro.george.postelnicu.geolibrary.mapper.BookMapper;
import ro.george.postelnicu.geolibrary.model.Author;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.Keyword;
import ro.george.postelnicu.geolibrary.model.Language;
import ro.george.postelnicu.geolibrary.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static ro.george.postelnicu.geolibrary.model.EntityName.BOOK;

@Service
public class BookService {
    public static final String NAME_IS_NOT_INCLUDED_IN_FULL_TITLE = "Name is not included in full title!";
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
        Book book = BookMapper.INSTANCE.toBook(bookDto);
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
        Book existingBook = read(id);

        if (!existingBook.getName().equalsIgnoreCase(updatedDto.getName()) &&
                repository.existsByNameIgnoreCase(updatedDto.getName())) {
            throw new EntityAlreadyExistException(BOOK, updatedDto.getName());
        }
        if (Objects.nonNull(updatedDto.getIsbn()) &&
                !existingBook.getIsbn().equalsIgnoreCase(updatedDto.getIsbn()) &&
                repository.existsByIsbnIgnoreCase(updatedDto.getIsbn())) {
            throw new EntityAlreadyExistException(BOOK, Set.of(updatedDto.getName(), updatedDto.getIsbn()));
        }
        if (Objects.nonNull(updatedDto.getBarcode()) &&
                !existingBook.getBarcode().equalsIgnoreCase(updatedDto.getBarcode()) &&
                repository.existsByBarcodeIgnoreCase(updatedDto.getBarcode())) {
            throw new EntityAlreadyExistException(BOOK, Set.of(updatedDto.getName(), updatedDto.getBarcode()));
        }
        validateNameIsContainedInFullTitle(updatedDto.getName(), updatedDto.getFullTitle());
        isbnService.isValid(updatedDto.getIsbn());
        BookMapper.INSTANCE.updateBookFromDto(updatedDto, existingBook);

        removeAuthors(updatedDto.getAuthors(), existingBook);
        saveAuthors(updatedDto.getAuthors(), existingBook);

        removeKeywords(updatedDto.getKeywords(), existingBook);
        saveKeywords(updatedDto.getKeywords(), existingBook);

        removeLanguages(updatedDto.getLanguages(), existingBook);
        saveLanguages(updatedDto.getLanguages(), existingBook);

        return repository.save(existingBook);
    }

    @Transactional(propagation = REQUIRED)
    public void delete(Long id) {
        Book book = read(id);

        removeAllAuthors(book);
        removeAllKeywords(book);
        removeAllLanguages(book);
        repository.delete(book);
    }

    private void removeAllAuthors(Book book) {
        List<Author> removeList = new ArrayList<>(book.getAuthors());
        for (Author author : removeList) {
            book.removeAuthor(author);
        }
    }

    private void removeAuthors(Set<String> authors, Book book) {
        if (authors == null || authors.isEmpty()) {
            return;
        }
        // Create a temporary list to hold authors to be removed
        List<Author> removeList = new ArrayList<>();
        for (Author author : book.getAuthors()) {
            if (!authors.contains(author.getName())) {
                removeList.add(author);
            }
        }
        // Remove authors after the iteration
        for (Author author : removeList) {
            book.removeAuthor(author);
        }
    }

    private void removeAllKeywords(Book book) {
        List<Keyword> removeList = new ArrayList<>(book.getKeywords());
        for (Keyword keyword : removeList) {
            book.removeKeyword(keyword);
        }
    }

    private void removeKeywords(Set<String> keywords, Book book) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }
        // Create a temporary list to hold keywords to be removed
        List<Keyword> removeList = new ArrayList<>();
        for (Keyword keyword : book.getKeywords()) {
            if (!keywords.contains(keyword.getName())) {
                removeList.add(keyword);
            }
        }
        // Remove keywords after the iteration
        for (Keyword keyword : removeList) {
            book.removeKeyword(keyword);
        }
    }

    private void removeAllLanguages(Book book) {
        List<Language> removeList = new ArrayList<>(book.getLanguages());
        for (Language language : removeList) {
            book.removeLanguage(language);
        }
    }

    private void removeLanguages(Set<String> languages, Book book) {
        if (languages == null || languages.isEmpty()) {
            return;
        }
        // Create a temporary list to hold languages to be removed
        List<Language> removeList = new ArrayList<>();
        for (Language language : book.getLanguages()) {
            if (!languages.contains(language.getName())) {
                removeList.add(language);
            }
        }
        // Remove languages after the iteration
        for (Language language : removeList) {
            book.removeLanguage(language);
        }
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
            throw new EntityValidationException(BOOK, NAME_IS_NOT_INCLUDED_IN_FULL_TITLE);
        }
    }

}
