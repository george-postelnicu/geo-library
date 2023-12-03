package ro.george.postelnicu.geolibrary.dto.language;

import jakarta.validation.constraints.NotNull;

public class LanguageResponseDto extends LanguageDto {
    @NotNull
    private Long id;

    public LanguageResponseDto(String name, Long id) {
        super(name);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
