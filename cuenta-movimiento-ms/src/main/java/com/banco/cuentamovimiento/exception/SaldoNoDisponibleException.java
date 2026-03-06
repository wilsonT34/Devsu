package com.banco.cuentamovimiento.exception;

/**
 *
 * @author user
 */

public class SaldoNoDisponibleException extends RuntimeException {
    public SaldoNoDisponibleException(String message) {
        super(message);
    }
}