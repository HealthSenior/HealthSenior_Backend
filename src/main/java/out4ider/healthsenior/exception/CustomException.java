package out4ider.healthsenior.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class CustomException extends RuntimeException{
    int code;
    String message;
    HttpStatusCode statusCode;
    public CustomException(String message) {
        super(message);
    }
}
