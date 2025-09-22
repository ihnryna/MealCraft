package org.l5g7.mealcraft.exception;

public class EntityDoesNotExistException extends RuntimeException {
    public EntityDoesNotExistException(String entityType, Long invalidId) {
        super(entityType + " with id " + invalidId + " not found");
    }
}
