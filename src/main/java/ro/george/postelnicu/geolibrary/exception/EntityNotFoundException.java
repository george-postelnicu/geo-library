package ro.george.postelnicu.geolibrary.exception;

public class EntityNotFoundException extends RuntimeException {

    public static final String CANNOT_FIND_ENTITY_ID = "Cannot find [%s] with [%s]";

    public EntityNotFoundException(String entity, Long id) {
        super(String.format(CANNOT_FIND_ENTITY_ID, entity, id));
    }
}
