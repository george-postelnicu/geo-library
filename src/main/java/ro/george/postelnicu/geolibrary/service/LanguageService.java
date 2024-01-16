package ro.george.postelnicu.geolibrary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.dto.language.LanguageDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguagesDto;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyLinkedException;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;
import ro.george.postelnicu.geolibrary.mapper.LibraryMapper;
import ro.george.postelnicu.geolibrary.model.Language;
import ro.george.postelnicu.geolibrary.repository.BookLanguageRelationRepository;
import ro.george.postelnicu.geolibrary.repository.LanguageRepository;
import ro.george.postelnicu.geolibrary.util.StringUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static ro.george.postelnicu.geolibrary.model.EntityName.LANGUAGE;
import static ro.george.postelnicu.geolibrary.util.StringUtil.splitCapitalizeAndJoin;
import static ro.george.postelnicu.geolibrary.util.StringUtil.splitCapitalizeAndJoins;

@Service
public class LanguageService {
    private final LanguageRepository repository;
    private final BookLanguageRelationRepository relationRepository;

    @Autowired
    public LanguageService(LanguageRepository repository, BookLanguageRelationRepository relationRepository) {
        this.repository = repository;
        this.relationRepository = relationRepository;
    }

    @Transactional
    public List<Language> createBulk(LanguagesDto request) {
        Set<String> existingLanguages = request.getLanguages().stream()
                .filter(repository::existsByNameIgnoreCase)
                .collect(Collectors.toSet());
        if (!existingLanguages.isEmpty()) {
            throw new EntityAlreadyExistException(LANGUAGE, splitCapitalizeAndJoins(existingLanguages));
        }

        List<Language> languages = request.getLanguages().stream()
                .map(StringUtil::splitCapitalizeAndJoin)
                .map(Language::new)
                .collect(Collectors.toList());

        return repository.saveAll(languages);
    }

    @Transactional
    public Language create(LanguageDto languageDto) {
        Language language = LibraryMapper.INSTANCE.toLanguage(languageDto);

        if (repository.existsByNameIgnoreCase(languageDto.getName())) {
            throw new EntityAlreadyExistException(LANGUAGE, splitCapitalizeAndJoin(languageDto.getName()));
        }

        return repository.save(language);
    }

    @Transactional
    public Language createIfNotExisting(LanguageDto languageDto) {
        List<Language> languages = repository.findByNameIgnoreCase(languageDto.getName())
                .stream()
                .toList();
        if (languages.isEmpty()) {
            return create(languageDto);
        } else {
            return languages.get(0);
        }
    }

    @Transactional(readOnly = true, propagation = REQUIRED)
    public Language read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(LANGUAGE, id));
    }

    @Transactional
    public Language update(Long id, LanguageDto languageDto) {
        Language language = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(LANGUAGE, id));

        if (repository.existsByNameIgnoreCaseAndIdIsNot(languageDto.getName(), id)) {
            throw new EntityAlreadyExistException(LANGUAGE, splitCapitalizeAndJoin(languageDto.getName()));
        }

        LibraryMapper.INSTANCE.updateLanguageFromDto(languageDto, language);

        return repository.save(language);
    }

    @Transactional
    public void delete(Long id) {
//        Language [3502][English]
//        repository.findAll().forEach(language -> System.out.printf("Language [%s][%s]%n", language.getId(),language.getName()));
        Language language = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(LANGUAGE, id));

        if (relationRepository.existsByLanguageId(id)) {
            throw new EntityAlreadyLinkedException(LANGUAGE, language.getName());
        }

        repository.delete(language);
    }
}
