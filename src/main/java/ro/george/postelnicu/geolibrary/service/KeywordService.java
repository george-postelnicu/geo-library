package ro.george.postelnicu.geolibrary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordsDto;
import ro.george.postelnicu.geolibrary.mapper.LibraryMapper;
import ro.george.postelnicu.geolibrary.model.Keyword;
import ro.george.postelnicu.geolibrary.repository.BookKeywordRelationRepository;
import ro.george.postelnicu.geolibrary.repository.KeywordRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static ro.george.postelnicu.geolibrary.model.EntityName.KEYWORD;

@Service
public class KeywordService {
    private final KeywordRepository repository;
    private final BookKeywordRelationRepository relationRepository;

    @Autowired
    public KeywordService(KeywordRepository repository, BookKeywordRelationRepository relationRepository) {
        this.repository = repository;
        this.relationRepository = relationRepository;
    }

    @Transactional
    public List<Keyword> createBulk(KeywordsDto keywordsDto) {
        Set<String> existingKeywords = keywordsDto.getKeywords().stream()
                .filter(repository::existsByNameIgnoreCase)
                .collect(Collectors.toSet());
        if (!existingKeywords.isEmpty()) {
            throw new EntityAlreadyExistException(KEYWORD, existingKeywords);
        }

        Set<Keyword> newKeywords = keywordsDto.getKeywords().stream()
                .map(Keyword::new)
                .collect(Collectors.toSet());

        return repository.saveAll(newKeywords);
    }

    @Transactional
    public Keyword create(KeywordDto keywordDto) {
        Keyword keyword = LibraryMapper.INSTANCE.toKeyword(keywordDto);

        if (repository.existsByNameIgnoreCase(keywordDto.getName())) {
            throw new EntityAlreadyExistException(KEYWORD, keywordDto.getName());
        }

        return repository.save(keyword);
    }

    @Transactional
    public Keyword createIfNotExisting(KeywordDto keywordDto) {
        Keyword keyword = LibraryMapper.INSTANCE.toKeyword(keywordDto);

        return repository.findByNameIgnoreCase(keyword.getName())
                .orElse(create(keywordDto));
    }

    @Transactional(readOnly = true, propagation = REQUIRED)
    public Keyword read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(KEYWORD, id));
    }

    @Transactional
    public Keyword update(Long id, KeywordDto keywordDto) {
        Keyword keyword = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(KEYWORD, id));

        if (repository.existsByNameIgnoreCase(keywordDto.getName())) {
            throw new EntityAlreadyExistException(KEYWORD, keywordDto.getName());
        }

        LibraryMapper.INSTANCE.updateKeywordFromDto(keywordDto, keyword);

        return repository.save(keyword);
    }

    @Transactional
    public void delete(Long id) {
        Keyword keyword = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(KEYWORD, id));

        if (relationRepository.existsByKeywordId(id)) {
            throw new EntityAlreadyExistException(KEYWORD, keyword.getName());
        }

        repository.delete(keyword);
    }
}
