package org.l5g7.mealcraft.exception;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String entityType, Long invalidId) {
        super(entityType + " with id " + invalidId + " already exists");
    }
}
