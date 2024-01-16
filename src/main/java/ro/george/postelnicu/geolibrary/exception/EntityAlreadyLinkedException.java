package ro.george.postelnicu.geolibrary.exception;

import java.util.Set;

public class EntityAlreadyLinkedException extends RuntimeException {

    public static final String ENTITY_ALREADY_HAS_A_LINK = "Entity [%s] already has a link to [%s]";
    public static final String ENTITY_ALREADY_HAS_LINKED_A_COLLECTION = "Entity [%s] already has links to %s";

    public EntityAlreadyLinkedException(String entityName, String duplicate) {
        super(String.format(ENTITY_ALREADY_HAS_A_LINK, entityName, duplicate));
    }
    public EntityAlreadyLinkedException(String entityName, Set<String> duplicates) {
        super(String.format(ENTITY_ALREADY_HAS_LINKED_A_COLLECTION, entityName, duplicates));
    }
}
