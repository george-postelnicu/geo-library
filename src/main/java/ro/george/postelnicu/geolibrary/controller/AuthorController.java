package ro.george.postelnicu.geolibrary.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.george.postelnicu.geolibrary.dto.author.AuthorDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorResponseDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorsDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorsResponseDto;
import ro.george.postelnicu.geolibrary.mapper.LibraryMapper;
import ro.george.postelnicu.geolibrary.model.Author;
import ro.george.postelnicu.geolibrary.service.AuthorService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;
import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.AUTHORS;
import static ro.george.postelnicu.geolibrary.controller.ApiPrefix.BULK;

@RestController
@RequestMapping(AUTHORS)
@Validated
public class AuthorController {
    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @PostMapping(value = BULK,
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorsResponseDto> createBulk(@Valid @RequestBody AuthorsDto authorsDto) {
        List<Author> authors = service.createBulk(authorsDto);
        List<AuthorResponseDto> responseDtos = authors.stream()
                .map(LibraryMapper.INSTANCE::toAuthorResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(AuthorsResponseDto.of(responseDtos));
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorResponseDto> create(@Valid @RequestBody AuthorDto authorDto) {
        Author author = service.create(authorDto);
        URI location = fromPath("/authors").pathSegment("{id}")
                .buildAndExpand(author.getId()).toUri();
        AuthorResponseDto responseDto = LibraryMapper.INSTANCE.toAuthorResponseDto(author);

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorResponseDto> read(@PathVariable Long id) {
        Author author = service.read(id);
        AuthorResponseDto responseDto = LibraryMapper.INSTANCE.toAuthorResponseDto(author);

        return ResponseEntity.ok().body(responseDto);
    }

    @PutMapping(value = "/{id}",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorResponseDto> update(@PathVariable Long id,
                                                    @Valid @RequestBody AuthorDto authorDto) {
        Author author = service.update(id, authorDto);
        AuthorResponseDto responseDto = LibraryMapper.INSTANCE.toAuthorResponseDto(author);
        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
