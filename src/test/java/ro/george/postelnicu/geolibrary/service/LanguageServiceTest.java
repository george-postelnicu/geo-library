package ro.george.postelnicu.geolibrary.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.AbstractIntegrationTest;
import ro.george.postelnicu.geolibrary.dto.BookDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguagesDto;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyLinkedException;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.Language;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ro.george.postelnicu.geolibrary.DataCommon.*;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException.ENTITY_ALREADY_HAS_A;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyLinkedException.ENTITY_ALREADY_HAS_A_LINK;
import static ro.george.postelnicu.geolibrary.exception.EntityNotFoundException.CANNOT_FIND_ENTITY_ID;
import static ro.george.postelnicu.geolibrary.model.EntityName.LANGUAGE;

class LanguageServiceTest extends AbstractIntegrationTest {
    private final LanguageService service;
    private final BookService bookService;

    @Autowired
    LanguageServiceTest(LanguageService service, BookService bookService) {
        this.service = service;
        this.bookService = bookService;
    }

    @Test
    void createBulk_isSuccessful() {
        LanguagesDto dto = new LanguagesDto();
        dto.setLanguages(Set.of(ENGLISH, FRENCH));
        List<Language> bulk = service.createBulk(dto);

        bulk.sort(Comparator.comparing(Language::getName));
        assertEquals(2, bulk.size());
        assertNotNull(bulk.get(0).getId());
        assertNotNull(bulk.get(1).getId());
        assertEquals(ENGLISH, bulk.get(0).getName());
        assertEquals(FRENCH, bulk.get(1).getName());
    }

    @Test
    void createBulk_throwsException_whenOneLanguageExistsCaseInsensitive() {
        LanguagesDto dto = new LanguagesDto();
        dto.setLanguages(Set.of(ENGLISH, FRENCH));
        service.createBulk(dto);

        LanguagesDto fail = new LanguagesDto();
        fail.setLanguages(Set.of(FRENCH.toUpperCase()));
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.createBulk(fail));

        assertEquals(String.format(ENTITY_ALREADY_HAS_A, LANGUAGE, FRENCH), ex.getMessage());
    }

    @Test
    void create_isSuccessful() {
        LanguageDto dto = new LanguageDto(ENGLISH);
        Language language = service.create(dto);

        assertNotNull(language.getId());
        assertEquals(ENGLISH, language.getName());
        assertEquals(0, language.getBooks().size());
    }

    @Test
    void create_throwsException_whenLanguageExistsCaseInsensitive() {
        LanguageDto dto = new LanguageDto(ENGLISH);
        service.create(dto);

        LanguageDto fail = new LanguageDto(ENGLISH.toUpperCase());
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> service.create(fail));

        assertEquals(String.format(ENTITY_ALREADY_HAS_A, LANGUAGE, ENGLISH), ex.getMessage());
    }

    @Test
    void createIfNotExisting_isSuccessful() {
        LanguageDto dto = new LanguageDto(ENGLISH);
        Language language = service.createIfNotExisting(dto);

        assertNotNull(language.getId());
        assertEquals(ENGLISH, language.getName());
        assertEquals(0, language.getBooks().size());
    }

    @Test
    @Transactional
    void createIfNotExisting_isSuccessful_whenSameLanguageIsUsedCaseInsensitive() {
        Language language = service.createIfNotExisting(new LanguageDto(ENGLISH));
        Language existing = service.createIfNotExisting(new LanguageDto(ENGLISH.toUpperCase()));

        assertEquals(language.getId(), existing.getId());
        assertEquals(language.getName(), existing.getName());
        assertEquals(language.getBooks(), existing.getBooks());
    }

    @Test
    @Transactional
    void read_isSuccessful() {
        Language language = service.create(new LanguageDto(FRENCH));

        Language existing = service.read(language.getId());
        assertEquals(language.getId(), existing.getId());
        assertEquals(language.getName(), existing.getName());
        assertEquals(language.getBooks(), existing.getBooks());
    }

    @Test
    void read_throwsException_whenIdDoesntExist() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.read(ID_NOT_FOUND));

        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, LANGUAGE, ID_NOT_FOUND), ex.getMessage());
    }

    @Test
    void update_isSuccessful() {
        Language english = service.create(new LanguageDto(ENGLISH.toUpperCase()));

        Language update = service.update(english.getId(), new LanguageDto(UPDATED_LANGUAGE));
        assertEquals(UPDATED_LANGUAGE, update.getName());
    }

    @Test
    void update_throwsException_whenIdDoesntExist() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.update(ID_NOT_FOUND, new LanguageDto(FRENCH)));

        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, LANGUAGE, ID_NOT_FOUND), ex.getMessage());
    }

    @Test
    void update_throwsException_whenLanguageExists() {
        service.create(new LanguageDto(ENGLISH));
        Language french = service.create(new LanguageDto(FRENCH));
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class,
                () -> service.update(french.getId(), new LanguageDto(ENGLISH)));

        assertEquals(String.format(ENTITY_ALREADY_HAS_A, LANGUAGE, ENGLISH), ex.getMessage());
    }

    @Test
    void delete_isSuccessful() {
        Language language = service.create(new LanguageDto(FRENCH));

        service.delete(language.getId());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.read(language.getId()));

        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, LANGUAGE, language.getId()), ex.getMessage());
    }

    @Test
    void delete_throwsException_whenIdDoesntExist() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(ID_NOT_FOUND));

        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, LANGUAGE, ID_NOT_FOUND), ex.getMessage());
    }

    @Test
    void delete_throwsException_whenBooksHaveThisLanguageLinked() {
        BookDto bookInEnglish = getLandscapesOfIdentityBook();
        Book book = bookService.create(bookInEnglish);
        List<Language> languages = book.getLanguages().stream().toList();

        assertEquals(1, languages.size());
        EntityAlreadyLinkedException ex = assertThrows(EntityAlreadyLinkedException.class, () -> service.delete(languages.getFirst().getId()));

        assertEquals(String.format(ENTITY_ALREADY_HAS_A_LINK, LANGUAGE, ENGLISH), ex.getMessage());
    }
}