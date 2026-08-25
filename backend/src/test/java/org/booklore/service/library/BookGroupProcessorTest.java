package org.booklore.service.library;

import org.booklore.model.FileProcessResult;
import org.booklore.model.dto.Book;
import org.booklore.model.dto.settings.LibraryFile;
import org.booklore.model.entity.LibraryEntity;
import org.booklore.model.enums.BookFileType;
import org.booklore.repository.BookAdditionalFileRepository;
import org.booklore.repository.BookRepository;
import org.booklore.repository.LibraryRepository;
import org.booklore.service.event.BookAddedEvent;
import org.booklore.service.event.BookImportedEvent;
import org.booklore.service.fileprocessor.BookFileProcessor;
import org.booklore.service.fileprocessor.BookFileProcessorRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookGroupProcessorTest {

    @Mock
    private LibraryRepository libraryRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookAdditionalFileRepository bookAdditionalFileRepository;
    @Mock
    private BookFileProcessorRegistry processorRegistry;
    @Mock
    private BookFileProcessor processor;
    @Mock
    private BookCoverGenerator bookCoverGenerator;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void process_publishesBookAddedAndBookImportedAfterSuccessfulProcessing() {
        long libraryId = 7L;
        LibraryEntity library = new LibraryEntity();
        library.setId(libraryId);
        LibraryFile file = LibraryFile.builder()
                .fileName("book.epub")
                .bookFileType(BookFileType.EPUB)
                .build();
        Book book = Book.builder().id(42L).build();

        when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
        when(processorRegistry.getProcessorOrThrow(BookFileType.EPUB)).thenReturn(processor);
        when(processor.processFile(file)).thenReturn(FileProcessResult.builder().book(book).build());

        BookGroupProcessor bookGroupProcessor = new BookGroupProcessor(
                libraryRepository,
                bookRepository,
                bookAdditionalFileRepository,
                processorRegistry,
                bookCoverGenerator,
                eventPublisher
        );

        bookGroupProcessor.process(List.of(file), libraryId);

        ArgumentCaptor<Object> events = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, org.mockito.Mockito.times(2)).publishEvent(events.capture());
        assertThat(events.getAllValues()).containsExactly(
                new BookAddedEvent(book),
                new BookImportedEvent(book)
        );
    }
}
