package ro.george.postelnicu.geolibrary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.dto.BookDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageDto;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.mapper.LibraryMapper;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.repository.BookRepository;

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
    public BookService(BookRepository repository,
                       IsbnService isbnService, AuthorService authorService,
                       KeywordService keywordService, LanguageService languageService) {
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
        validateName(book.getName(), book.getFullTitle());
        isbnService.isValid(book.getIsbn());

        saveAuthors(bookDto.getAuthors(), book);
        saveKeywords(bookDto.getKeywords(), book);
        saveLanguages(bookDto.getLanguages(), book);

        return repository.save(book);
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

    private void validateName(String name, String fullTitle) {
        if (fullTitle != null && !fullTitle.contains(name)) {
            throw new EntityAlreadyExistException(BOOK, Set.of(name, fullTitle));
        }
    }

}
