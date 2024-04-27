package com.monks.electronic.store.exceptions;

import lombok.Builder;

@Builder
public class ResourceNotFound extends RuntimeException{
    public ResourceNotFound() {
        super("Resource not found !!");
    }

    public ResourceNotFound(String message) {
        super(message);
    }
}
