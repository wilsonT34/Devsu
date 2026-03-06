package com.banco.clientepersona.exception;
/**
 *
 * @author user
 */
public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(String message) {
        super(message);
    }
}
