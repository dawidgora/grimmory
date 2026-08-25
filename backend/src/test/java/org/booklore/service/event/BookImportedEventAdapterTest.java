package org.booklore.service.event;

import org.booklore.extension.api.BookImportedEventListener;
import org.booklore.model.dto.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookImportedEventAdapterTest {

    @Mock
    private BookImportedEventListener firstListener;
    @Mock
    private BookImportedEventListener secondListener;

    @Test
    void handle_dispatchesEventOnceToEverySpringManagedListener() {
        BookImportedEvent event = new BookImportedEvent(Book.builder().id(42L).build());
        BookImportedEventAdapter adapter = new BookImportedEventAdapter(List.of(firstListener, secondListener));

        adapter.handle(event);

        verify(firstListener, times(1)).handle(event);
        verify(secondListener, times(1)).handle(event);
    }
}
