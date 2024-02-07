package ro.george.postelnicu.geolibrary.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ro.george.postelnicu.geolibrary.dto.book.BookResponseDto;
import ro.george.postelnicu.geolibrary.model.Book;

@Mapper(uses = BookRelationMapper.class)
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookResponseDto toBookResponseDto(Book book);
}
