package com.cmanager.app.core.exception;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String entity, String username) {
        super(entity + " already exists: " + username);
    }
}
