package ru.practicum.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exceptions.*;
import ru.practicum.exceptions.notFound.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        return new ErrorResponse(
            "Объект не найден", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEventNotFoundException(final EventNotFoundException e) {
        return new ErrorResponse(
            "Объект не найден", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCategoryNotFoundException(final CategoryNotFoundException e) {
        return new ErrorResponse(
            "Объект не найден", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleCategoryNameDuplicateException(final DuplicateException e) {
        return new ErrorResponse(
            "Объект не найден", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDeleteObjectInUseException(final DeleteObjectInUseException e) {
        return new ErrorResponse(
            "Объект не найден", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleStateException(final EventStateException e) {
        return new ErrorResponse(
            "Объект не найден", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserRequestOwnEventException(final UserRequestOwnEventException e) {
        return new ErrorResponse(
            "Объект не найден", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalEventStateException(final RequestStatusException e) {
        return new ErrorResponse(
            "Объект не найден", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDateException(final DateException e) {
        return new ErrorResponse(
            "date error", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleParticipantLimitException(final ParticipantLimitException e) {
        return new ErrorResponse(
            "date error", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleCompilationNotFoundException(final CompilationNotFoundException e) {
        return new ErrorResponse(
            "date error", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleRequestNotFoundException(final RequestNotFoundException e) {
        return new ErrorResponse(
            "date error", e.getMessage()
        );
    }
}
