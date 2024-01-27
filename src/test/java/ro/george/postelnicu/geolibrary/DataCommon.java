package ro.george.postelnicu.geolibrary;

import ro.george.postelnicu.geolibrary.dto.BookDto;

import java.util.Set;

import static ro.george.postelnicu.geolibrary.model.CoverType.HARDCOVER;
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

    public static BookDto landscapesOfIdentity() {
        BookDto dto = new BookDto(LANDSCAPES_OF_IDENTITY, "ISBN 978-9949-687-32-9", HAVE);
        dto.setFullTitle("Landscapes of Identity: Estonian Art 1700-1945 The 3rd-floor permanent exhibition of the Kumu Art Museum");
        dto.setDescription("Lorem Ipsum");
        dto.setAuthors(Set.of("Linda Kalijundi", "Kadi Polli", "Bart Pushaw", "Kaja Kahrik"));
        dto.setKeywords(Set.of("Kumu Art Museum", "Art", "Estonian Art"));
        dto.setLanguages(Set.of("English"));
        dto.setPublisher(ART_MUSEUM_OF_ESTONIA);
        dto.setCover(SOFTCOVER_WITH_DUST_JACKET);
        dto.setPublishYear(2021);
        dto.setPages(ESTONIAN_ART_BOOKS_PAGE_NR);
        dto.setBarcode("9789949687329");
        return dto;
    }

    public static BookDto anotherEnglishBook() {
        BookDto dto = new BookDto(_20TH_CENTURY_ESTONIAN_ARCHITECTURE, "ISBN 978-9949-9078-6-1", HAVE);
        dto.setLanguages(Set.of("English"));
        dto.setAuthors(Set.of("Linda Kalijundi", "Kadi Polli", "Bart Pushaw", "Kaja Kahrik"));
        dto.setKeywords(Set.of("Kumu Art Museum", "Art", "Estonian Art"));
        dto.setPublisher(ART_MUSEUM_OF_ESTONIA);
        return dto;
    }

    public static BookDto conflictsAndAdaptations() {
        BookDto dto = new BookDto(CONFLICTS_AND_ADAPTATIONS, "ISBN 978-9949-687-44-2", HAVE);
        dto.setFullTitle("Conflicts and Adaptations. Estonian Art of the Soviet Era (1940-1991)");
        dto.setDescription("Lorem Ipsum");
        dto.setAuthors(Set.of("Anu Allas", "Sirje Helme", "Liisa Kaljula"));
        dto.setKeywords(Set.of("Kumu Art Museum", "Art", "Estonian Art"));
        dto.setLanguages(Set.of("English"));
        dto.setCover(SOFTCOVER_WITH_DUST_JACKET);
        dto.setPublisher(ART_MUSEUM_OF_ESTONIA);
        dto.setPublishYear(CONFLICTS_PUBLISH_YEAR);
        dto.setPages(ESTONIAN_ART_BOOKS_PAGE_NR);
        dto.setBarcode("9789949687442");
        return dto;
    }


    public static BookDto oneHundredStepsThrough20thCenturyEstonianArchitecture() {
        BookDto dto = new BookDto(_20TH_CENTURY_ESTONIAN_ARCHITECTURE, "ISBN 978-9949-9078-6-1", HAVE);
        dto.setLanguages(Set.of("Estonian", "English"));
        /*dto.setDescription("""
                The 100 steps denote 100 keywords, which most characterise the period of 1870–1992.
                These may be building types, or so much as fields (railway architecture, the municipal building,
                 the rural schoolhouse, the private dwelling), specific structures
                 (the Riigikogu building, TV- and radio buildings, the Munamäe viewing platform, the Seaplane Hangars, etc.),
                 types of settlements (the micro-district, the rural settlement, the hamlet, the summer-home cooperative, etc.),
                 architects (Karl Burman, Edgar Velbri, Raine Karp, Toomas Rein, etc.), building materials (silica brick,
                 reinforced concrete, large block, etc.), or styles (historicism, functionalism, art-nouveau, Stalinism, etc.).
                 This allows all more significant architectural phenomena in 20th-century Estonian architecture to be covered:
                 both crowning achievements and everyday architecture. Both less-known buildings and those not protected as items
                 of cultural value so far are presented, as are structures that are already under state protection.
                """);*/
        dto.setAuthors(Set.of("Lilian Hansar", "Jaak Huimerind", "Karen Jagodin", "Liina Jänes", "Mart Kalm",
                "Epp Lankots", "Maris Mändel", "Triin Ojari", "Oliver Orro",
                "Heiki Pärdi", "Anneli Randla", "Leele Välja", "Mait Väljas"));
        dto.setKeywords(Set.of("20th Century Architecture", "Architecture", "Estonian Architecture"));
        dto.setCover(SOFTCOVER_WITH_DUST_JACKET);
        dto.setPublisher(ESTONIAN_MUSEUM_OF_ARCHITECTURE);
        dto.setPublishYear(2013);
        dto.setPages(215);
        dto.setBarcode("9789949907861");
        return dto;
    }

    public static BookDto oneHundredFiftyHouses() {
        BookDto dto = new BookDto(HOUSES_YOU_NEED_TO_VISIT_BEFORE_YOU_DIE, "ISBN 978-940-14620-4-4", HAVE);
        dto.setLanguages(Set.of("English"));
        /*dto.setDescription("""
                Architectural travel is on the rise. With this book you not only have a fantastic reference book of the best 150 private houses, but also a bucket list to plan your next city trip or trip. All houses guarantee a unique experience through the aesthetics of the house, the architectural masterpiece or the sophisticated design.
                                
                This book is the ultimate architecture travel wish list. At each house, the authors provide the reader with a clear identity kit regarding the year of construction, architect and all the information you want to know at a glance.
                                
                150 Houses You Need to Visit Before You Die is the ultimate 'architecture bucket list'.
                """);*/
        dto.setAuthors(Set.of("Thijs Demeulemeester", "Jacinthe Gigou"));
        dto.setKeywords(Set.of("Architecture", "World Architecture", "20th Century Architecture"));
        dto.setCover(HARDCOVER);
        dto.setPublisher(LANNOO);
        dto.setPublishYear(2021);
        dto.setPages(253);
        dto.setBarcode("9789401462044");
        return dto;
    }

    public static Set<String> allBookNames() {
        return Set.of(LANDSCAPES_OF_IDENTITY, CONFLICTS_AND_ADAPTATIONS,
                HOUSES_YOU_NEED_TO_VISIT_BEFORE_YOU_DIE, _20TH_CENTURY_ESTONIAN_ARCHITECTURE);
    }

    public static Set<String> estonianArtBooks() {
        return Set.of(CONFLICTS_AND_ADAPTATIONS, LANDSCAPES_OF_IDENTITY);
    }
}
