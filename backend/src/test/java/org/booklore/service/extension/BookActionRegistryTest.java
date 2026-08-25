package org.booklore.service.extension;

import org.booklore.exception.APIException;
import org.booklore.extension.api.BookAction;
import org.booklore.extension.api.BookActionProvider;
import org.booklore.extension.api.BookActionResult;
import org.booklore.mapper.BookMapper;
import org.booklore.model.dto.Book;
import org.booklore.model.entity.BookEntity;
import org.booklore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class BookActionRegistryTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookActionProvider provider;
    @Mock
    private BookAction action;

    private final BookEntity bookEntity = new BookEntity();
    private final Book book = Book.builder().id(42L).build();
    private BookActionRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new BookActionRegistry(bookRepository, bookMapper, List.of(provider));
        lenient().when(bookRepository.findByIdWithBookFiles(42L)).thenReturn(Optional.of(bookEntity));
        lenient().when(bookMapper.toBook(bookEntity)).thenReturn(book);
        lenient().doReturn(List.of(action)).when(provider).getActions(book);
        lenient().when(action.getId()).thenReturn("demo.action");
    }

    @Test
    void getAvailableActions_collectsProviderActions() {
        assertThat(registry.getAvailableActions(42L)).containsExactly(action);
        verify(provider).getActions(book);
    }

    @Test
    void execute_routesToMatchingAction() {
        BookActionResult expected = new BookActionResult(true, "done");
        when(action.execute(book)).thenReturn(expected);

        assertThat(registry.execute(42L, "demo.action")).isSameAs(expected);
        verify(action).execute(book);
    }

    @Test
    void execute_rejectsUnknownAction() {
        assertThatThrownBy(() -> registry.execute(42L, "missing.action"))
                .isInstanceOf(APIException.class)
                .hasMessage("Book action not available: missing.action");
    }

    @Test
    void execute_rejectsMissingBook() {
        when(bookRepository.findByIdWithBookFiles(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registry.execute(99L, "demo.action"))
                .isInstanceOf(APIException.class)
                .hasMessage("Book not found with ID: 99");
    }
}
