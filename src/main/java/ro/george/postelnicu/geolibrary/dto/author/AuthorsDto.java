package ro.george.postelnicu.geolibrary.dto.author;

import jakarta.validation.Valid;

import java.util.HashSet;
import java.util.Set;

public class AuthorsDto {
    @Valid
    private Set<String> authors = new HashSet<>();

    public Set<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }
}
