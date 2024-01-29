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
import static ro.george.postelnicu.geolibrary.util.StringUtil.SPACE;
import static ro.george.postelnicu.geolibrary.util.StringUtil.WILDCARD;


class BookSearchServiceTest extends AbstractIntegrationTest {

    public static final Pageable SIZE = Pageable.ofSize(20);
    private final BookService bookService;
    private final BookSearchService service;

    @Autowired
    BookSearchServiceTest(BookService bookService, BookSearchService service) {
        this.bookService = bookService;
        this.service = service;
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

        resultsFound(artMuseumOfEstoniaPublisher, estonianArtBookNames());
        noResultsFound(notFound);
    }

    @Test
    void search_shouldReturnBooks_whenFilteringByCoverType() {
        BookSearchCriteria hardcoverBooks = getBSC_byCoverType(HARDCOVER);
        BookSearchCriteria softCoverWithDustJacketBooks = getBSC_byCoverType(SOFTCOVER_WITH_DUST_JACKET);
        BookSearchCriteria softCoverBooks = getBSC_byCoverType(SOFTCOVER);

        resultsFound(hardcoverBooks, housesYouNeedToVisit());
        resultsFound(softCoverWithDustJacketBooks, estonianBookNames());
        noResultsFound(softCoverBooks);
    }

    @Test
    void search_shouldFindBooks_whenFilteringByPublishYear() {
        BookSearchCriteria min_2023 = getBSC_byMinMaxPublishYear(CONFLICTS_PUBLISH_YEAR, null);
        BookSearchCriteria max_2023 = getBSC_byMinMaxPublishYear(null, CONFLICTS_PUBLISH_YEAR);
        BookSearchCriteria min_max_2023 = getBSC_byMinMaxPublishYear(CONFLICTS_PUBLISH_YEAR, CONFLICTS_PUBLISH_YEAR);
        BookSearchCriteria notFound = getBSC_byMinMaxPublishYear(2025, null);

        resultsFound(min_2023, Set.of(CONFLICTS_AND_ADAPTATIONS));
        resultsFound(max_2023, allBookNames());
        resultsFound(min_max_2023, Set.of(CONFLICTS_AND_ADAPTATIONS));
        noResultsFound(notFound);
    }

    @Test
    void search_shouldFindBooks_whenFilteringByPages() {
        BookSearchCriteria min_2Books = getBSC_byMinMaxPages(ESTONIAN_ART_BOOKS_PAGE_NR, null);
        BookSearchCriteria max_2Books = getBSC_byMinMaxPages(null, ESTONIAN_ART_BOOKS_PAGE_NR);
        BookSearchCriteria min_max_2Books = getBSC_byMinMaxPages(ESTONIAN_ART_BOOKS_PAGE_NR, ESTONIAN_ART_BOOKS_PAGE_NR);
        BookSearchCriteria notFound = getBSC_byMinMaxPublishYear(2025, null);

        resultsFound(min_2Books, allBookNames());
        resultsFound(max_2Books, estonianArtBookNames());
        resultsFound(min_max_2Books, estonianArtBookNames());
        noResultsFound(notFound);
    }

    @Test
    void search_shouldFindBooks_whenFilteringByAuthors() {
        BookSearchCriteria authors = getBSC_byAuthors(Set.of(KAJA));
        resultsFound(authors, estonianArtBookNames());

        BookSearchCriteria searchCriteria = getBSC_byAuthors(Set.of(LINDA, KADI, BART, KAJA));
        resultsFound(searchCriteria, Set.of(LANDSCAPES_OF_IDENTITY));

        BookSearchCriteria notFound = getBSC_byAuthors(Set.of(NOT_FOUND));
        noResultsFound(notFound);
    }

    @Test
    void search_shouldFindBooks_whenFilteringByKeywords() {
        BookSearchCriteria art = getBSC_byKeywords(Set.of(ART));
        resultsFound(art, estonianArtBookNames());

        BookSearchCriteria estonianArtKeywords = getBSC_byKeywords(estonianArtKeywords());
        resultsFound(estonianArtKeywords, estonianArtBookNames());

        BookSearchCriteria notFound = getBSC_byKeywords(Set.of(NOT_FOUND));
        noResultsFound(notFound);
    }

    @Test
    void search_shouldFindBooks_whenFilteringByLanguages() {
        BookSearchCriteria englishAndEstonian = getBSC_byKLanguages(bothLanguages());
        resultsFound(englishAndEstonian, Set.of(_20TH_CENTURY_ESTONIAN_ARCHITECTURE));

        BookSearchCriteria english = getBSC_byKLanguages(Set.of(ENGLISH));
        resultsFound(english, allBookNames());

        BookSearchCriteria notFound = getBSC_byKLanguages(Set.of(NOT_FOUND));
        noResultsFound(notFound);
    }

    @Test
    void search_shouldFindBooks_whenFilteringByName() {
        BookSearchCriteria landscapes = getBSC_byName(LANDSCAPES_OF_IDENTITY);
        resultsFound(landscapes, Set.of(LANDSCAPES_OF_IDENTITY));

        BookSearchCriteria wildcardName = getBSC_byName("*AND*");
        resultsFound(wildcardName, estonianArtBookNames());

        BookSearchCriteria notFound = getBSC_byName(NOT_FOUND);
        noResultsFound(notFound);
    }

    @Test
    void search_shouldFindAllBooks_whenWildcardIsWrong() {
        BookSearchCriteria ignoredSearchTermBecauseItsOnlyWildcard = getBSC_byName(WILDCARD);
        resultsFound(ignoredSearchTermBecauseItsOnlyWildcard, allBookNames());

        BookSearchCriteria ignoredSearchTermBecauseItsNotAtLeast4CharsLong = getBSC_byName("ZZ*");
        resultsFound(ignoredSearchTermBecauseItsNotAtLeast4CharsLong, allBookNames());

        BookSearchCriteria ignoredSearchTermBecauseHasMoreThan2Wildcards = getBSC_byName("*A*B*C*");
        resultsFound(ignoredSearchTermBecauseHasMoreThan2Wildcards, allBookNames());

        BookSearchCriteria ignoredSearchTermBecauseItsABlankString = getBSC_byName(SPACE);
        resultsFound(ignoredSearchTermBecauseItsABlankString, allBookNames());
    }

    @Test
    void search_shouldFindBooks_whenFilteringByFullName() {
        BookSearchCriteria conflicts = getBSC_byFullTitle(CONFLICTS_AND_ADAPTATIONS_FULL_TITLE);
        resultsFound(conflicts, Set.of(CONFLICTS_AND_ADAPTATIONS));

        BookSearchCriteria sovietEra = getBSC_byFullTitle("*Soviet Era*");
        resultsFound(sovietEra, Set.of(CONFLICTS_AND_ADAPTATIONS));

        BookSearchCriteria notFound = getBSC_byFullTitle(NOT_FOUND);
        noResultsFound(notFound);
    }

    @Test
    void search_shouldFindBooks_whenFilteringByDescription() {
        BookSearchCriteria description = getBSC_byDescription(LOREM_IPSUM);
        resultsFound(description, estonianArtBookNames());

        BookSearchCriteria lorem = getBSC_byDescription("LOREM*");
        resultsFound(lorem, estonianArtBookNames());

        BookSearchCriteria notFound = getBSC_byDescription(NOT_FOUND);
        noResultsFound(notFound);
    }

    @Test
    void search_shouldFindBooks_whenFilteringByIsbnOrByBarcode() {
        BookSearchCriteria isbn = getBSC_byIsbnOrBarcode(ISBN_HOUSES_YOU_NEED_TO_VISIT, null);
        resultsFound(isbn, Set.of(HOUSES_YOU_NEED_TO_VISIT_BEFORE_YOU_DIE));

        BookSearchCriteria isbnWildcard = getBSC_byIsbnOrBarcode("ISBN 978-9949*", null);
        resultsFound(isbnWildcard, estonianBookNames());

        BookSearchCriteria noIsbnFound = getBSC_byIsbnOrBarcode(NOT_FOUND, null);
        noResultsFound(noIsbnFound);

        BookSearchCriteria barcode = getBSC_byIsbnOrBarcode(null, BARCODE_HOUSES_YOU_NEED_TO_VISIT);
        resultsFound(barcode, Set.of(HOUSES_YOU_NEED_TO_VISIT_BEFORE_YOU_DIE));

        BookSearchCriteria barcodeWildcard = getBSC_byIsbnOrBarcode(null, "9789949*");
        resultsFound(barcodeWildcard, estonianBookNames());

        BookSearchCriteria noBarcodeFound = getBSC_byIsbnOrBarcode(null, NOT_FOUND);
        noResultsFound(noBarcodeFound);
    }

    private static BookSearchCriteria getBSC_byName(String name) {
        return new BookSearchCriteria(name, null, null, null, null,
                null, null, null,
                null, null, null,
                null, null, null);
    }

    private static BookSearchCriteria getBSC_byFullTitle(String fullTitle) {
        return new BookSearchCriteria(null, fullTitle, null, null, null,
                null, null, null,
                null, null, null,
                null, null, null);
    }

    private static BookSearchCriteria getBSC_byDescription(String description) {
        return new BookSearchCriteria(null, null, description, null, null,
                null, null, null,
                null, null, null,
                null, null, null);
    }
    private static BookSearchCriteria getBSC_byIsbnOrBarcode(String isbn, String barcode) {
        return new BookSearchCriteria(null, null, null, isbn, barcode,
                null, null, null,
                null, null, null,
                null, null, null);
    }

    private static BookSearchCriteria getBSC_byPublisher(String publisher) {
        return new BookSearchCriteria(null, null, null, null, null,
                null, null, null,
                publisher, null, null,
                null, null, null);
    }

    private static BookSearchCriteria getBSC_byMinMaxPublishYear(Integer minYear, Integer maxYear) {
        return new BookSearchCriteria(null, null, null, null, null,
                null, null, null,
                null, null, minYear,
                maxYear, null, null);
    }

    private static BookSearchCriteria getBSC_byMinMaxPages(Integer minPages, Integer maxPages) {
        return new BookSearchCriteria(null, null, null, null, null,
                null, null, null,
                null, null, null,
                null, minPages, maxPages);
    }

    private static BookSearchCriteria getBSC_byCoverType(CoverType coverType) {
        return new BookSearchCriteria(null, null, null, null, null,
                null, null, null,
                null, coverType, null,
                null, null, null);
    }

    private static BookSearchCriteria getBSC_byAuthors(Set<String> authors) {
        return new BookSearchCriteria(null, null, null, null, null,
                authors, null, null,
                null, null, null,
                null, null, null);
    }

    private static BookSearchCriteria getBSC_byKeywords(Set<String> keywords) {
        return new BookSearchCriteria(null, null, null, null, null,
                null, keywords, null,
                null, null, null,
                null, null, null);
    }

    private static BookSearchCriteria getBSC_byKLanguages(Set<String> languages) {
        return new BookSearchCriteria(null, null, null, null, null,
                null, null, languages,
                null, null, null,
                null, null, null);
    }

    private static Set<String> getNameOfBooks(Page<Book> books) {
        return books.stream().map(Book::getName).collect(Collectors.toSet());
    }

    private void resultsFound(BookSearchCriteria bookSearchCriteria, Set<String> expectedBookNames) {
        Page<Book> books = service.search(bookSearchCriteria, SIZE);
        assertEquals(expectedBookNames.size(), books.getTotalElements());
        assertEquals(expectedBookNames, getNameOfBooks(books));
    }

    private void noResultsFound(BookSearchCriteria bookSearchCriteria) {
        Page<Book> books = service.search(bookSearchCriteria, SIZE);
        assertEquals(0, books.getTotalElements());
    }
}
