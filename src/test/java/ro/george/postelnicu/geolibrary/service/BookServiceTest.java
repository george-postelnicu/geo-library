package ro.george.postelnicu.geolibrary.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ro.george.postelnicu.geolibrary.AbstractIntegrationTest;
import ro.george.postelnicu.geolibrary.DataCommon;
import ro.george.postelnicu.geolibrary.dto.book.BookDto;
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
import static ro.george.postelnicu.geolibrary.DataCommon.*;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException.ENTITY_ALREADY_HAS_A;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException.ENTITY_ALREADY_HAS_COLLECTION;
import static ro.george.postelnicu.geolibrary.exception.EntityValidationException.ENTITY_VALIDATION_FAILURE;
import static ro.george.postelnicu.geolibrary.model.CoverType.SOFTCOVER_WITH_DUST_JACKET;
import static ro.george.postelnicu.geolibrary.model.EntityName.BOOK;
import static ro.george.postelnicu.geolibrary.model.StatusType.HAVE;
import static ro.george.postelnicu.geolibrary.service.BookService.NAME_IS_NOT_INCLUDED_IN_FULL_TITLE;


class BookServiceTest extends AbstractIntegrationTest {
    private final BookService service;

    @Autowired
    BookServiceTest(BookService service) {
        this.service = service;
    }

    @Test
    void create_isSuccessful_whenFullBookDetailsAreGiven() {
        BookDto dto = landscapesOfIdentity();

        Book book = service.create(dto);

        assertNotNull(book.getId());
        assertEquals(DataCommon.LANDSCAPES_OF_IDENTITY, book.getName());
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
        Book book1 = service.create(landscapesOfIdentity());
        Book book2 = service.create(anotherBookLikeLandscapes());

        List<Language> book1Languages = book1.getLanguages().stream().toList();
        List<Language> book2Languages = book2.getLanguages().stream().toList();
        assertEquals(book1Languages.size(), book2Languages.size());
        assertEquals(book1Languages.getFirst().getId(), book2Languages.getFirst().getId());

        List<Keyword> book1Keywords = book1.getKeywords().stream().toList();
        List<Keyword> book2Keywords = book2.getKeywords().stream().toList();
        assertEquals(book1Keywords.size(), book2Keywords.size());
        assertEquals(book1Keywords.getFirst().getId(), book2Keywords.getFirst().getId());

        List<Author> book1Authors = book1.getAuthors().stream().toList();
        List<Author> book2Authors = book2.getAuthors().stream().toList();
        assertEquals(book1Authors.size(), book2Authors.size());
        assertEquals(book1Authors.getFirst().getId(), book2Authors.getFirst().getId());
    }

    @Test
    void create_throwsException_whenNameAlreadyExistsCaseInsensitive() {
        BookDto dto = landscapesOfIdentity();
        BookDto fail = landscapesOfIdentity();
        fail.setName(fail.getName().toUpperCase());

        service.create(dto);

        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.create(fail));

        assertEquals(String.format(ENTITY_ALREADY_HAS_A, BOOK, DataCommon.LANDSCAPES_OF_IDENTITY.toUpperCase()), ex.getMessage());
    }

    @Test
    void create_throwsException_whenNameIsNotIncludedInFullTitle() {
        BookDto dto = landscapesOfIdentity();
        dto.setFullTitle("Landscapes of Identiti: Estonian Art 1700-1945 The 3rd-floor permanent exhibition of the Kumu Art Museum");

        EntityValidationException ex = assertThrows(EntityValidationException.class, () -> service.create(dto));

        assertEquals(String.format(ENTITY_VALIDATION_FAILURE, BOOK, "Name is not included in full title!"), ex.getMessage());
    }

    @Test
    void create_throwsException_whenIsbnIsFoundInAnotherBook() {
        BookDto dto = landscapesOfIdentity();
        BookDto duplicateIsbn = conflictsAndAdaptations();
        duplicateIsbn.setIsbn(dto.getIsbn());

        service.create(dto);
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.create(duplicateIsbn));

        assertEquals(String.format(ENTITY_ALREADY_HAS_COLLECTION, BOOK, Set.of(duplicateIsbn.getName(), duplicateIsbn.getIsbn())), ex.getMessage());
    }

    @Test
    void create_throwsException_whenBarcodeIsFoundInAnotherBook() {
        BookDto dto = landscapesOfIdentity();
        BookDto duplicateBarcode = conflictsAndAdaptations();
        duplicateBarcode.setBarcode(dto.getBarcode());

        service.create(dto);
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.create(duplicateBarcode));

        assertEquals(String.format(ENTITY_ALREADY_HAS_COLLECTION, BOOK, Set.of(duplicateBarcode.getName(), duplicateBarcode.getBarcode())), ex.getMessage());
    }

    @Test
    void update_isSuccessful() {
        BookDto dto = landscapesOfIdentity();
        Book book = service.create(dto);

        BookDto updatedDto = conflictsAndAdaptations();

        Book updatedBook = service.update(book.getId(), updatedDto);
        assertEquals(updatedDto.getName(), updatedBook.getName());
    }

    @Test
    void update_throwsException_whenFullTitleDoesNotContainName() {
        BookDto dto = landscapesOfIdentity();
        Book book = service.create(dto);

        BookDto updatedDto = landscapesOfIdentity();
        updatedDto.setName("Updated Book Name");

        EntityValidationException ex = assertThrows(EntityValidationException.class, () -> service.update(book.getId(), updatedDto));

        assertEquals(String.format(ENTITY_VALIDATION_FAILURE, BOOK, NAME_IS_NOT_INCLUDED_IN_FULL_TITLE), ex.getMessage());
    }

    @Test
    void update_throwsException_whenNameAlreadyExists() {
        BookDto dto = landscapesOfIdentity();
        BookDto secondDto = conflictsAndAdaptations();
        Book book = service.create(dto);
        service.create(secondDto);

        dto.setName(secondDto.getName());
        dto.setFullTitle(secondDto.getFullTitle());

        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.update(book.getId(), dto));

        assertEquals(String.format(ENTITY_ALREADY_HAS_A, BOOK, secondDto.getName()), ex.getMessage());
    }

    @Test
    void update_throwsException_whenIsbnExists() {
        BookDto dto = landscapesOfIdentity();
        BookDto secondDto = conflictsAndAdaptations();
        Book book = service.create(dto);
        service.create(secondDto);

        dto.setIsbn(secondDto.getIsbn());
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.update(book.getId(), dto));

        assertEquals(String.format(ENTITY_ALREADY_HAS_COLLECTION, BOOK, Set.of(dto.getName(), dto.getIsbn())), ex.getMessage());
    }

    @Test
    void update_throwsException_whenBarcodeExists() {
        BookDto dto = landscapesOfIdentity();
        BookDto secondDto = conflictsAndAdaptations();
        Book book = service.create(dto);
        service.create(secondDto);
        dto.setBarcode(secondDto.getBarcode());

        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.update(book.getId(), dto));

        assertEquals(String.format(ENTITY_ALREADY_HAS_COLLECTION, BOOK, Set.of(dto.getName(), dto.getBarcode())), ex.getMessage());
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