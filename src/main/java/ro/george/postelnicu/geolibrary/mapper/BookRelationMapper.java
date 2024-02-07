package ro.george.postelnicu.geolibrary.mapper;

import org.mapstruct.Mapper;
import ro.george.postelnicu.geolibrary.dto.author.AuthorResponseDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordResponseDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageResponseDto;
import ro.george.postelnicu.geolibrary.model.Author;
import ro.george.postelnicu.geolibrary.model.Keyword;
import ro.george.postelnicu.geolibrary.model.Language;

import java.util.Set;

@Mapper
public interface BookRelationMapper {
    Set<AuthorResponseDto> mapAuthors(Set<Author> authors);

    Set<KeywordResponseDto> mapKeywords(Set<Keyword> keywords);

    Set<LanguageResponseDto> mapLanguages(Set<Language> languages);
}
