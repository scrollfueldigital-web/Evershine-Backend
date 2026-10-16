package com.evershine.EvershineServer.projectExceptionHandler;

public class FilterOptionNotFoundException extends RuntimeException {
    public FilterOptionNotFoundException(String message) {
        super(message);
    }
}
