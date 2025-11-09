package org.emoseman.cmsc451.project1.model;

/**
 * Evan Moseman
 * CMSC-451
 * Project 1
 * November 11, 2025
 *
 * Immutable data holder that stores the runtime and operation count for a single benchmark execution.
 */
public record RunCounter(
    long runDuration, int operationCount) {
}
