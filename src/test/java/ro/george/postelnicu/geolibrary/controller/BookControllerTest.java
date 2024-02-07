package ro.george.postelnicu.geolibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.george.postelnicu.geolibrary.AbstractIntegrationTest;
import ro.george.postelnicu.geolibrary.dto.author.AuthorResponseDto;
import ro.george.postelnicu.geolibrary.dto.book.BookDto;
import ro.george.postelnicu.geolibrary.dto.book.BookResponseDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordResponseDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageResponseDto;
import ro.george.postelnicu.geolibrary.model.CoverType;
import ro.george.postelnicu.geolibrary.service.BookService;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ro.george.postelnicu.geolibrary.DataCommon.landscapesOfIdentity;
import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.BOOKS;

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
    void create() throws Exception {
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
    void read() {


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