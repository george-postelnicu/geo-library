package ro.george.postelnicu.geolibrary.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordResponseDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordsDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordsResponseDto;
import ro.george.postelnicu.geolibrary.mapper.LibraryMapper;
import ro.george.postelnicu.geolibrary.model.Keyword;
import ro.george.postelnicu.geolibrary.service.KeywordService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@RestController
public class KeywordController {
    private final KeywordService service;

    public KeywordController(KeywordService service) {
        this.service = service;
    }

    @PostMapping(value = "/keywords-bulk",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<KeywordsResponseDto> createBulk(@Valid @RequestBody KeywordsDto keywordsDto) {
        List<Keyword> keywords = service.createBulk(keywordsDto);
        List<KeywordResponseDto> responseDtos = keywords.stream()
                .map(LibraryMapper.INSTANCE::toKeywordResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(KeywordsResponseDto.of(responseDtos));
    }

    @PostMapping(value = "/keywords",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<KeywordResponseDto> create(@Valid @RequestBody KeywordDto keywordDto) {
        Keyword keyword = service.create(keywordDto);
        URI location = fromPath("/keywords").pathSegment("{id}")
                .buildAndExpand(keyword.getId()).toUri();
        KeywordResponseDto responseDto = LibraryMapper.INSTANCE.toKeywordResponseDto(keyword);

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping(value = "/keywords/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<KeywordResponseDto> read(@PathVariable Long id) {
        Keyword keyword = service.read(id);
        KeywordResponseDto responseDto = LibraryMapper.INSTANCE.toKeywordResponseDto(keyword);

        return ResponseEntity.ok().body(responseDto);
    }

    @PutMapping(value = "/keywords/{id}",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<KeywordResponseDto> update(@PathVariable Long id,
                                                     @Valid @RequestBody KeywordDto keywordDto) {
        Keyword keyword = service.update(id, keywordDto);
        KeywordResponseDto responseDto = LibraryMapper.INSTANCE.toKeywordResponseDto(keyword);
        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping(value = "/keywords/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
