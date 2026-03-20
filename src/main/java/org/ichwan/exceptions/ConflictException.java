package org.ichwan.exceptions;

public class ConflictException extends AppException{
    public ConflictException(String message) {
        super(message, 409, "Conflict");
    }
}
