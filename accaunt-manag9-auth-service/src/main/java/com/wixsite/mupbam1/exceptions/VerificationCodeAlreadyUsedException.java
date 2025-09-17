package com.wixsite.mupbam1.exceptions;

public class VerificationCodeAlreadyUsedException extends RuntimeException {
    public VerificationCodeAlreadyUsedException(String message) {
        super(message);
    }
}
