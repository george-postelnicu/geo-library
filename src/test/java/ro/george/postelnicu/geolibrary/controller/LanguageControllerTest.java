package ro.george.postelnicu.geolibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.george.postelnicu.geolibrary.dto.ErrorDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageResponseDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguagesDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguagesResponseDto;
import ro.george.postelnicu.geolibrary.model.Language;
import ro.george.postelnicu.geolibrary.service.LanguageService;

import java.util.Set;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ro.george.postelnicu.geolibrary.controller.GlobalControllerAdvice.BAD_REQUEST_ERROR_TYPE;
import static ro.george.postelnicu.geolibrary.controller.LanguageController.LANGUAGE_API_BASE;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException.ENTITY_ALREADY_HAS_A;
import static ro.george.postelnicu.geolibrary.exception.EntityNotFoundException.CANNOT_FIND_ENTITY_ID;
import static ro.george.postelnicu.geolibrary.model.EntityName.LANGUAGE;

@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "classpath:/sql/clean-all-data.sql",
})
class LanguageControllerTest {

    public static final String BULK = "/bulk";
    public static final String ENGLISH = "English";
    public static final String FRENCH = "French";
    public static final String UPDATED_LANGUAGE = "German";
    private final LanguageController languageController;
    private final LanguageService languageService;
    private final ObjectMapper objectMapper;
    private final Long NON_EXISTING_ID = 0L;
    private MockMvc mockMvc;

    @Autowired
    LanguageControllerTest(LanguageController languageController, LanguageService languageService, ObjectMapper objectMapper) {
        this.languageController = languageController;
        this.languageService = languageService;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(languageController)
                .setControllerAdvice(new GlobalControllerAdvice()).build();
    }

    @Test
    void createBulk_shouldReturn200_whenLanguagesDoNotExist() throws Exception {
        LanguagesDto languagesDto = new LanguagesDto();
        languagesDto.setLanguages(Set.of(ENGLISH, FRENCH));

        String responseString = this.mockMvc
                .perform(
                        post(LANGUAGE_API_BASE + BULK)
                                .content(objectMapper.writeValueAsString(languagesDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        LanguagesResponseDto responseDto = objectMapper.readValue(responseString, LanguagesResponseDto.class);

        assertNotNull(responseDto.getElements());
        assertEquals(2, responseDto.getElements().size());
    }

    @Test
    void createBulk_shouldThrowException_whenAnyOfTheLanguagesAlreadyExists() throws Exception {
        LanguagesDto languagesDto = new LanguagesDto();
        languagesDto.setLanguages(Set.of(ENGLISH, FRENCH));
        languageService.createBulk(languagesDto);

        String responseString = this.mockMvc
                .perform(
                        post(LANGUAGE_API_BASE + BULK)
                                .content(objectMapper.writeValueAsString(languagesDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(ENTITY_ALREADY_HAS_A, LANGUAGE, ENGLISH), errorDto.getDetail());
    }

    @Test
    void create_shouldReturn201_whenLanguageDoesNotExist() throws Exception {
        LanguageDto createDto = new LanguageDto(ENGLISH);

        String responseString = this.mockMvc
                .perform(
                        post(LANGUAGE_API_BASE)
                                .content(objectMapper.writeValueAsString(createDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, startsWith(LANGUAGE_API_BASE)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        LanguageResponseDto newDto = objectMapper.readValue(responseString, LanguageResponseDto.class);

        assertNotNull(newDto.getId());
        assertEquals(createDto.getName(), newDto.getName());
    }

    @Test
    void create_shouldThrowException_whenLanguageExists() throws Exception {
        LanguageDto createDto = new LanguageDto(ENGLISH);
        languageService.create(createDto);

        String responseString = this.mockMvc
                .perform(
                        post(LANGUAGE_API_BASE)
                                .content(objectMapper.writeValueAsString(createDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(ENTITY_ALREADY_HAS_A, LANGUAGE, createDto.getName()), errorDto.getDetail());
    }

    @Test
    void read_shouldReturn200_whenIdIsFound() throws Exception {
        LanguageDto createDto = new LanguageDto(ENGLISH);
        Language createdLanguage = languageService.create(createDto);

        String responseString = this.mockMvc
                .perform(
                        get(LANGUAGE_API_BASE + "/" + createdLanguage.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        LanguageResponseDto retrievedDto = objectMapper.readValue(responseString, LanguageResponseDto.class);

        assertEquals(createdLanguage.getId(), retrievedDto.getId());
        assertEquals(createDto.getName(), retrievedDto.getName());
    }

    @Test
    void read_shouldThrowException_whenIdIsNotFound() throws Exception {
        String responseString = this.mockMvc
                .perform(
                        get(LANGUAGE_API_BASE + "/" + NON_EXISTING_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, LANGUAGE, NON_EXISTING_ID), errorDto.getDetail());
    }

    @Test
    void update_shouldReturn200_whenLanguageIsUpdated() throws Exception {
        LanguageDto createDto = new LanguageDto(ENGLISH);
        Language createdLanguage = languageService.create(createDto);
        LanguageDto updateDto = new LanguageDto(UPDATED_LANGUAGE);

        String responseString = this.mockMvc
                .perform(
                        put(LANGUAGE_API_BASE + "/" + createdLanguage.getId())
                                .content(objectMapper.writeValueAsString(updateDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        LanguageResponseDto updatedDto = objectMapper.readValue(responseString, LanguageResponseDto.class);

        assertEquals(createdLanguage.getId(), updatedDto.getId());
        assertEquals(UPDATED_LANGUAGE, updatedDto.getName());
    }

    @Test
    void update_shouldThrowException_whenIdIsNotFound() throws Exception {
        LanguageDto createDto = new LanguageDto(ENGLISH);

        String responseString = this.mockMvc
                .perform(
                        put(LANGUAGE_API_BASE + "/" + NON_EXISTING_ID)
                                .content(objectMapper.writeValueAsString(createDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, LANGUAGE, NON_EXISTING_ID), errorDto.getDetail());
    }

    @Test
    void delete_shouldReturn204_whenIdIsFound() throws Exception {
        LanguageDto createDto = new LanguageDto(ENGLISH);
        Language createdLanguage = languageService.create(createDto);

        this.mockMvc
                .perform(
                        delete(LANGUAGE_API_BASE + "/" + createdLanguage.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldThrowException_whenIdIsNotFound() throws Exception {
        String responseString = this.mockMvc
                .perform(
                        delete(LANGUAGE_API_BASE + "/" + NON_EXISTING_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, LANGUAGE, NON_EXISTING_ID), errorDto.getDetail());
    }
}
