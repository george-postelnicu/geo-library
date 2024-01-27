package ro.george.postelnicu.geolibrary.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.BULK;
import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.LANGUAGES;

@RestController
@RequestMapping(LANGUAGES)
@Validated
public class LanguageController {
    private final LanguageService service;

    @Autowired
    public LanguageController(LanguageService service) {
        this.service = service;
    }

    @PostMapping(value = BULK,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<LanguagesResponseDto> createBulk(@Valid @RequestBody LanguagesDto languagesDto) {
        List<Language> bulk = service.createBulk(languagesDto);
        List<LanguageResponseDto> responseDtos = bulk.stream()
                .map(LibraryMapper.INSTANCE::toLanguageResponseDto)
                .toList();
        return ResponseEntity.ok().body(LanguagesResponseDto.of(responseDtos));
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<LanguageResponseDto> create(@Valid @RequestBody LanguageDto languageDto) {
        Language language = service.create(languageDto);
        URI location = fromPath(LANGUAGES).pathSegment("{id}")
                .buildAndExpand(language.getId()).toUri();
        LanguageResponseDto responseDto = LibraryMapper.INSTANCE.toLanguageResponseDto(language);

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LanguageResponseDto> read(@PathVariable Long id) {
        Language language = service.read(id);
        LanguageResponseDto responseDto = LibraryMapper.INSTANCE.toLanguageResponseDto(language);

        return ResponseEntity.ok().body(responseDto);
    }

    @PutMapping(value = "/{id}",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<LanguageResponseDto> update(@PathVariable Long id,
                                                      @Valid @RequestBody LanguageDto keywordDto) {
        Language language = service.update(id, keywordDto);
        LanguageResponseDto responseDto = LibraryMapper.INSTANCE.toLanguageResponseDto(language);
        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
