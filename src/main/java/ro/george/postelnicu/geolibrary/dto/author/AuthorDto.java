package ro.george.postelnicu.geolibrary.dto.author;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class AuthorDto {

    @NotBlank
    private String name;

    @JsonCreator
    public AuthorDto(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
