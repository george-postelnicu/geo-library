package ro.george.postelnicu.geolibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.george.postelnicu.geolibrary.dto.ErrorDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorResponseDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorsDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorsResponseDto;
import ro.george.postelnicu.geolibrary.model.Author;
import ro.george.postelnicu.geolibrary.service.AuthorService;

import java.util.Set;

import static java.lang.StringTemplate.STR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ro.george.postelnicu.geolibrary.DataCommon.*;
import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.AUTHORS;
import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.BULK;
import static ro.george.postelnicu.geolibrary.controller.GlobalControllerAdvice.BAD_REQUEST_ERROR_TYPE;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException.ENTITY_ALREADY_HAS_A;
import static ro.george.postelnicu.geolibrary.exception.EntityNotFoundException.CANNOT_FIND_ENTITY_ID;
import static ro.george.postelnicu.geolibrary.model.EntityName.AUTHOR;

@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "classpath:/sql/clean-all-data.sql",
})
class AuthorControllerTest {

    private final AuthorController authorController;
    private final AuthorService service;
    private final ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @Autowired
    AuthorControllerTest(AuthorController controller, AuthorService service, ObjectMapper objectMapper) {
        this.authorController = controller;
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(authorController)
                .setControllerAdvice(new GlobalControllerAdvice()).build();
    }

    @Test
    void createBulk_shouldReturn200_whenAuthorsDoNotExist() throws Exception {
        AuthorsDto dto = new AuthorsDto();
        dto.setAuthors(Set.of(LINDA, BART));

        String responseString = this.mockMvc.perform(
                        post(AUTHORS + BULK)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthorsResponseDto responseDto = objectMapper.readValue(responseString, AuthorsResponseDto.class);

        assertNotNull(responseDto.getElements());
        assertEquals(2, responseDto.getElements().size());
    }

    @Test
    void createBulk_shouldThrowException_whenAnyOfTheAuthorsAlreadyExists() throws Exception {
        AuthorsDto dto = new AuthorsDto();
        dto.setAuthors(Set.of(LINDA, BART));
        service.createBulk(dto);

        String responseString = this.mockMvc.perform(
                        post(AUTHORS + BULK)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(ENTITY_ALREADY_HAS_A, AUTHOR, BART), errorDto.getDetail());
    }

    @Test
    void create_shouldReturn201_whenAuthorDoesNotExist() throws Exception {
        AuthorDto dto = new AuthorDto(LINDA);

        String responseString = this.mockMvc.perform(
                        post(AUTHORS)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthorResponseDto responseDto = objectMapper.readValue(responseString, AuthorResponseDto.class);

        assertNotNull(responseDto.getName());
        assertEquals(dto.getName(), responseDto.getName());
    }

    @Test
    void create_shouldThrowException_whenAuthorExists() throws Exception {
        AuthorDto dto = new AuthorDto(LINDA);
        service.create(dto);

        String responseString = this.mockMvc.perform(
                        post(AUTHORS)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(ENTITY_ALREADY_HAS_A, AUTHOR, LINDA), errorDto.getDetail());
    }

    @Test
    void read_shouldReturn200_whenIdIsFound() throws Exception {
        AuthorDto dto = new AuthorDto(LINDA);
        Author author = service.create(dto);

        String responseString = this.mockMvc.perform(
                        get(STR."\{AUTHORS}/\{author.getId()}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthorResponseDto responseDto = objectMapper.readValue(responseString, AuthorResponseDto.class);

        assertNotNull(responseDto.getName());
        assertEquals(author.getName(), responseDto.getName());
    }

    @Test
    void read_shouldThrowException_whenIdIsNotFound() throws Exception {
        String responseString = this.mockMvc.perform(
                        get(STR."\{AUTHORS}/\{ID_NOT_FOUND}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, AUTHOR, ID_NOT_FOUND), errorDto.getDetail());
    }

    @Test
    void update_shouldReturn200_whenAuthorIsUpdated() throws Exception {
        AuthorDto authorDto = new AuthorDto(LINDA);
        Author author = service.create(authorDto);

        authorDto.setName(UPDATED_AUTHOR);

        String responseString = this.mockMvc.perform(
                        put(STR."\{AUTHORS}/\{author.getId()}")
                                .content(objectMapper.writeValueAsString(authorDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthorResponseDto responseDto = objectMapper.readValue(responseString, AuthorResponseDto.class);

        assertNotNull(responseDto.getName());
        assertEquals(UPDATED_AUTHOR, responseDto.getName());
    }

    @Test
    void update_shouldThrowException_whenIdIsNotFound() throws Exception {
        AuthorDto authorDto = new AuthorDto(LINDA);
        String responseString = this.mockMvc.perform(
                        put(STR."\{AUTHORS}/\{ID_NOT_FOUND}")
                                .content(objectMapper.writeValueAsString(authorDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, AUTHOR, ID_NOT_FOUND), errorDto.getDetail());
    }

    @Test
    void delete_shouldReturn204_whenIdIsFound() throws Exception {
        AuthorDto authorDto = new AuthorDto(LINDA);
        Author author = service.create(authorDto);

        this.mockMvc.perform(
                        delete(STR."\{AUTHORS}/\{author.getId()}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldThrowException_whenIdIsNotFound() throws Exception {
        String responseString = this.mockMvc.perform(
                        delete(STR."\{AUTHORS}/\{ID_NOT_FOUND}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, AUTHOR, ID_NOT_FOUND), errorDto.getDetail());
    }
}
