package ro.george.postelnicu.geolibrary.dto.author;

import jakarta.validation.constraints.NotBlank;

public class AuthorDto {

    @NotBlank
    private String name;

    public AuthorDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
