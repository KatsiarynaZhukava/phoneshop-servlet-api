package com.es.phoneshop.exception;

import java.text.MessageFormat;

public class NotFoundException extends RuntimeException {
    public NotFoundException( final String message, final Object... params ) {
        super( MessageFormat.format( message, params ) );
    }
}
