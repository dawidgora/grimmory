package org.booklore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.booklore.config.security.annotation.CheckBookAccess;
import org.booklore.extension.api.BookAction;
import org.booklore.extension.api.BookActionResult;
import org.booklore.model.dto.response.BookActionDto;
import org.booklore.model.dto.response.BookActionResultDto;
import org.booklore.service.extension.BookActionRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Book Actions", description = "Actions supplied by internal book extensions")
public class BookActionController {

    private final BookActionRegistry bookActionRegistry;

    @Operation(summary = "Get available actions for a book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available actions returned successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{bookId}/actions")
    @PreAuthorize("@securityUtil.canEditMetadata() or @securityUtil.isAdmin()")
    @CheckBookAccess(bookIdParam = "bookId")
    public ResponseEntity<List<BookActionDto>> getActions(@PathVariable long bookId) {
        List<BookActionDto> actions = bookActionRegistry.getAvailableActions(bookId).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(actions);
    }

    @Operation(summary = "Execute a book action")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Action executed successfully"),
            @ApiResponse(responseCode = "404", description = "Book or action not found")
    })
    @PostMapping("/{bookId}/actions/{actionId}")
    @PreAuthorize("@securityUtil.canEditMetadata() or @securityUtil.isAdmin()")
    @CheckBookAccess(bookIdParam = "bookId")
    public ResponseEntity<BookActionResultDto> executeAction(
            @PathVariable long bookId,
            @PathVariable String actionId) {
        BookActionResult result = bookActionRegistry.execute(bookId, actionId);
        return ResponseEntity.ok(new BookActionResultDto(result.success(), result.message()));
    }

    private BookActionDto toDto(BookAction action) {
        return new BookActionDto(action.getId(), action.getLabel());
    }
}
