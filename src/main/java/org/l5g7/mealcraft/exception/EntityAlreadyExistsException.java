package org.l5g7.mealcraft.exception;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String entityType, String data) {
        super(entityType + " with email/id = " + data + " already exists");
    }
}
