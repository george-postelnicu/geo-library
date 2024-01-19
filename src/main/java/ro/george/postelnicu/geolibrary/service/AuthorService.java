package ro.george.postelnicu.geolibrary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.dto.author.AuthorDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorsDto;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyLinkedException;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;
import ro.george.postelnicu.geolibrary.mapper.LibraryMapper;
import ro.george.postelnicu.geolibrary.model.Author;
import ro.george.postelnicu.geolibrary.repository.AuthorRepository;
import ro.george.postelnicu.geolibrary.util.StringUtil;

import java.util.List;
import java.util.stream.Collectors;

import static ro.george.postelnicu.geolibrary.model.EntityName.AUTHOR;
import static ro.george.postelnicu.geolibrary.util.StringUtil.splitCapitalizeAndJoin;

@Service
public class AuthorService {
    private final AuthorRepository repository;

    @Autowired
    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<Author> createBulk(AuthorsDto request) {
        return request.getAuthors().stream()
                .map(StringUtil::splitCapitalizeAndJoin)
                .map(AuthorDto::new)
                .map(this::create)
                .collect(Collectors.toList());
    }

    @Transactional
    public Author create(AuthorDto authorDto) {
        Author author = LibraryMapper.INSTANCE.toAuthor(authorDto);

        if (repository.existsByNameIgnoreCase(authorDto.getName())) {
            throw new EntityAlreadyExistException(AUTHOR, splitCapitalizeAndJoin(authorDto.getName()));
        }

        return repository.save(author);
    }

    @Transactional
    public Author createIfNotExisting(AuthorDto authorDto) {
        return repository.findByNameIgnoreCase(authorDto.getName())
                .orElseGet(() -> create(authorDto));
    }

    @Transactional
    public Author read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR, id));
    }

    @Transactional
    public Author update(Long id, AuthorDto authorDto) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR, id));

        if (repository.existsByNameIgnoreCaseAndIdIsNot(authorDto.getName(), id)) {
            throw new EntityAlreadyExistException(AUTHOR, splitCapitalizeAndJoin(authorDto.getName()));
        }

        LibraryMapper.INSTANCE.updateAuthorFromDto(authorDto, author);

        return repository.save(author);
    }

    @Transactional
    public void delete(Long id) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR, id));

        if (author.getBooks().isEmpty()) {
            repository.delete(author);
        } else {
            throw new EntityAlreadyLinkedException(AUTHOR, author.getName());
        }
    }
}
