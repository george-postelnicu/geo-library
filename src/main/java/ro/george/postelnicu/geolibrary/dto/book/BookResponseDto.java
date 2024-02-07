package ro.george.postelnicu.geolibrary.dto.book;

import jakarta.validation.constraints.NotNull;
import ro.george.postelnicu.geolibrary.dto.author.AuthorResponseDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordResponseDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageResponseDto;

import java.util.Set;

public class BookResponseDto extends BookBaseDto {
    @NotNull
    private Long id;

    private Set<AuthorResponseDto> authors;
    private Set<KeywordResponseDto> keywords;
    private Set<LanguageResponseDto> languages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<AuthorResponseDto> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<AuthorResponseDto> authors) {
        this.authors = authors;
    }

    public Set<KeywordResponseDto> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<KeywordResponseDto> keywords) {
        this.keywords = keywords;
    }

    public Set<LanguageResponseDto> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<LanguageResponseDto> languages) {
        this.languages = languages;
    }
}
