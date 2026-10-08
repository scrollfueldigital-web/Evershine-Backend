package com.evershine.EvershineServer.productApi.ProductApiExceptionHandler;

public class DuplicateFilterException extends RuntimeException {
    public DuplicateFilterException(String message) {
        super(message);
    }
}
