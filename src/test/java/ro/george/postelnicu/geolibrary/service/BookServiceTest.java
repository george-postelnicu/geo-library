package ro.george.postelnicu.geolibrary.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ro.george.postelnicu.geolibrary.dto.BookDto;
import ro.george.postelnicu.geolibrary.model.Author;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.Keyword;
import ro.george.postelnicu.geolibrary.model.Language;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ro.george.postelnicu.geolibrary.model.CoverType.SOFTCOVER_WITH_DUST_JACKET;
import static ro.george.postelnicu.geolibrary.model.StatusType.HAVE;

@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "classpath:/sql/clean-all-data.sql",
})
class BookServiceTest {
    private final BookService service;

    @Autowired
    BookServiceTest(BookService service) {
        this.service = service;
    }

    @Test
    void create_isSuccessful_whenFullBookDetailsAreGiven() {
        BookDto dto = getLandscapesOfIdentityBook();

        Book book = service.create(dto);

        assertNotNull(book.getId());
        assertEquals("Landscapes of Identity", book.getName());
        assertEquals("ISBN 978-9949-687-32-9", book.getIsbn());
        assertEquals(HAVE.name(), book.getStatus().name());
        assertEquals("Landscapes of Identity: Estonian Art 1700-1945 The 3rd-floor permanent exhibition of the Kumu Art Museum",
                book.getFullTitle());
        assertEquals("Lorem Ipsum", book.getDescription());
        assertEquals(Set.of("Linda Kalijundi", "Kadi Polli", "Bart Pushaw", "Kaja Kahrik"), getAuthorNames(book.getAuthors()));
        assertEquals(Set.of("Museum Collection", "Kumu Art Museum", "Art", "Estonian Art"), getKeywordNames(book.getKeywords()));
        assertEquals(Set.of("English"), getLanguageNames(book.getLanguages()));
        assertEquals("Art Museum of Estonia", book.getPublisher());
        assertEquals(SOFTCOVER_WITH_DUST_JACKET, book.getCover());
        assertEquals(2021, book.getPublishYear());
        assertEquals(111, book.getPages());
        assertEquals("9789949687329", book.getBarcode());
    }

    private static BookDto getLandscapesOfIdentityBook() {
        BookDto dto = new BookDto("Landscapes of Identity", "ISBN 978-9949-687-32-9", HAVE);
        dto.setFullTitle("Landscapes of Identity: Estonian Art 1700-1945 The 3rd-floor permanent exhibition of the Kumu Art Museum");
        dto.setDescription("Lorem Ipsum");
        dto.setAuthors(Set.of("Linda Kalijundi", "Kadi Polli", "Bart Pushaw", "Kaja Kahrik"));
        dto.setKeywords(Set.of("Museum Collection", "Kumu Art Museum", "Art", "Estonian Art"));
        dto.setLanguages(Set.of("English"));
        dto.setPublisher("Art Museum of Estonia");
        dto.setCover(SOFTCOVER_WITH_DUST_JACKET);
        dto.setPublishYear(2021);
        dto.setPages(111);
        dto.setBarcode("9789949687329");
        return dto;
    }

    private static Set<String> getAuthorNames(Set<Author> authors) {
        return authors.stream().map(Author::getName).collect(Collectors.toSet());
    }

    private static Set<String> getLanguageNames(Set<Language> languages) {
        return languages.stream().map(Language::getName).collect(Collectors.toSet());
    }

    private static Set<String> getKeywordNames(Set<Keyword> languages) {
        return languages.stream().map(Keyword::getName).collect(Collectors.toSet());
    }
}