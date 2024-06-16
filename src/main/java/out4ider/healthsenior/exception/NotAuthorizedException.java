package out4ider.healthsenior.exception;

import org.springframework.http.HttpStatusCode;

public class NotAuthorizedException extends CustomException{
    public NotAuthorizedException(int code, String message, HttpStatusCode statusCode) {
        super(message);
        super.code = code;
        super.message = message;
        super.statusCode = statusCode;
    }
}
