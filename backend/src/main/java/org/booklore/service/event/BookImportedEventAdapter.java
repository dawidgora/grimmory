package org.booklore.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.booklore.extension.api.BookImportedEventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * Dispatches imported-book events to all Spring-managed extension listeners.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookImportedEventAdapter {

    private final List<BookImportedEventListener> listeners;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handle(BookImportedEvent event) {
        for (BookImportedEventListener listener : listeners) {
            try {
                listener.handle(event);
            } catch (Exception e) {
                log.error("Failed to dispatch BookImported event to {}: {}",
                        listener.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }
}
