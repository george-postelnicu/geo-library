package ro.george.postelnicu.geolibrary.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.george.postelnicu.geolibrary.dto.language.LanguageDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageResponseDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguagesDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguagesResponseDto;
import ro.george.postelnicu.geolibrary.mapper.LibraryMapper;
import ro.george.postelnicu.geolibrary.model.Language;
import ro.george.postelnicu.geolibrary.service.LanguageService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@RestController
public class LanguageController {
    private final LanguageService service;

    @Autowired
    public LanguageController(LanguageService service) {
        this.service = service;
    }

    @PostMapping(value = "/languages-bulk",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<LanguagesResponseDto> createBulk(@Valid @RequestBody LanguagesDto languagesDto) {
        List<Language> bulk = service.createBulk(languagesDto);
        List<LanguageResponseDto> responseDtos = bulk.stream()
                .map(LibraryMapper.INSTANCE::toLanguageResponseDto)
                .toList();
        return ResponseEntity.ok().body(LanguagesResponseDto.of(responseDtos));
    }

    @PostMapping(value = "/languages",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<LanguageResponseDto> create(@Valid @RequestBody LanguageDto languageDto) {
        Language language = service.create(languageDto);
        URI location = fromPath("/languages").pathSegment("{id}")
                .buildAndExpand(language.getId()).toUri();
        LanguageResponseDto responseDto = LibraryMapper.INSTANCE.toLanguageResponseDto(language);

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping(value = "/languages/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LanguageResponseDto> read(@PathVariable Long id) {
        Language language = service.read(id);
        LanguageResponseDto responseDto = LibraryMapper.INSTANCE.toLanguageResponseDto(language);

        return ResponseEntity.ok().body(responseDto);
    }

    @PutMapping(value = "/languages/{id}",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<LanguageResponseDto> update(@PathVariable Long id,
                                                      @Valid @RequestBody LanguageDto keywordDto) {
        Language language = service.update(id, keywordDto);
        LanguageResponseDto responseDto = LibraryMapper.INSTANCE.toLanguageResponseDto(language);
        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping(value = "/languages/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
