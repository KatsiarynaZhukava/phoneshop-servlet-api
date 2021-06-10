package com.es.phoneshop.exception;

import java.text.MessageFormat;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException( final String message, final Object... params ) {
        super( MessageFormat.format( message, params ) );
    }
}
