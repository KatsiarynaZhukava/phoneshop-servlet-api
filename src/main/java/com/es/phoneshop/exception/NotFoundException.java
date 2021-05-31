package com.es.phoneshop.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class NotFoundException extends RuntimeException {
    public NotFoundException( final String message, final Object... params ) {
        super( MessageFormat.format( message, params ) );
    }

    public static Supplier<NotFoundException> supplier( final String message, final Object ... params ) {
        return () -> new NotFoundException(message, params);
    }
}
