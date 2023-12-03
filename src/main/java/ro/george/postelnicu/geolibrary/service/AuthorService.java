package ro.george.postelnicu.geolibrary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.dto.author.AuthorDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorsDto;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;
import ro.george.postelnicu.geolibrary.mapper.LibraryMapper;
import ro.george.postelnicu.geolibrary.model.Author;
import ro.george.postelnicu.geolibrary.repository.AuthorRepository;
import ro.george.postelnicu.geolibrary.repository.BookAuthorRelationRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static ro.george.postelnicu.geolibrary.model.EntityName.AUTHOR;

@Service
public class AuthorService {
    private final AuthorRepository repository;
    private final BookAuthorRelationRepository relationRepository;

    @Autowired
    public AuthorService(AuthorRepository repository, BookAuthorRelationRepository relationRepository) {
        this.repository = repository;
        this.relationRepository = relationRepository;
    }

    @Transactional
    public List<Author> createBulk(AuthorsDto request) {
        Set<String> existingAuthors = request.getAuthors().stream()
                .filter(repository::existsByNameIgnoreCase)
                .collect(Collectors.toSet());
        if (!existingAuthors.isEmpty()) {
            throw new EntityAlreadyExistException(AUTHOR, existingAuthors);
        }

        Set<Author> authors = request.getAuthors().stream()
                .map(Author::new)
                .collect(Collectors.toSet());

        return repository.saveAll(authors);
    }

    @Transactional
    public Author create(AuthorDto authorDto) {
        Author author = LibraryMapper.INSTANCE.toAuthor(authorDto);

        if (repository.existsByNameIgnoreCase(authorDto.getName())) {
            throw new EntityAlreadyExistException(AUTHOR, authorDto.getName());
        }

//        LibraryMapper.INSTANCE.updateAuthorFromDto(authorDto, author);

        return repository.save(author);
    }

    @Transactional
    public Author createIfNotExisting(AuthorDto authorDto) {
        Author author = LibraryMapper.INSTANCE.toAuthor(authorDto);

        return repository.findByNameIgnoreCase(author.getName())
                .orElse(create(authorDto));
    }

    @Transactional(readOnly = true, propagation = REQUIRED)
    public Author read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR, id));
    }

    @Transactional
    public Author update(Long id, AuthorDto authorDto) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR, id));

        if (repository.existsByNameIgnoreCase(authorDto.getName())) {
            throw new EntityAlreadyExistException(AUTHOR, authorDto.getName());
        }

        LibraryMapper.INSTANCE.updateAuthorFromDto(authorDto, author);

        return repository.save(author);
    }

    @Transactional
    public void delete(Long id) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR, id));

        if (relationRepository.existsByAuthorId(id)) {
            throw new EntityAlreadyExistException(AUTHOR, author.getName());
        }

        repository.delete(author);
    }
}
