package org.booklore.extension.api;

import org.booklore.service.event.BookImportedEvent;

/**
 * Capability for an extension to react to an imported book.
 */
@FunctionalInterface
public interface BookImportedEventListener {

    void handle(BookImportedEvent event);
}
