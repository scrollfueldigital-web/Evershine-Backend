package com.evershine.EvershineServer.projectExceptionHandler;

public class CartQuantityException extends RuntimeException {
    public CartQuantityException(String message) {
        super(message);
    }
}
