package org.emoseman.cmsc451.project1;

/**
 * Evan Moseman
 * CMSC-451
 * Professor Jiang
 * November 11, 2025
 *
 * An exception to indicate that the sorting algorithm failed to sort the data.
 */
public class UnsortedException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnsortedException() {
        super();
    }

    public UnsortedException(String message) {
        super(message);
    }

    public UnsortedException(Throwable cause) {
        super(cause);
    }

    public UnsortedException(String message, Throwable cause) {
        super(message, cause);

    }

}
