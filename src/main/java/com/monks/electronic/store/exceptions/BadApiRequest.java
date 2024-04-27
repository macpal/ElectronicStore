package com.monks.electronic.store.exceptions;

public class BadApiRequest extends RuntimeException{
    public BadApiRequest() {
        super("Bad Request");
    }

    public BadApiRequest(String msg) {
        super(msg);
    }
}
