package com.luv2code.book.controller;

import com.luv2code.book.entity.Book;
import com.luv2code.book.exception.BookErrorResponse;
import com.luv2code.book.exception.BookNotFoundException;
import com.luv2code.book.request.BookRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name="Book REST API Endpoints", description = "Operations related to books")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final List<Book> books = new ArrayList<>();

    public BookController() {
        initializeBooks();
    }

    // Initializing Books for test
    private void initializeBooks() {
        books.addAll(
                List.of(
                        new Book(1, "Computer Science Pro", "Chad Darby", "Computer Science", 5),
                        new Book(2, "Java Spring Master", "Eric Roby", "Computer Science", 5),
                        new Book(3, "Why 1+1 Rocks2", "Adil A.", "Math", 5),
                        new Book(4, "How Bears Hibernate", "Bob B.", "Science", 2),
                        new Book(5, "A pirate's Treasure", "Curt C.", "History", 3),
                        new Book(6, "Why 2+2 is Better", "Dan D.", "Math", 1)
                )
        );
    }

    // ========================================== GET ALL BOOKS ==========================================

    @Operation(summary = "Get all books", description = "Retrieve a list of all available books")
    @ResponseStatus(HttpStatus.OK) // Response 200
    @GetMapping
    public List<Book> getBooks(@Parameter(description = "Optional query parameter")
                                   @RequestParam(required = false) String category) {

        // If no have category param
        if (category == null) {
            return books;
        }

        // If have category param
        return books
                .stream()
                .filter(book -> book.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    // ========================================== GET BY ID ==========================================

    @Operation(summary = "Get a book by id", description = "Retrieve a specific book by id")
    @ResponseStatus(HttpStatus.OK) // Response 200
    @GetMapping("/{id}")
    public Book getBookById(@Parameter(description = "Id of book to be retrieved")
                                @PathVariable @Min(value=1) long id) {
        return books
                .stream()
                .filter(book -> book.getId() == id)
                .findFirst()
                .orElseThrow(() ->
                        new BookNotFoundException("BOOK NOT FOUND - " + id)
                );
    }

    // ========================================== CREATE ==========================================

    @Operation(summary = "Create a new book", description = "Add a new book to a list")
    @ResponseStatus(HttpStatus.CREATED) // Response 201
    @PostMapping
    public void createBook(@Valid @RequestBody BookRequest bookRequest) {
        long id = books.isEmpty() ? 1 : books.size() + 1;

        books.add(
                convertToBook(id, bookRequest)
        );
    }

    // ========================================== UPDATE ==========================================

    @Operation(summary = "Update a book", description = "Update the details of an existing book")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Response 204: Used for void
    @PutMapping("/{id}")
    public Book updateBook(@Parameter(description = "Id of the book to update") @PathVariable @Min(value = 1) long id,
                           @Valid @RequestBody BookRequest bookRequest) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == id) {
                Book updatedBook = convertToBook(id, bookRequest);
                books.set(i, updatedBook);
                return updatedBook;
            }
        }

        throw new BookNotFoundException("BOOK NOT FOUND - " + id);
    }

    // ========================================== DELETE ==========================================

    @Operation(summary = "Delete a book", description = "Retrieve a book from the list")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Response 204: Used for void
    @DeleteMapping("/{id}")
    public void deletedBook(@Parameter(description = "Id of the book to delete")
                                @PathVariable long id) {
        // Finding book
        books.stream()
            .filter(book -> book.getId() == id)
            .findFirst()
            .orElseThrow(() ->
                    new BookNotFoundException("BOOK NOT FOUND - " + id)
            );

        // Delete if founding was successes
        books.removeIf(book -> book.getId() == id);
    }

    // ========================================== Other methods ==========================================

    private Book convertToBook(long id, BookRequest bookRequest) {
        return new Book(
                id,
                bookRequest.getTitle(),
                bookRequest.getAuthor(),
                bookRequest.getCategory(),
                bookRequest.getRating()
        );
    }


}
