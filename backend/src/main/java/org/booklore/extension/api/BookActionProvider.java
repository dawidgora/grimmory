package org.booklore.extension.api;

import org.booklore.model.dto.Book;

import java.util.Collection;

/**
 * Spring-discovered source of actions available for a book.
 */
public interface BookActionProvider {

    Collection<? extends BookAction> getActions(Book book);
}
