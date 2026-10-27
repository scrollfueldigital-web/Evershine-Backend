package com.evershine.EvershineServer.projectExceptionHandler;

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
