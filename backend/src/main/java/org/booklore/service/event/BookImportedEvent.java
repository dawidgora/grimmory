package org.booklore.service.event;

import org.booklore.model.dto.Book;

/**
 * Event published after a book has been imported by the library processor.
 *
 * @param book the imported book
 */
public record BookImportedEvent(Book book) {
}
