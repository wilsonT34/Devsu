
package com.banco.cuentamovimiento.exception;

/**
 *
 * @author user
 */

public class CuentaNotFoundException extends RuntimeException {
    public CuentaNotFoundException(String message) {
        super(message);
    }
}
