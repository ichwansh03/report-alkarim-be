package org.ichwan.exceptions;

public class BadRequestException extends AppException{
    public BadRequestException(String message) {
        super(message, 400, "Bad Request");
    }
}
