package ro.george.postelnicu.geolibrary.dto.author;

import ro.george.postelnicu.geolibrary.dto.ListResultDto;

import java.util.List;

public class AuthorsResponseDto extends ListResultDto<AuthorResponseDto> {
    public static AuthorsResponseDto of(List<AuthorResponseDto> elements) {
        AuthorsResponseDto result = new AuthorsResponseDto();
        result.setElements(elements);
        return result;
    }

}
