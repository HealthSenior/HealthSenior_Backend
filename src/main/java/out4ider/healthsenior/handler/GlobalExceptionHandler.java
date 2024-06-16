package out4ider.healthsenior.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import out4ider.healthsenior.dto.ExceptionResponse;
import out4ider.healthsenior.exception.NotFoundElementException;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundElementException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundElementException(NotFoundElementException ex) {
        ExceptionResponse body = new ExceptionResponse(ex.getCode(), ex.getMessage());
        return this.handleExceptionInternal(body, ex.getStatusCode());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundElementException(IOException ex) {
        ExceptionResponse body = new ExceptionResponse(2, ex.getMessage());
        return this.handleExceptionInternal(body, HttpStatus.LOCKED);
    }

    private ResponseEntity<ExceptionResponse> handleExceptionInternal(ExceptionResponse body, HttpStatusCode statusCode) {
        return ResponseEntity.status(statusCode)
                .body(body);
    }
}
