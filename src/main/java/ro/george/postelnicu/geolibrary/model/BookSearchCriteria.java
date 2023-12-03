package ro.george.postelnicu.geolibrary.model;


import java.util.List;

public class BookSearchCriteria {
    private final String name;
    private final String fullTitle;
    private final String isbn;
    private final List<String> authors;
    private final List<String> keywords;
    private final List<String> languages;
    private final String publisher;
    private final CoverType coverType;
    private final Integer minYear;
    private final Integer maxYear;
    private final Integer minPages;
    private final Integer maxPages;

    public BookSearchCriteria(String name, String fullTitle, String isbn,
                              List<String> authors, List<String> keywords, List<String> languages,
                              String publisher, CoverType coverType,
                              Integer minYear, Integer maxYear,
                              Integer minPages, Integer maxPages) {
        this.name = name;
        this.fullTitle = fullTitle;
        this.isbn = isbn;
        this.authors = authors;
        this.keywords = keywords;
        this.languages = languages;
        this.publisher = publisher;
        this.coverType = coverType;
        this.minYear = minYear;
        this.maxYear = maxYear;
        this.minPages = minPages;
        this.maxPages = maxPages;
    }

    public String getName() {
        return name;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public String getIsbn() {
        return isbn;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getPublisher() {
        return publisher;
    }

    public CoverType getCoverType() {
        return coverType;
    }

    public Integer getMinYear() {
        return minYear;
    }

    public Integer getMaxYear() {
        return maxYear;
    }

    public Integer getMinPages() {
        return minPages;
    }

    public Integer getMaxPages() {
        return maxPages;
    }
}
