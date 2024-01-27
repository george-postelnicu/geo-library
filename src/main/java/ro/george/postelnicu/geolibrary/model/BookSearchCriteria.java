package ro.george.postelnicu.geolibrary.model;


import java.util.List;

public record BookSearchCriteria(String name, String fullTitle, String isbn, List<String> authors,
                                 List<String> keywords, List<String> languages, String publisher, CoverType coverType,
                                 Integer minYear, Integer maxYear, Integer minPages, Integer maxPages) {
}
