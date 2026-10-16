package com.evershine.EvershineServer.projectExceptionHandler;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
