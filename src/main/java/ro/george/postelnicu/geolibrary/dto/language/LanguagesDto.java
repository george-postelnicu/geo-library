package ro.george.postelnicu.geolibrary.dto.language;

import jakarta.validation.Valid;

import java.util.HashSet;
import java.util.Set;

public class LanguagesDto {
    @Valid
    private Set<String> languages = new HashSet<>();

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }
}
