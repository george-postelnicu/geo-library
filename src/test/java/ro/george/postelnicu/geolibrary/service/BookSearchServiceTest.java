package ro.george.postelnicu.geolibrary.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ro.george.postelnicu.geolibrary.AbstractIntegrationTest;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.BookSearchCriteria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ro.george.postelnicu.geolibrary.DataCommon.*;


class BookSearchServiceTest extends AbstractIntegrationTest {

    private final BookService bookService;
    private final BookSearchService searchService;

    @Autowired
    BookSearchServiceTest(BookService bookService, BookSearchService searchService) {
        this.bookService = bookService;
        this.searchService = searchService;
    }

    @BeforeEach
    void beforeEach() {
        bookService.create(getLandscapesOfIdentityBook());
        bookService.create(anotherEnglishBook());
    }

    @Test
    void search() {
        BookSearchCriteria searchCriteria = new BookSearchCriteria(null, null, null,
                null, null, null,
                ART_MUSEUM_OF_ESTONIA, null, null,
                null, null, null);
        Page<Book> books = searchService.search(searchCriteria, Pageable.ofSize(20));
        assertEquals(2, books.getTotalElements());
    }
}