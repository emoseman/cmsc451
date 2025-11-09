package org.emoseman.cmsc451.project1.exp;

/**
 * Evan Moseman
 * CMSC-451
 * Project 1
 * November 11, 2025
 *
 * Runtime exception used when a sorting algorithm fails to produce ordered data.
 */
public class UnsortedException
    extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Create an exception without details.
     */
    public UnsortedException() {
        super();
    }

    /**
     * Create an exception with the supplied message.
     *
     * @param message detail message describing the failure
     */
    public UnsortedException(String message) {
        super(message);
    }

    /**
     * Create an exception chained to another cause.
     *
     * @param cause root cause of the sorting failure
     */
    public UnsortedException(Throwable cause) {
        super(cause);
    }

    /**
     * Create an exception with both a message and underlying cause.
     *
     * @param message detail message describing the failure
     * @param cause   root cause of the sorting failure
     */
    public UnsortedException(String message, Throwable cause) {
        super(message, cause);
    }
}
