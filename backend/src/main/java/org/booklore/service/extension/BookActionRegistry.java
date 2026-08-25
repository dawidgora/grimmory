package org.booklore.service.extension;

import lombok.RequiredArgsConstructor;
import org.booklore.exception.ApiError;
import org.booklore.extension.api.BookAction;
import org.booklore.extension.api.BookActionProvider;
import org.booklore.extension.api.BookActionResult;
import org.booklore.mapper.BookMapper;
import org.booklore.model.dto.Book;
import org.booklore.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Collects book actions from Spring-managed extension providers and routes execution.
 */
@Service
@RequiredArgsConstructor
public class BookActionRegistry {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final List<BookActionProvider> actionProviders;

    @Transactional(readOnly = true)
    public List<BookAction> getAvailableActions(long bookId) {
        return actionsFor(loadBook(bookId));
    }

    @Transactional
    public BookActionResult execute(long bookId, String actionId) {
        if (actionId == null || actionId.isBlank()) {
            throw ApiError.GENERIC_BAD_REQUEST.createException("Action ID is required");
        }

        Book book = loadBook(bookId);
        BookAction action = actionsFor(book).stream()
                .filter(candidate -> actionId.equals(candidate.getId()))
                .findFirst()
                .orElseThrow(() -> ApiError.GENERIC_NOT_FOUND
                        .createException("Book action not available: " + actionId));

        BookActionResult result = action.execute(book);
        if (result == null) {
            throw ApiError.INTERNAL_SERVER_ERROR.createException("Book action returned no result: " + actionId);
        }
        return result;
    }

    private Book loadBook(long bookId) {
        return bookRepository.findByIdWithBookFiles(bookId)
                .map(bookMapper::toBook)
                .orElseThrow(() -> ApiError.BOOK_NOT_FOUND.createException(bookId));
    }

    private List<BookAction> actionsFor(Book book) {
        return actionProviders.stream()
                .filter(Objects::nonNull)
                .map(provider -> provider.getActions(book))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(action -> action.getId() != null && !action.getId().isBlank())
                .map(action -> (BookAction) action)
                .toList();
    }
}
