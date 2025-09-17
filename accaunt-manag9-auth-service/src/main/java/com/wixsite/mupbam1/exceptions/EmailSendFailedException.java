package com.wixsite.mupbam1.exceptions;

public class EmailSendFailedException extends RuntimeException {
    public EmailSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
