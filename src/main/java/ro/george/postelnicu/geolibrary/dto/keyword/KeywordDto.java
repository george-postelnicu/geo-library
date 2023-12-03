package ro.george.postelnicu.geolibrary.dto.keyword;

import jakarta.validation.constraints.NotBlank;

public class KeywordDto {
    @NotBlank
    private String name;

    public KeywordDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
