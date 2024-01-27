package ro.george.postelnicu.geolibrary.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ro.george.postelnicu.geolibrary.dto.BookDto;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.exception.EntityValidationException;
import ro.george.postelnicu.geolibrary.model.Author;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.Keyword;
import ro.george.postelnicu.geolibrary.model.Language;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException.ENTITY_ALREADY_HAS_A;
import static ro.george.postelnicu.geolibrary.exception.EntityValidationException.ENTITY_VALIDATION_FAILURE;
import static ro.george.postelnicu.geolibrary.model.CoverType.SOFTCOVER_WITH_DUST_JACKET;
import static ro.george.postelnicu.geolibrary.model.EntityName.BOOK;
import static ro.george.postelnicu.geolibrary.model.StatusType.HAVE;

@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "classpath:/sql/clean-all-data.sql",
})
class BookServiceTest {
    public static final String LANDSCAPES_OF_IDENTITY = "Landscapes of Identity";
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
        assertEquals(LANDSCAPES_OF_IDENTITY, book.getName());
        assertEquals("ISBN 978-9949-687-32-9", book.getIsbn());
        assertEquals(HAVE.name(), book.getStatus().name());
        assertEquals("Landscapes of Identity: Estonian Art 1700-1945 The 3rd-floor permanent exhibition of the Kumu Art Museum",
                book.getFullTitle());
        assertEquals("Lorem Ipsum", book.getDescription());
        assertEquals(Set.of("Linda Kalijundi", "Kadi Polli", "Bart Pushaw", "Kaja Kahrik"), getAuthorNames(book.getAuthors()));
        assertEquals(Set.of("Kumu Art Museum", "Art", "Estonian Art"), getKeywordNames(book.getKeywords()));
        assertEquals(Set.of("English"), getLanguageNames(book.getLanguages()));
        assertEquals("Art Museum of Estonia", book.getPublisher());
        assertEquals(SOFTCOVER_WITH_DUST_JACKET, book.getCover());
        assertEquals(2021, book.getPublishYear());
        assertEquals(111, book.getPages());
        assertEquals("9789949687329", book.getBarcode());
    }

    @Test
    void create_isSuccessful_whenTwoBooksReturnSameIdsForExternalEntities() {
        Book book1 = service.create(getLandscapesOfIdentityBook());
        Book book2 = service.create(anotherEnglishBook());

        List<Language> book1Languages = book1.getLanguages().stream().toList();
        List<Language> book2Languages = book2.getLanguages().stream().toList();
        assertEquals(book1Languages.size(), book2Languages.size());
        assertEquals(book1Languages.get(0).getId(), book2Languages.get(0).getId());

        List<Keyword> book1Keywords = book1.getKeywords().stream().toList();
        List<Keyword> book2Keywords = book2.getKeywords().stream().toList();
        assertEquals(book1Keywords.size(), book2Keywords.size());
        assertEquals(book1Keywords.get(0).getId(), book2Keywords.get(0).getId());

        List<Author> book1Authors = book1.getAuthors().stream().toList();
        List<Author> book2Authors = book2.getAuthors().stream().toList();
        assertEquals(book1Authors.size(), book2Authors.size());
        assertEquals(book1Authors.get(0).getId(), book2Authors.get(0).getId());
    }

    @Test
    void create_throwsException_whenNameAlreadyExistsCaseInsensitive() {
        BookDto dto = getLandscapesOfIdentityBook();
        BookDto fail = getLandscapesOfIdentityBook();
        fail.setName(fail.getName().toUpperCase());

        service.create(dto);

        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.create(fail));

        assertEquals(String.format(ENTITY_ALREADY_HAS_A, BOOK, LANDSCAPES_OF_IDENTITY.toUpperCase()), ex.getMessage());
    }

    @Test
    void create_throwsException_whenNameIsNotIncludedInFullTitle() {
        BookDto dto = getLandscapesOfIdentityBook();
        dto.setFullTitle("Landscapes of Identiti: Estonian Art 1700-1945 The 3rd-floor permanent exhibition of the Kumu Art Museum");

        EntityValidationException ex = assertThrows(EntityValidationException.class, () -> service.create(dto));

        assertEquals(String.format(ENTITY_VALIDATION_FAILURE, BOOK, "Name is not included in full title!"), ex.getMessage());
    }

    public static BookDto getLandscapesOfIdentityBook() {
        BookDto dto = new BookDto(LANDSCAPES_OF_IDENTITY, "ISBN 978-9949-687-32-9", HAVE);
        dto.setFullTitle("Landscapes of Identity: Estonian Art 1700-1945 The 3rd-floor permanent exhibition of the Kumu Art Museum");
        dto.setDescription("Lorem Ipsum");
        dto.setAuthors(Set.of("Linda Kalijundi", "Kadi Polli", "Bart Pushaw", "Kaja Kahrik"));
        dto.setKeywords(Set.of("Kumu Art Museum", "Art", "Estonian Art"));
        dto.setLanguages(Set.of("English"));
        dto.setPublisher("Art Museum of Estonia");
        dto.setCover(SOFTCOVER_WITH_DUST_JACKET);
        dto.setPublishYear(2021);
        dto.setPages(111);
        dto.setBarcode("9789949687329");
        return dto;
    }

    public static BookDto anotherEnglishBook() {
        BookDto dto = new BookDto("100 Steps Through 20th Century Estonian Architecture", "ISBN 978-9949-9078-6-1", HAVE);
        dto.setLanguages(Set.of("English"));
        dto.setAuthors(Set.of("Linda Kalijundi", "Kadi Polli", "Bart Pushaw", "Kaja Kahrik"));
        dto.setKeywords(Set.of("Kumu Art Museum", "Art", "Estonian Art"));
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