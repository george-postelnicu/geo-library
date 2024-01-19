package ro.george.postelnicu.geolibrary.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ro.george.postelnicu.geolibrary.dto.BookDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorDto;
import ro.george.postelnicu.geolibrary.dto.author.AuthorResponseDto;
import ro.george.postelnicu.geolibrary.dto.book.BookResponseDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordDto;
import ro.george.postelnicu.geolibrary.dto.keyword.KeywordResponseDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageDto;
import ro.george.postelnicu.geolibrary.dto.language.LanguageResponseDto;
import ro.george.postelnicu.geolibrary.model.Author;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.Keyword;
import ro.george.postelnicu.geolibrary.model.Language;
import ro.george.postelnicu.geolibrary.util.StringUtil;

@Mapper
public interface LibraryMapper {
    LibraryMapper INSTANCE = Mappers.getMapper(LibraryMapper.class);

    @Mapping(target = "name", source = "name")
    Author toAuthor(AuthorDto dto);

    @AfterMapping
    default void toAuthor(@MappingTarget Author author) {
        author.setName(StringUtil.splitCapitalizeAndJoin(author.getName()));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    void updateAuthorFromDto(AuthorDto dto, @MappingTarget Author entity);

    AuthorResponseDto toAuthorResponseDto(Author entity);

    @Mapping(target = "name", source = "name")
    Keyword toKeyword(KeywordDto dto);

    @AfterMapping
    default void toKeyword(@MappingTarget Keyword keyword) {
        keyword.setName(StringUtil.splitCapitalizeAndJoin(keyword.getName()));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    void updateKeywordFromDto(KeywordDto dto, @MappingTarget Keyword entity);

    KeywordResponseDto toKeywordResponseDto(Keyword entity);

    @Mapping(target = "name", source = "name")
    Language toLanguage(LanguageDto dto);

    @AfterMapping
    default void toLanguage(@MappingTarget Language language) {
        language.setName(StringUtil.splitCapitalizeAndJoin(language.getName()));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    void updateLanguageFromDto(LanguageDto dto, @MappingTarget Language entity);

    LanguageResponseDto toLanguageResponseDto(Language entity);

    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "keywords", ignore = true)
    @Mapping(target = "languages", ignore = true)
    Book toBook(BookDto dto);

    @Mapping(target = "name", source = "name")
    BookResponseDto toBookResponseDto(Book book);
}
