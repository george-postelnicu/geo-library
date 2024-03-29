package ro.george.postelnicu.geolibrary.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.george.postelnicu.geolibrary.dto.book.BookDto;
import ro.george.postelnicu.geolibrary.dto.book.BookResponseDto;
import ro.george.postelnicu.geolibrary.mapper.BookMapper;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.BookSearchCriteria;
import ro.george.postelnicu.geolibrary.model.CoverType;
import ro.george.postelnicu.geolibrary.service.BookSearchService;
import ro.george.postelnicu.geolibrary.service.BookService;

import java.net.URI;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;
import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.BOOKS;

@RestController
@RequestMapping(BOOKS)
@Validated
public class BookController {
    private final BookService service;
    private final BookSearchService searchService;

    public BookController(BookService service, BookSearchService searchService) {
        this.service = service;
        this.searchService = searchService;
    }

    @GetMapping()
    ResponseEntity<Page<BookResponseDto>> searchBooks(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "20") Integer size,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "full_title", required = false) String fullTitle,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "isbn", required = false) String isbn,
            @RequestParam(name = "barcode", required = false) String barcode,
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
        BookSearchCriteria searchCriteria = new BookSearchCriteria(name, fullTitle, description,
                isbn, barcode, authors, keywords, languages,
                publisher, coverType, minYear, maxYear, minPages, maxPages);
        Page<BookResponseDto> bookResponseDtos = searchService.search(searchCriteria,
                        PageRequest.of(page, size))
                .map(BookMapper.INSTANCE::toBookResponseDto);
        return ResponseEntity.ok(bookResponseDtos);
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<BookResponseDto> create(@RequestBody BookDto bookDto) {
        Book book = service.create(bookDto);
        URI location = fromPath(BOOKS).pathSegment("{id}")
                .buildAndExpand(book.getId()).toUri();
        BookResponseDto responseDto = BookMapper.INSTANCE.toBookResponseDto(book);

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping("/{id}")
    ResponseEntity<BookResponseDto> read(@PathVariable Long id) {
        Book book = service.read(id);

        BookResponseDto responseDto = BookMapper.INSTANCE.toBookResponseDto(book);

        return ResponseEntity.ok().body(responseDto);
    }

    @PutMapping("/{id}")
    ResponseEntity<BookResponseDto> update(@RequestBody BookDto newBook, @PathVariable Long id) {
        Book updated = service.update(id, newBook);

        BookResponseDto responseDto = BookMapper.INSTANCE.toBookResponseDto(updated);
        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
