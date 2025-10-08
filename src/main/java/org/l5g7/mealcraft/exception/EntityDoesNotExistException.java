package org.l5g7.mealcraft.exception;

public class EntityDoesNotExistException extends RuntimeException {
    public EntityDoesNotExistException(String entityType, String data) {
        super(entityType + " with email/id = " + data + " not found");
    }
}
