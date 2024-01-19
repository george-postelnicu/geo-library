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
import ro.george.postelnicu.geolibrary.repository.LanguageRepository;
import ro.george.postelnicu.geolibrary.util.StringUtil;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static ro.george.postelnicu.geolibrary.model.EntityName.LANGUAGE;
import static ro.george.postelnicu.geolibrary.util.StringUtil.splitCapitalizeAndJoin;

@Service
public class LanguageService {
    private final LanguageRepository repository;

    @Autowired
    public LanguageService(LanguageRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<Language> createBulk(LanguagesDto request) {
        return request.getLanguages().stream()
                .map(StringUtil::splitCapitalizeAndJoin)
                .map(LanguageDto::new)
                .map(this::create)
                .collect(Collectors.toList());
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
        return repository.findByNameIgnoreCase(languageDto.getName())
                .orElseGet(() -> create(languageDto));
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
        Language language = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(LANGUAGE, id));

        if (language.getBooks().isEmpty()) {
            repository.delete(language);
        } else {
            throw new EntityAlreadyLinkedException(LANGUAGE, language.getName());
        }
    }
}
