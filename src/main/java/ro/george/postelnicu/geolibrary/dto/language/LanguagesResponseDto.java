package ro.george.postelnicu.geolibrary.dto.language;

import ro.george.postelnicu.geolibrary.dto.ListResultDto;

import java.util.List;

public class LanguagesResponseDto extends ListResultDto<LanguageResponseDto> {
    public static LanguagesResponseDto of(List<LanguageResponseDto> elements) {
        LanguagesResponseDto result = new LanguagesResponseDto();
        result.setElements(elements);
        return result;
    }
}
