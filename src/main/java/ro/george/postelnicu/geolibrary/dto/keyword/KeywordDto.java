package ro.george.postelnicu.geolibrary.dto.keyword;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class KeywordDto {
    @NotBlank
    private String name;

    @JsonCreator
    public KeywordDto(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
