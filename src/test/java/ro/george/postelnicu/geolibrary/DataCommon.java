package ro.george.postelnicu.geolibrary;

import ro.george.postelnicu.geolibrary.dto.BookDto;

import java.util.Set;

import static ro.george.postelnicu.geolibrary.model.CoverType.SOFTCOVER_WITH_DUST_JACKET;
import static ro.george.postelnicu.geolibrary.model.StatusType.HAVE;

public class DataCommon {
    public static final String ART = "Art";
    public static final String ARCHITECTURE = "Architecture";
    public static final String UPDATED_KEYWORD = "Finance";

    public static final String ENGLISH = "English";
    public static final String FRENCH = "French";
    public static final String UPDATED_LANGUAGE = "German";

    public static final String LINDA = "Linda Kalijundi";
    public static final String BART = "Bart Pushaw";
    public static final String UPDATED_AUTHOR = "Kadi Polli";

    public static final long ID_NOT_FOUND = 0L;
    public static final String LANDSCAPES_OF_IDENTITY = "Landscapes of Identity";
    public static final String ART_MUSEUM_OF_ESTONIA = "Art Museum of Estonia";

    public static BookDto getLandscapesOfIdentityBook() {
        BookDto dto = new BookDto(LANDSCAPES_OF_IDENTITY, "ISBN 978-9949-687-32-9", HAVE);
        dto.setFullTitle("Landscapes of Identity: Estonian Art 1700-1945 The 3rd-floor permanent exhibition of the Kumu Art Museum");
        dto.setDescription("Lorem Ipsum");
        dto.setAuthors(Set.of("Linda Kalijundi", "Kadi Polli", "Bart Pushaw", "Kaja Kahrik"));
        dto.setKeywords(Set.of("Kumu Art Museum", "Art", "Estonian Art"));
        dto.setLanguages(Set.of("English"));
        dto.setPublisher(ART_MUSEUM_OF_ESTONIA);
        dto.setCover(SOFTCOVER_WITH_DUST_JACKET);
        dto.setPublishYear(2021);
        dto.setPages(111);
        dto.setBarcode("9789949687329");
        return dto;
    }

    public static BookDto anotherEnglishBook() {
        BookDto dto = new BookDto("100 Steps Through 20th Century Estonian Architecture", "ISBN 978-9949-9078-6-1", HAVE);
        dto.setLanguages(Set.of("English"));
        dto.setAuthors(Set.of("Linda Kalijundi", "Kadi Polli", "Bart Pushaw", "Kaja Kahrik"));
        dto.setKeywords(Set.of("Kumu Art Museum", "Art", "Estonian Art"));
        dto.setPublisher(ART_MUSEUM_OF_ESTONIA);
        return dto;
    }
}
