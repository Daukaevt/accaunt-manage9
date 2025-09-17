package com.wixsite.mupbam1.exceptions;

public class TooManyEmailRequestsException extends RuntimeException {
    public TooManyEmailRequestsException(String message) {
        super(message);
    }
}
