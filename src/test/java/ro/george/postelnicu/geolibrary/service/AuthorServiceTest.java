package ro.george.postelnicu.geolibrary.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.dto.BookDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorsDto;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyLinkedException;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;
import ro.george.postelnicu.geolibrary.model.Author;
import ro.george.postelnicu.geolibrary.model.Book;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ro.george.postelnicu.geolibrary.DataCommon.*;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException.ENTITY_ALREADY_HAS_A;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyLinkedException.ENTITY_ALREADY_HAS_A_LINK;
import static ro.george.postelnicu.geolibrary.exception.EntityNotFoundException.CANNOT_FIND_ENTITY_ID;
import static ro.george.postelnicu.geolibrary.model.EntityName.AUTHOR;

@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "classpath:/sql/clean-all-data.sql",
})
class AuthorServiceTest {

    private final AuthorService service;
    private final BookService bookService;

    @Autowired
    AuthorServiceTest(AuthorService service, BookService bookService) {
        this.service = service;
        this.bookService = bookService;
    }

    @Test
    void createBulk_isSuccessful() {
        AuthorsDto dto = new AuthorsDto();
        dto.setAuthors(Set.of(LINDA, BART));
        List<Author> bulk = service.createBulk(dto);

        bulk.sort(Comparator.comparing(Author::getName));
        assertEquals(2, bulk.size());
        assertNotNull(bulk.get(0).getId());
        assertNotNull(bulk.get(1).getId());
        assertEquals(BART, bulk.get(0).getName());
        assertEquals(LINDA, bulk.get(1).getName());
    }

    @Test
    void createBulk_throwsException_whenOneAuthorExistsCaseInsensitive() {
        AuthorsDto dto = new AuthorsDto();
        dto.setAuthors(Set.of(LINDA, BART));
        service.createBulk(dto);

        AuthorsDto fail = new AuthorsDto();
        fail.setAuthors(Set.of(BART.toUpperCase()));
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.createBulk(fail));

        assertEquals(String.format(ENTITY_ALREADY_HAS_A, AUTHOR, BART), ex.getMessage());
    }

    @Test
    void create_isSuccessful() {
        AuthorDto dto = new AuthorDto(LINDA);
        Author author = service.create(dto);

        assertNotNull(author.getId());
        assertEquals(LINDA, author.getName());
        assertEquals(0, author.getBooks().size());
    }

    @Test
    void create_throwsException_whenAuthorExistsCaseInsensitive() {
        AuthorDto dto = new AuthorDto(LINDA);
        service.create(dto);

        AuthorDto fail = new AuthorDto(LINDA.toUpperCase());
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.create(fail));

        assertEquals(String.format(ENTITY_ALREADY_HAS_A, AUTHOR, LINDA), ex.getMessage());
    }

    @Test
    void createIfNotExisting_isSuccessful() {
        AuthorDto dto = new AuthorDto(LINDA);
        Author author = service.createIfNotExisting(dto);

        assertNotNull(author.getId());
        assertEquals(LINDA, author.getName());
        assertEquals(0, author.getBooks().size());
    }

    @Test
    @Transactional
    void createIfNotExisting_isSuccessful_whenSameAuthorIsUsedCaseInsensitive() {
        Author author = service.createIfNotExisting(new AuthorDto(LINDA));
        Author existing = service.createIfNotExisting(new AuthorDto(LINDA.toUpperCase()));

        assertEquals(author.getId(), existing.getId());
        assertEquals(author.getName(), existing.getName());
        assertEquals(author.getBooks(), existing.getBooks());
    }

    @Test
    @Transactional
    void read_isSuccessful() {
        Author author = service.create(new AuthorDto(BART));

        Author existing = service.read(author.getId());
        assertEquals(author.getId(), existing.getId());
        assertEquals(author.getName(), existing.getName());
        assertEquals(author.getBooks(), existing.getBooks());
    }

    @Test
    void read_throwsException_whenIdDoesntExist() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.read(ID_NOT_FOUND));

        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, AUTHOR, ID_NOT_FOUND), ex.getMessage());
    }

    @Test
    void update_isSuccessful() {
        Author linda = service.create(new AuthorDto(LINDA.toUpperCase()));

        Author update = service.update(linda.getId(), new AuthorDto(LINDA + " III"));
        assertEquals(LINDA + " III", update.getName());
    }

    @Test
    void update_throwsException_whenIdDoesntExist() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.update(ID_NOT_FOUND, new AuthorDto(BART)));

        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, AUTHOR, ID_NOT_FOUND), ex.getMessage());
    }

    @Test
    void update_throwsException_whenAuthorExists() {
        service.create(new AuthorDto(LINDA));
        Author bart = service.create(new AuthorDto(BART));
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class,
                () -> service.update(bart.getId(), new AuthorDto(LINDA)));

        assertEquals(String.format(ENTITY_ALREADY_HAS_A, AUTHOR, LINDA), ex.getMessage());
    }

    @Test
    void delete_isSuccessful() {
        Author author = service.create(new AuthorDto(BART));

        service.delete(author.getId());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.read(author.getId()));

        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, AUTHOR, author.getId()), ex.getMessage());
    }

    @Test
    void delete_throwsException_whenIdDoesntExist() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(ID_NOT_FOUND));

        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, AUTHOR, ID_NOT_FOUND), ex.getMessage());
    }

    @Test
    void delete_throwsException_whenBooksHaveThisAuthorLinked() {
        BookDto bookInEnglish = BookServiceTest.getLandscapesOfIdentityBook();
        Book book = bookService.create(bookInEnglish);
        List<Author> authors = new ArrayList<>(book.getAuthors().stream().toList());
        authors.sort(Comparator.comparing(Author::getName));

        assertEquals(bookInEnglish.getAuthors().size(), authors.size());
        EntityAlreadyLinkedException ex = assertThrows(EntityAlreadyLinkedException.class, () -> service.delete(authors.getFirst().getId()));

        assertEquals(String.format(ENTITY_ALREADY_HAS_A_LINK, AUTHOR, BART), ex.getMessage());
    }
}
