package org.l5g7.mealcraft.exception;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends RuntimeException {

    private final String entityType;
    private final String fieldName;
    private final String fieldValue;

    public EntityAlreadyExistsException(String entityType, String fieldValue) {
        this(entityType, "id", fieldValue);
    }

    public EntityAlreadyExistsException(String entityType, String fieldName, String fieldValue) {
        super(entityType + " already exists");
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
