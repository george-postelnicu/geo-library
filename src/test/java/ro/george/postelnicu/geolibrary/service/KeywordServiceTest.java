package ro.george.postelnicu.geolibrary.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.dto.BookDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordsDto;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyLinkedException;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.Keyword;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException.ENTITY_ALREADY_HAS_A;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyLinkedException.ENTITY_ALREADY_HAS_A_LINK;
import static ro.george.postelnicu.geolibrary.exception.EntityNotFoundException.CANNOT_FIND_ENTITY_ID;
import static ro.george.postelnicu.geolibrary.model.EntityName.KEYWORD;

@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "classpath:/sql/clean-all-data.sql",
})
class KeywordServiceTest {

    public static final String ART = "Art";
    public static final String ARCHITECTURE = "Architecture";
    public static final long ID_NOT_FOUND = 0L;
    private final KeywordService service;
    private final BookService bookService;

    @Autowired
    KeywordServiceTest(KeywordService service, BookService bookService) {
        this.service = service;
        this.bookService = bookService;
    }

    @Test
    void createBulk_isSuccessful() {
        KeywordsDto dto = new KeywordsDto();
        dto.setKeywords(Set.of(ART, ARCHITECTURE));
        List<Keyword> bulk = service.createBulk(dto);

        bulk.sort(Comparator.comparing(Keyword::getName));
        assertEquals(bulk.size(), 2);
        assertNotNull(bulk.get(0).getId());
        assertNotNull(bulk.get(1).getId());
        assertEquals(bulk.get(0).getName(), ARCHITECTURE);
        assertEquals(bulk.get(1).getName(), ART);
    }

    @Test
    void createBulk_throwsException_whenOneAuthorExistsCaseInsensitive() {
        KeywordsDto dto = new KeywordsDto();
        dto.setKeywords(Set.of(ART, ARCHITECTURE));
        service.createBulk(dto);

        KeywordsDto fail = new KeywordsDto();
        fail.setKeywords(Set.of(ARCHITECTURE.toUpperCase()));
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.createBulk(fail));

        assertEquals(ex.getMessage(), String.format(ENTITY_ALREADY_HAS_A, KEYWORD, ARCHITECTURE));
    }

    @Test
    void create_isSuccessful() {
        KeywordDto dto = new KeywordDto(ART);
        Keyword keyword = service.create(dto);

        assertNotNull(keyword.getId());
        assertEquals(keyword.getName(), ART);
        assertEquals(keyword.getBooks().size(), 0);
    }

    @Test
    void create_throwsException_whenAuthorExistsCaseInsensitive() {
        KeywordDto dto = new KeywordDto(ART);
        service.create(dto);

        KeywordDto fail = new KeywordDto(ART.toUpperCase());
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.create(fail));

        assertEquals(ex.getMessage(), String.format(ENTITY_ALREADY_HAS_A, KEYWORD, ART));
    }

    @Test
    void createIfNotExisting_isSuccessful() {
        KeywordDto dto = new KeywordDto(ART);
        Keyword keyword = service.createIfNotExisting(dto);

        assertNotNull(keyword.getId());
        assertEquals(keyword.getName(), ART);
        assertEquals(keyword.getBooks().size(), 0);
    }

    @Test
    @Transactional
    void createIfNotExisting_isSuccessful_whenSameAuthorIsUsedCaseInsensitive() {
        Keyword keyword = service.createIfNotExisting(new KeywordDto(ART));
        Keyword existing = service.createIfNotExisting(new KeywordDto(ART.toUpperCase()));

        assertEquals(keyword.getId(), existing.getId());
        assertEquals(keyword.getName(), existing.getName());
        assertEquals(keyword.getBooks(), existing.getBooks());
    }

    @Test
    @Transactional
    void read_isSuccessful() {
        Keyword keyword = service.create(new KeywordDto(ARCHITECTURE));

        Keyword existing = service.read(keyword.getId());
        assertEquals(keyword.getId(), existing.getId());
        assertEquals(keyword.getName(), existing.getName());
        assertEquals(keyword.getBooks(), existing.getBooks());
    }

    @Test
    void read_throwsException_whenIdDoesntExist() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.read(ID_NOT_FOUND));

        assertEquals(ex.getMessage(), String.format(CANNOT_FIND_ENTITY_ID, KEYWORD, ID_NOT_FOUND));
    }

    @Test
    void update_isSuccessful() {
        Keyword english = service.create(new KeywordDto(ART.toUpperCase()));

        service.update(english.getId(), new KeywordDto(ART.toLowerCase()));
    }

    @Test
    void update_throwsException_whenIdDoesntExist() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.update(ID_NOT_FOUND, new KeywordDto(ARCHITECTURE)));

        assertEquals(ex.getMessage(), String.format(CANNOT_FIND_ENTITY_ID, KEYWORD, ID_NOT_FOUND));
    }

    @Test
    void update_throwsException_whenAuthorExists() {
        service.create(new KeywordDto(ART));
        Keyword french = service.create(new KeywordDto(ARCHITECTURE));
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class,
                () -> service.update(french.getId(), new KeywordDto(ART)));

        assertEquals(ex.getMessage(), String.format(ENTITY_ALREADY_HAS_A, KEYWORD, ART));
    }

    @Test
    void delete_isSuccessful() {
        Keyword keyword = service.create(new KeywordDto(ARCHITECTURE));

        service.delete(keyword.getId());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.read(keyword.getId()));

        assertEquals(ex.getMessage(), String.format(CANNOT_FIND_ENTITY_ID, KEYWORD, keyword.getId()));
    }

    @Test
    void delete_throwsException_whenIdDoesntExist() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(ID_NOT_FOUND));

        assertEquals(ex.getMessage(), String.format(CANNOT_FIND_ENTITY_ID, KEYWORD, ID_NOT_FOUND));
    }

    @Test
    void delete_throwsException_whenBooksHaveThisAuthorLinked() {
        BookDto bookInEnglish = BookServiceTest.getLandscapesOfIdentityBook();
        Book book = bookService.create(bookInEnglish);
        List<Keyword> keywords = book.getKeywords().stream().toList();

        assertEquals(bookInEnglish.getKeywords().size(), keywords.size());
        EntityAlreadyLinkedException ex = assertThrows(EntityAlreadyLinkedException.class, () -> service.delete(keywords.get(0).getId()));

        assertEquals(ex.getMessage(), String.format(ENTITY_ALREADY_HAS_A_LINK, KEYWORD, ART));
    }
}
