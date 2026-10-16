package com.evershine.EvershineServer.projectExceptionHandler;

public class DuplicateFilterException extends RuntimeException {
    public DuplicateFilterException(String message) {
        super(message);
    }
}
