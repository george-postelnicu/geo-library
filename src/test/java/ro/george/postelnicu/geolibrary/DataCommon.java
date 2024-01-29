package ro.george.postelnicu.geolibrary;

import ro.george.postelnicu.geolibrary.dto.BookDto;

import java.util.Set;

import static ro.george.postelnicu.geolibrary.model.CoverType.HARDCOVER;
import static ro.george.postelnicu.geolibrary.model.CoverType.SOFTCOVER_WITH_DUST_JACKET;
import static ro.george.postelnicu.geolibrary.model.StatusType.HAVE;

public class DataCommon {
    public static final String ART = "Art";
    public static final String ARCHITECTURE = "Architecture";
    public static final String FINANCE = "Finance";

    public static final String ENGLISH = "English";
    public static final String FRENCH = "French";
    public static final String ESTONIAN = "Estonian";

    public static final String LINDA = "Linda Kalijundi";
    public static final String BART = "Bart Pushaw";
    public static final String KADI = "Kadi Polli";

    public static final long ID_NOT_FOUND = 0L;
    public static final String ART_MUSEUM_OF_ESTONIA = "Art Museum of Estonia";
    public static final String LANDSCAPES_OF_IDENTITY = "Landscapes of Identity";
    public static final String CONFLICTS_AND_ADAPTATIONS = "Conflicts and adaptations";
    public static final String _20TH_CENTURY_ESTONIAN_ARCHITECTURE = "100 Steps Through 20th Century Estonian Architecture";
    public static final String HOUSES_YOU_NEED_TO_VISIT_BEFORE_YOU_DIE = "150 Houses You Need to Visit Before You Die";
    public static final int CONFLICTS_PUBLISH_YEAR = 2023;
    public static final int ESTONIAN_ART_BOOKS_PAGE_NR = 111;
    public static final String LANNOO = "Lannoo";
    public static final String ESTONIAN_MUSEUM_OF_ARCHITECTURE = "Estonian Museum of Architecture";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String KAJA = "Kaja Kahrik";
    public static final String CONFLICTS_AND_ADAPTATIONS_FULL_TITLE = "Conflicts and Adaptations. Estonian Art of the Soviet Era (1940-1991)";
    public static final String LOREM_IPSUM = "Lorem Ipsum";
    public static final String ISBN_HOUSES_YOU_NEED_TO_VISIT = "ISBN 978-940-14620-4-4";
    public static final String BARCODE_HOUSES_YOU_NEED_TO_VISIT = "9789401462044";

    public static BookDto landscapesOfIdentity() {
        BookDto dto = new BookDto(LANDSCAPES_OF_IDENTITY, "ISBN 978-9949-687-32-9", HAVE);
        dto.setFullTitle("Landscapes of Identity: Estonian Art 1700-1945 The 3rd-floor permanent exhibition of the Kumu Art Museum");
        dto.setDescription(LOREM_IPSUM);
        dto.setAuthors(Set.of(LINDA, KADI, BART, KAJA));
        dto.setKeywords(estonianArtKeywords());
        dto.setLanguages(Set.of("English"));
        dto.setPublisher(ART_MUSEUM_OF_ESTONIA);
        dto.setCover(SOFTCOVER_WITH_DUST_JACKET);
        dto.setPublishYear(2021);
        dto.setPages(ESTONIAN_ART_BOOKS_PAGE_NR);
        dto.setBarcode("9789949687329");
        return dto;
    }

    public static BookDto anotherBookLikeLandscapes() {
        BookDto dto = landscapesOfIdentity();
        dto.setName(_20TH_CENTURY_ESTONIAN_ARCHITECTURE);
        dto.setIsbn("ISBN 978-9949-9078-6-1");
        dto.setBarcode("9789949907861");
        dto.setFullTitle(_20TH_CENTURY_ESTONIAN_ARCHITECTURE);
        return dto;
    }

    public static BookDto conflictsAndAdaptations() {
        BookDto dto = new BookDto(CONFLICTS_AND_ADAPTATIONS, "ISBN 978-9949-687-44-2", HAVE);
        dto.setFullTitle(CONFLICTS_AND_ADAPTATIONS_FULL_TITLE);
        dto.setDescription(LOREM_IPSUM);
        dto.setAuthors(Set.of("Anu Allas", "Sirje Helme", "Liisa Kaljula", KAJA));
        dto.setKeywords(estonianArtKeywords());
        dto.setLanguages(Set.of("English"));
        dto.setCover(SOFTCOVER_WITH_DUST_JACKET);
        dto.setPublisher(ART_MUSEUM_OF_ESTONIA);
        dto.setPublishYear(CONFLICTS_PUBLISH_YEAR);
        dto.setPages(ESTONIAN_ART_BOOKS_PAGE_NR);
        dto.setBarcode("9789949687442");
        return dto;
    }

    public static Set<String> estonianArtKeywords() {
        return Set.of("Kumu Art Museum", ART, "Estonian Art");
    }


    public static BookDto oneHundredStepsThrough20thCenturyEstonianArchitecture() {
        BookDto dto = new BookDto(_20TH_CENTURY_ESTONIAN_ARCHITECTURE, "ISBN 978-9949-9078-6-1", HAVE);
        dto.setLanguages(bothLanguages());
        dto.setAuthors(Set.of("Lilian Hansar", "Jaak Huimerind", "Karen Jagodin", "Liina Jänes", "Mart Kalm",
                "Epp Lankots", "Maris Mändel", "Triin Ojari", "Oliver Orro"));
        dto.setKeywords(Set.of("20th Century Architecture", "Architecture", "Estonian Architecture"));
        dto.setCover(SOFTCOVER_WITH_DUST_JACKET);
        dto.setPublisher(ESTONIAN_MUSEUM_OF_ARCHITECTURE);
        dto.setPublishYear(2013);
        dto.setPages(215);
        dto.setBarcode("9789949907861");
        return dto;
    }

    public static Set<String> bothLanguages() {
        return Set.of(ESTONIAN, ENGLISH);
    }

    public static BookDto oneHundredFiftyHouses() {
        BookDto dto = new BookDto(HOUSES_YOU_NEED_TO_VISIT_BEFORE_YOU_DIE, ISBN_HOUSES_YOU_NEED_TO_VISIT, HAVE);
        dto.setLanguages(Set.of("English"));
        dto.setAuthors(Set.of("Thijs Demeulemeester", "Jacinthe Gigou"));
        dto.setKeywords(Set.of("Architecture", "World Architecture", "20th Century Architecture"));
        dto.setCover(HARDCOVER);
        dto.setPublisher(LANNOO);
        dto.setPublishYear(2021);
        dto.setPages(253);
        dto.setBarcode(BARCODE_HOUSES_YOU_NEED_TO_VISIT);
        return dto;
    }

    public static Set<String> allBookNames() {
        return Set.of(LANDSCAPES_OF_IDENTITY, CONFLICTS_AND_ADAPTATIONS,
                HOUSES_YOU_NEED_TO_VISIT_BEFORE_YOU_DIE, _20TH_CENTURY_ESTONIAN_ARCHITECTURE);
    }

    public static Set<String> estonianBookNames() {
        return Set.of(LANDSCAPES_OF_IDENTITY, CONFLICTS_AND_ADAPTATIONS,
                _20TH_CENTURY_ESTONIAN_ARCHITECTURE);
    }

    public static Set<String> estonianArtBookNames() {
        return Set.of(CONFLICTS_AND_ADAPTATIONS, LANDSCAPES_OF_IDENTITY);
    }

    public static Set<String> housesYouNeedToVisit() {
        return Set.of(HOUSES_YOU_NEED_TO_VISIT_BEFORE_YOU_DIE);
    }
}
