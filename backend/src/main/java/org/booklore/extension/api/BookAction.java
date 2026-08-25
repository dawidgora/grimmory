package org.booklore.extension.api;

import org.booklore.model.dto.Book;

/**
 * A generic action that can be offered and executed for a book.
 */
public interface BookAction {

    String getId();

    String getLabel();

    BookActionResult execute(Book book);
}
