package ro.george.postelnicu.geolibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.george.postelnicu.geolibrary.AbstractIntegrationTest;
import ro.george.postelnicu.geolibrary.dto.ErrorDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordResponseDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordsDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordsResponseDto;
import ro.george.postelnicu.geolibrary.model.Keyword;
import ro.george.postelnicu.geolibrary.service.KeywordService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ro.george.postelnicu.geolibrary.DataCommon.*;
import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.BULK;
import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.KEYWORDS;
import static ro.george.postelnicu.geolibrary.controller.GlobalControllerAdvice.BAD_REQUEST_ERROR_TYPE;
import static ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException.ENTITY_ALREADY_HAS_A;
import static ro.george.postelnicu.geolibrary.exception.EntityNotFoundException.CANNOT_FIND_ENTITY_ID;
import static ro.george.postelnicu.geolibrary.model.EntityName.KEYWORD;

class KeywordControllerTest extends AbstractIntegrationTest {
    private final KeywordController controller;
    private final KeywordService service;
    private final ObjectMapper objectMapper;
    private final Long NON_EXISTING_ID = 0L;
    private MockMvc mockMvc;

    @Autowired
    KeywordControllerTest(KeywordController controller, KeywordService service, ObjectMapper objectMapper) {
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
    void createBulk_shouldReturn200_whenKeywordsDoNotExist() throws Exception {
        KeywordsDto dto = new KeywordsDto();
        dto.setKeywords(Set.of(ART, ARCHITECTURE));

        String responseString = this.mockMvc.perform(
                        post(KEYWORDS + BULK)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        KeywordsResponseDto responseDto = objectMapper.readValue(responseString, KeywordsResponseDto.class);

        assertNotNull(responseDto.getElements());
        assertEquals(2, responseDto.getElements().size());
    }

    @Test
    void createBulk_shouldThrowException_whenAnyOfTheKeywordsAlreadyExists() throws Exception {
        KeywordsDto dto = new KeywordsDto();
        dto.setKeywords(Set.of(ART, ARCHITECTURE));
        service.createBulk(dto);

        String responseString = this.mockMvc.perform(
                        post(KEYWORDS + BULK)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(ENTITY_ALREADY_HAS_A, KEYWORD, ART), errorDto.getDetail());
    }

    @Test
    void create_shouldReturn201_whenKeywordDoesNotExist() throws Exception {
        KeywordDto keywordDto = new KeywordDto(ART);

        String responseString = this.mockMvc.perform(
                        post(KEYWORDS)
                                .content(objectMapper.writeValueAsString(keywordDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        KeywordResponseDto responseDto = objectMapper.readValue(responseString, KeywordResponseDto.class);

        assertNotNull(responseDto.getName());
        assertEquals(keywordDto.getName(), responseDto.getName());
    }

    @Test
    void create_shouldThrowException_whenKeywordExists() throws Exception {
        KeywordDto keywordDto = new KeywordDto(ART);
        service.create(keywordDto);

        String responseString = this.mockMvc.perform(
                        post(KEYWORDS)
                                .content(objectMapper.writeValueAsString(keywordDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(ENTITY_ALREADY_HAS_A, KEYWORD, ART), errorDto.getDetail());
    }

    @Test
    void read_shouldReturn200_whenIdIsFound() throws Exception {
        KeywordDto keywordDto = new KeywordDto(ART);
        Keyword keyword = service.create(keywordDto);

        String responseString = this.mockMvc.perform(
                        get(STR."\{KEYWORDS}/\{keyword.getId()}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        KeywordResponseDto responseDto = objectMapper.readValue(responseString, KeywordResponseDto.class);

        assertNotNull(responseDto.getName());
        assertEquals(keyword.getName(), responseDto.getName());
    }

    @Test
    void read_shouldThrowException_whenIdIsNotFound() throws Exception {
        String responseString = this.mockMvc.perform(
                        get(STR."\{KEYWORDS}/\{NON_EXISTING_ID}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, KEYWORD, NON_EXISTING_ID), errorDto.getDetail());
    }

    @Test
    void update_shouldReturn200_whenKeywordIsUpdated() throws Exception {
        KeywordDto keywordDto = new KeywordDto(ART);
        Keyword keyword = service.create(keywordDto);

        keywordDto.setName(UPDATED_KEYWORD);

        String responseString = this.mockMvc.perform(
                        put(STR."\{KEYWORDS}/\{keyword.getId()}")
                                .content(objectMapper.writeValueAsString(keywordDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        KeywordResponseDto responseDto = objectMapper.readValue(responseString, KeywordResponseDto.class);

        assertNotNull(responseDto.getName());
        assertEquals(UPDATED_KEYWORD, responseDto.getName());
    }

    @Test
    void update_shouldThrowException_whenIdIsNotFound() throws Exception {
        KeywordDto keywordDto = new KeywordDto(ART);
        String responseString = this.mockMvc.perform(
                        put(STR."\{KEYWORDS}/\{NON_EXISTING_ID}")
                                .content(objectMapper.writeValueAsString(keywordDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, KEYWORD, NON_EXISTING_ID), errorDto.getDetail());
    }

    @Test
    void delete_shouldReturn204_whenIdIsFound() throws Exception {
        KeywordDto keywordDto = new KeywordDto(ART);
        Keyword keyword = service.create(keywordDto);

        this.mockMvc.perform(
                        delete(STR."\{KEYWORDS}/\{keyword.getId()}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldThrowException_whenIdIsNotFound() throws Exception {
        String responseString = this.mockMvc.perform(
                        delete(STR."\{KEYWORDS}/\{NON_EXISTING_ID}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorDto errorDto = objectMapper.readValue(responseString, ErrorDto.class);

        assertEquals(BAD_REQUEST_ERROR_TYPE, errorDto.getTitle());
        assertEquals(String.format(CANNOT_FIND_ENTITY_ID, KEYWORD, NON_EXISTING_ID), errorDto.getDetail());
    }
}
