package org.booklore.extension.api;

/**
 * Result returned by a generic book action.
 */
public record BookActionResult(boolean success, String message) {
}
