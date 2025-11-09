package org.emoseman.cmsc451.project1.model;

/**
 * Evan Moseman
 * <p>
 * TODO
 *
 * @param averageCount
 * @param averageTime
 * @param countCoefficient
 * @param elementCount
 * @param timeCoefficient
 */
public record Statistics(
    double averageCount,
    double countCoefficient,
    double averageTime,
    double timeCoefficient) {
}
