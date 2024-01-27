package ro.george.postelnicu.geolibrary.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ro.george.postelnicu.geolibrary.AbstractIntegrationTest;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.BookSearchCriteria;
import ro.george.postelnicu.geolibrary.model.CoverType;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ro.george.postelnicu.geolibrary.DataCommon.*;
import static ro.george.postelnicu.geolibrary.model.CoverType.*;


class BookSearchServiceTest extends AbstractIntegrationTest {

    public static final Pageable SIZE = Pageable.ofSize(20);
    private final BookService bookService;
    private final BookSearchService searchService;

    @Autowired
    BookSearchServiceTest(BookService bookService, BookSearchService searchService) {
        this.bookService = bookService;
        this.searchService = searchService;
    }

    @BeforeEach
    void beforeEach() {
        bookService.create(landscapesOfIdentity());
        bookService.create(conflictsAndAdaptations());
        bookService.create(oneHundredStepsThrough20thCenturyEstonianArchitecture());
        bookService.create(oneHundredFiftyHouses());
    }

    @Test
    void search_shouldReturnBooks_whenFilteringByPublisher() {
        BookSearchCriteria artMuseumOfEstoniaPublisher = getBSC_byPublisher(ART_MUSEUM_OF_ESTONIA);
        BookSearchCriteria notFound = getBSC_byPublisher(NOT_FOUND);

        Page<Book> books = searchService.search(artMuseumOfEstoniaPublisher, SIZE);
        assertEquals(2, books.getTotalElements());
        assertEquals(estonianArtBooks(), getNameOfBooks(books));

        books = searchService.search(notFound, SIZE);
        assertEquals(0, books.getTotalElements());
    }

    @Test
    void search_shouldReturnBooks_whenFilteringByCoverType() {
        BookSearchCriteria hardcoverBooks = getBSC_byCoverType(HARDCOVER);
        BookSearchCriteria softCoverWithDustJacketBooks = getBSC_byCoverType(SOFTCOVER_WITH_DUST_JACKET);
        BookSearchCriteria softCoverBooks = getBSC_byCoverType(SOFTCOVER);

        Page<Book> books = searchService.search(hardcoverBooks, SIZE);
        assertEquals(1, books.getTotalElements());

        books = searchService.search(softCoverWithDustJacketBooks, SIZE);
        assertEquals(3, books.getTotalElements());

        books = searchService.search(softCoverBooks, SIZE);
        assertEquals(0, books.getTotalElements());
    }

    @Test
    void search_shouldFindBooks_byPublishYearMinMax() {
        BookSearchCriteria min_2023 = getBSC_byMinMaxPublishYear(CONFLICTS_PUBLISH_YEAR, null);
        BookSearchCriteria max_2023 = getBSC_byMinMaxPublishYear(null, CONFLICTS_PUBLISH_YEAR);
        BookSearchCriteria min_max_2023 = getBSC_byMinMaxPublishYear(CONFLICTS_PUBLISH_YEAR, CONFLICTS_PUBLISH_YEAR);
        BookSearchCriteria notFound = getBSC_byMinMaxPublishYear(2025, null);

        Page<Book> books = searchService.search(min_2023, SIZE);
        assertEquals(1, books.getTotalElements());
        assertEquals(Set.of(CONFLICTS_AND_ADAPTATIONS), getNameOfBooks(books));

        books = searchService.search(max_2023, SIZE);
        assertEquals(4, books.getTotalElements());
        assertEquals(allBookNames(), getNameOfBooks(books));

        books = searchService.search(min_max_2023, SIZE);
        assertEquals(1, books.getTotalElements());
        assertEquals(Set.of(CONFLICTS_AND_ADAPTATIONS), getNameOfBooks(books));

        books = searchService.search(notFound, SIZE);
        assertEquals(0, books.getTotalElements());
    }

    @Test
    void search_shouldFindBooks_whenPageMinMaxIsUsed() {
        BookSearchCriteria min_2Books = getBSC_byMinMaxPages(ESTONIAN_ART_BOOKS_PAGE_NR, null);
        BookSearchCriteria max_2Books = getBSC_byMinMaxPages(null, ESTONIAN_ART_BOOKS_PAGE_NR);
        BookSearchCriteria min_max_2Books = getBSC_byMinMaxPages(ESTONIAN_ART_BOOKS_PAGE_NR, ESTONIAN_ART_BOOKS_PAGE_NR);
        BookSearchCriteria notFound = getBSC_byMinMaxPublishYear(2025, null);

        Page<Book> books = searchService.search(min_2Books, SIZE);
        assertEquals(4, books.getTotalElements());
        assertEquals(allBookNames(), getNameOfBooks(books));

        books = searchService.search(max_2Books, SIZE);
        assertEquals(2, books.getTotalElements());
        assertEquals(estonianArtBooks(), getNameOfBooks(books));

        books = searchService.search(min_max_2Books, SIZE);
        assertEquals(2, books.getTotalElements());
        assertEquals(estonianArtBooks(), getNameOfBooks(books));

        books = searchService.search(notFound, SIZE);
        assertEquals(0, books.getTotalElements());
    }

    private static BookSearchCriteria getBSC_byPublisher(String publisher) {
        return new BookSearchCriteria(null, null, null,
                null, null, null,
                publisher, null, null,
                null, null, null);
    }

    private static BookSearchCriteria getBSC_byMinMaxPublishYear(Integer minYear, Integer maxYear) {
        return new BookSearchCriteria(null, null, null,
                null, null, null,
                null, null, minYear,
                maxYear, null, null);
    }

    private static BookSearchCriteria getBSC_byMinMaxPages(Integer minPages, Integer maxPages) {
        return new BookSearchCriteria(null, null, null,
                null, null, null,
                null, null, null,
                null, minPages, maxPages);
    }

    private static BookSearchCriteria getBSC_byCoverType(CoverType coverType) {
        return new BookSearchCriteria(null, null, null,
                null, null, null,
                null, coverType, null,
                null, null, null);
    }

    private static Set<String> getNameOfBooks(Page<Book> books) {
        return books.stream().map(Book::getName).collect(Collectors.toSet());
    }

}
