package ro.george.postelnicu.geolibrary.exception;

import java.util.Set;

public class EntityAlreadyExistException extends RuntimeException {

    public static final String ENTITY_ALREADY_HAS_A = "Entity [%s] already has [%s]";
    public static final String ENTITY_ALREADY_HAS_COLLECTION = "Entity [%s] already has %s";

    public EntityAlreadyExistException(String entityName, String duplicate) {
        super(String.format(ENTITY_ALREADY_HAS_A, entityName, duplicate));
    }
    public EntityAlreadyExistException(String entityName, Set<String> duplicates) {
        super(String.format(ENTITY_ALREADY_HAS_COLLECTION, entityName, duplicates));
    }
}
