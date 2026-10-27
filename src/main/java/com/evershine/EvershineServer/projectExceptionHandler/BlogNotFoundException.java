package com.evershine.EvershineServer.projectExceptionHandler;

public class BlogNotFoundException extends ResourceNotFoundException {
    public BlogNotFoundException(String message) {
        super(message);
    }
}
