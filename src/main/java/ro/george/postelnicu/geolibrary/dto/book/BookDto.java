package ro.george.postelnicu.geolibrary.dto.book;

import java.util.Set;

public class BookDto extends BookBaseDto {
    private Set<String> authors;
    private Set<String> keywords;
    private Set<String> languages;

    public Set<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

}
