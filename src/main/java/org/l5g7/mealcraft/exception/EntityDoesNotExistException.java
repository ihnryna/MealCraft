package org.l5g7.mealcraft.exception;

import lombok.Getter;

@Getter
public class EntityDoesNotExistException extends RuntimeException {

    private final String entityType;
    private final String fieldName;
    private final String fieldValue;

    public EntityDoesNotExistException(String entityType, String fieldValue) {
        this(entityType, "id", fieldValue);
    }

    public EntityDoesNotExistException(String entityType, String fieldName, String fieldValue) {
        super(entityType + " not found");
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
