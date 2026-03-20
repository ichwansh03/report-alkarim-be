package org.ichwan.exceptions;

public class NotFoundException extends AppException {

    public NotFoundException(String message) {
        super(message, 404, "Not Found");
    }
}
