package org.booklore.extension.builtin;

import lombok.extern.slf4j.Slf4j;
import org.booklore.extension.api.BookAction;
import org.booklore.extension.api.BookActionProvider;
import org.booklore.extension.api.BookActionResult;
import org.booklore.extension.api.BookImportedEventListener;
import org.booklore.model.dto.Book;
import org.booklore.service.event.BookImportedEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Minimal built-in extension used to prove the internal extension API wiring.
 * Format conversion is intentionally not performed by this PoC.
 */
@Component
@Slf4j
public class FormatConverterExtension implements BookImportedEventListener, BookActionProvider {

    public static final String ACTION_ID = "format-converter.generate-missing-formats";
    public static final String ACTION_LABEL = "Generate missing formats";

    private final BookAction generateMissingFormatsAction = new BookAction() {
        @Override
        public String getId() {
            return ACTION_ID;
        }

        @Override
        public String getLabel() {
            return ACTION_LABEL;
        }

        @Override
        public BookActionResult execute(Book book) {
            long bookId = book != null && book.getId() != null ? book.getId() : -1L;
            log.info("FormatConverter demo action requested for book ID {}", bookId);
            return new BookActionResult(true,
                    "Format converter demo completed for book " + bookId + "; no files were changed.");
        }
    };

    @Override
    public void handle(BookImportedEvent event) {
        if (event == null || event.book() == null) {
            log.warn("FormatConverter received an empty BookImported event");
            return;
        }
        log.info("FormatConverter received imported book ID {}", event.book().getId());
    }

    @Override
    public Collection<? extends BookAction> getActions(Book book) {
        return List.of(generateMissingFormatsAction);
    }
}
