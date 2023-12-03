package ro.george.postelnicu.geolibrary.dto.language;

import jakarta.validation.constraints.NotBlank;

public class LanguageDto {

    @NotBlank
    private String name;

    public LanguageDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
