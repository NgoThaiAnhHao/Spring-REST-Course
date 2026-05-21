package com.luv2code.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BookExceptionHandler {
    // Response for not found - 404
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<BookErrorResponse> handleNotFoundException(BookNotFoundException e) {
        BookErrorResponse bookErrorResponse = new BookErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(bookErrorResponse, HttpStatus.NOT_FOUND);
    }

    // Response for bad request - 400
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BookErrorResponse> handleInvalidBookException(Exception e) {
        BookErrorResponse bookErrorResponse = new BookErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(bookErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
