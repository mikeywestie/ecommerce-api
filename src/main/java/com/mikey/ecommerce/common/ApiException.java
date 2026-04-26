package com.mikey.ecommerce.common;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
