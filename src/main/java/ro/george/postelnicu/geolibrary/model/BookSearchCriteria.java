package ro.george.postelnicu.geolibrary.model;


import java.util.Set;

public record BookSearchCriteria(String name, String fullTitle, String description, String isbn, Set<String> authors,
                                 Set<String> keywords, Set<String> languages, String publisher, CoverType coverType,
                                 Integer minYear, Integer maxYear, Integer minPages, Integer maxPages) {
}
