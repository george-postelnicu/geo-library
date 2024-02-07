package ro.george.postelnicu.geolibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.AbstractIntegrationTest;
import ro.george.postelnicu.geolibrary.dto.ErrorDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorResponseDto;
import ro.george.postelnicu.geolibrary.dto.book.BookDto;
import ro.george.postelnicu.geolibrary.dto.book.BookResponseDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordResponseDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageResponseDto;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.service.BookService;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ro.george.postelnicu.geolibrary.DataCommon.*;
import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.BOOKS;
import static ro.george.postelnicu.geolibrary.controller.GlobalControllerAdvice.BAD_REQUEST_ERROR_TYPE;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException.ENTITY_ALREADY_HAS_A;
import static ro.george.postelnicu.geolibrary.exception.EntityNotFoundException.CANNOT_FIND_ENTITY_ID;
import static ro.george.postelnicu.geolibrary.model.EntityName.BOOK;

class BookControllerTest extends AbstractIntegrationTest {

    private final BookController controller;
    private final BookService service;
    private final ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @Autowired
    public BookControllerTest(BookController controller, BookService service, ObjectMapper objectMapper) {
        this.controller = controller;
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalControllerAdvice()).build();
    }

    @Test
    void create_isSuccessful() throws Exception {
        BookDto requestDto = landscapesOfIdentity();

        String responseString = mockMvc.perform(
                        post(BOOKS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookResponseDto responseDto = objectMapper.readValue(responseString, BookResponseDto.class);

        assertNotNull(responseDto);
        assertEquals(requestDto.getName(), responseDto.getName());
        assertEquals(requestDto.getFullTitle(), responseDto.getFullTitle());
        assertEquals(requestDto.getDescription(), responseDto.getDescription());
        assertEquals(requestDto.getIsbn(), responseDto.getIsbn());
        assertEquals(requestDto.getBarcode(), responseDto.getBarcode());
        assertEquals(requestDto.getAuthors(), getAuthorNames(responseDto.getAuthors()));
        assertEquals(requestDto.getKeywords(), getKeywordNames(responseDto.getKeywords()));
        assertEquals(requestDto.getLanguages(), getLanguageNames(responseDto.getLanguages()));
        assertEquals(requestDto.getPublisher(), responseDto.getPublisher());
        assertEquals(requestDto.getCover(), responseDto.getCover());
        assertEquals(requestDto.getPublishYear(), responseDto.getPublishYear());
        assertEquals(requestDto.getPages(), responseDto.getPages());
    }

    @Test
    void create_shouldThrowException_whenBookNameExists() throws Exception {
        BookDto requestDto = landscapesOfIdentity();
        service.create(requestDto);

        String responseString = mockMvc.perform(
                        post(BOOKS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(ENTITY_ALREADY_HAS_A, BOOK, LANDSCAPES_OF_IDENTITY), errorDto.getDetail());
    }

    @Test
    @Transactional
    void read_shouldReturn200_whenIdIsFound() throws Exception {
        BookDto requestDto = landscapesOfIdentity();
        Book book = service.create(requestDto);

        String responseString = mockMvc.perform(
                        get(STR."\{BOOKS}/{id}", book.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookResponseDto responseDto = objectMapper.readValue(responseString, BookResponseDto.class);

        assertNotNull(responseDto);
        assertEquals(requestDto.getName(), responseDto.getName());
        assertEquals(requestDto.getFullTitle(), responseDto.getFullTitle());
        assertEquals(requestDto.getDescription(), responseDto.getDescription());
        assertEquals(requestDto.getIsbn(), responseDto.getIsbn());
        assertEquals(requestDto.getBarcode(), responseDto.getBarcode());
        assertEquals(requestDto.getAuthors(), getAuthorNames(responseDto.getAuthors()));
        assertEquals(requestDto.getKeywords(), getKeywordNames(responseDto.getKeywords()));
        assertEquals(requestDto.getLanguages(), getLanguageNames(responseDto.getLanguages()));
        assertEquals(requestDto.getPublisher(), responseDto.getPublisher());
        assertEquals(requestDto.getCover(), responseDto.getCover());
        assertEquals(requestDto.getPublishYear(), responseDto.getPublishYear());
        assertEquals(requestDto.getPages(), responseDto.getPages());
    }

    @Test
    void read_shouldThrowException_whenIdDoesNotExist() throws Exception {
        String responseString = mockMvc.perform(
                        get(STR."\{BOOKS}/{id}", ID_NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, BOOK, ID_NOT_FOUND), errorDto.getDetail());
    }

    @Test
    void update_isSuccessful() throws Exception {
        BookDto requestDto = landscapesOfIdentity();
        Book book = service.create(requestDto);

        BookDto updatedDto = conflictsAndAdaptations();

        String responseString = mockMvc.perform(
                        put(STR."\{BOOKS}/{id}", book.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookResponseDto responseDto = objectMapper.readValue(responseString, BookResponseDto.class);

        assertNotNull(responseDto);
        assertEquals(updatedDto.getName(), responseDto.getName());
        assertEquals(updatedDto.getFullTitle(), responseDto.getFullTitle());
        assertEquals(updatedDto.getDescription(), responseDto.getDescription());
        assertEquals(updatedDto.getIsbn(), responseDto.getIsbn());
        assertEquals(updatedDto.getBarcode(), responseDto.getBarcode());
        assertEquals(updatedDto.getAuthors(), getAuthorNames(responseDto.getAuthors()));
        assertEquals(updatedDto.getKeywords(), getKeywordNames(responseDto.getKeywords()));
        assertEquals(updatedDto.getLanguages(), getLanguageNames(responseDto.getLanguages()));
        assertEquals(updatedDto.getPublisher(), responseDto.getPublisher());
        assertEquals(updatedDto.getCover(), responseDto.getCover());
        assertEquals(updatedDto.getPublishYear(), responseDto.getPublishYear());
        assertEquals(updatedDto.getPages(), responseDto.getPages());
    }

    @Test
    void update_shouldThrowException_whenNameAlreadyExists() throws Exception {
        BookDto requestDto = landscapesOfIdentity();
        Book book = service.create(requestDto);
        BookDto existingDto = conflictsAndAdaptations();
        service.create(existingDto);

        String responseString = mockMvc.perform(
                        put(STR."\{BOOKS}/{id}", book.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(existingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(ENTITY_ALREADY_HAS_A, BOOK, existingDto.getName()), errorDto.getDetail());
    }

    @Test
    void delete_isSuccessful() throws Exception {
        BookDto requestDto = landscapesOfIdentity();
        Book book = service.create(requestDto);

        mockMvc.perform(
                        delete(STR."\{BOOKS}/{id}", book.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThrows(EntityNotFoundException.class, () -> service.read(book.getId()));
    }

    @Test
    void delete_shouldThrowException_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(
                        delete(STR."\{BOOKS}/{id}", ID_NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private static Set<String> getAuthorNames(Set<AuthorResponseDto> authors) {
        return authors.stream().map(AuthorResponseDto::getName).collect(Collectors.toSet());
    }

    private static Set<String> getKeywordNames(Set<KeywordResponseDto> languages) {
        return languages.stream().map(KeywordResponseDto::getName).collect(Collectors.toSet());
    }

    private static Set<String> getLanguageNames(Set<LanguageResponseDto> languages) {
        return languages.stream().map(LanguageResponseDto::getName).collect(Collectors.toSet());
    }
}