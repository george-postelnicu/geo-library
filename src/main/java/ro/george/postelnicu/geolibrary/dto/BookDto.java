package ro.george.postelnicu.geolibrary.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import ro.george.postelnicu.geolibrary.model.CoverType;
import ro.george.postelnicu.geolibrary.model.StatusType;

import java.util.Set;

public class BookDto {
    @NotBlank
    private String name;
    private String fullTitle;
    private String description;
    private Set<String> authors;
    private Set<String> keywords;
    private Set<String> languages;
    private String publisher;
    @NotBlank
    private String isbn;
    private CoverType cover;
    @Min(value = 1800)
    @Max(value = 2100)
    private Integer publishYear;
    @Min(value = 5)
    @Max(value = 1000)
    private Integer pages;
    private String barcode;
    @NotBlank
    private StatusType status;

    public BookDto(String name, String isbn, StatusType status) {
        this.name = name;
        this.isbn = isbn;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public CoverType getCover() {
        return cover;
    }

    public void setCover(CoverType cover) {
        this.cover = cover;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }
}
