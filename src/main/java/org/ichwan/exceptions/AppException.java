package org.ichwan.exceptions;

public class AppException extends RuntimeException {
    private final int statusCode;
    private final String error;

    public AppException(String message, int statusCode, String error) {
        super(message);
        this.statusCode = statusCode;
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

}

