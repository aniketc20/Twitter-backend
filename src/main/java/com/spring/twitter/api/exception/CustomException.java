package com.spring.twitter.api.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Paly
 * @version 1.0
 * @date 04/05/22 9:34 PM
 * @company Redeminds
 */
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String message;
    private final HttpStatus httpStatus;

    public CustomException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
