package ro.george.postelnicu.geolibrary.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.george.postelnicu.geolibrary.dto.BookDto;
import ro.george.postelnicu.geolibrary.dto.book.BookResponseDto;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;
import ro.george.postelnicu.geolibrary.mapper.LibraryMapper;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.BookSearchCriteria;
import ro.george.postelnicu.geolibrary.model.CoverType;
import ro.george.postelnicu.geolibrary.repository.BookRepository;
import ro.george.postelnicu.geolibrary.service.BookSearchService;
import ro.george.postelnicu.geolibrary.service.BookService;

import java.util.Set;

import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.BOOKS;

@RestController
@RequestMapping(BOOKS)
@Validated
public class BookController {
    private final BookService service;
    private final BookSearchService searchService;
    private final BookRepository repository;

    public BookController(BookService service, BookSearchService searchService, BookRepository repository) {
        this.service = service;
        this.searchService = searchService;
        this.repository = repository;
    }

    @GetMapping()
    ResponseEntity<Page<BookResponseDto>> searchBooks(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "20") Integer size,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "full_title", required = false) String fullTitle,
            @RequestParam(name = "isbn", required = false) String isbn,
            @RequestParam(name = "authors", required = false) Set<String> authors,
            @RequestParam(name = "keywords", required = false) Set<String> keywords,
            @RequestParam(name = "languages", required = false) Set<String> languages,
            @RequestParam(name = "publisher", required = false) String publisher,
            @RequestParam(name = "cover_type", required = false) CoverType coverType,
            @RequestParam(name = "min_year", required = false) Integer minYear,
            @RequestParam(name = "max_year", required = false) Integer maxYear,
            @RequestParam(name = "min_pages", required = false) Integer minPages,
            @RequestParam(name = "max_pages", required = false) Integer maxPages
    ) {
        BookSearchCriteria searchCriteria = new BookSearchCriteria(name, fullTitle, isbn, authors, keywords, languages,
                publisher, coverType, minYear, maxYear, minPages, maxPages);
        Page<BookResponseDto> bookResponseDtos = searchService.search(searchCriteria,
                        PageRequest.of(page, size))
                .map(LibraryMapper.INSTANCE::toBookResponseDto);
        return ResponseEntity.ok(bookResponseDtos);
    }

    @PostMapping()
    Book create(@RequestBody BookDto bookDto) {
        Book book = service.create(bookDto);

        return repository.save(book);
    }

    @GetMapping("/{id}")
    Book read(@PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("book", id));
    }

    @PutMapping("/{id}")
    Book update(@RequestBody Book newBook, @PathVariable Long id) {

        return repository.findById(id)
                .map(book -> {
                    book.setName(newBook.getName());
                    book.setIsbn(newBook.getIsbn());
                    return repository.save(book);
                })
                .orElseGet(() -> {
                    newBook.setId(id);
                    return repository.save(newBook);
                });
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
