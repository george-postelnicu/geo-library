package ro.george.postelnicu.geolibrary.dto.keyword;

import jakarta.validation.Valid;

import java.util.HashSet;
import java.util.Set;

public class KeywordsDto {
    @Valid
    private Set<String> keywords = new HashSet<>();

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }
}
