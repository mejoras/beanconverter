package com.indra.isl.malaga;

/**
 * Clase excepción para los conversores.
 *
 * @author mcastillog
 *
 */
public class BeanConverterException extends Exception {

    /** Number serial UID. */
    private static final long serialVersionUID = 1L;

    /** Constructor por defecto. */
    public BeanConverterException() {

    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message
     *            mensaje
     * @param cause
     *            causa
     */
    public BeanConverterException(String message, Throwable cause) {
        super(message, cause);
    }

}
