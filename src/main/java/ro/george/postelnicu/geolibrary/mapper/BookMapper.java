package ro.george.postelnicu.geolibrary.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ro.george.postelnicu.geolibrary.dto.book.BookDto;
import ro.george.postelnicu.geolibrary.dto.book.BookResponseDto;
import ro.george.postelnicu.geolibrary.model.Book;

@Mapper(uses = BookRelationMapper.class)
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "keywords", ignore = true)
    @Mapping(target = "languages", ignore = true)
    Book toBook(BookDto dto);

    BookResponseDto toBookResponseDto(Book book);

    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "keywords", ignore = true)
    @Mapping(target = "languages", ignore = true)
    void updateBookFromDto(BookDto dto, @MappingTarget Book book);
}
