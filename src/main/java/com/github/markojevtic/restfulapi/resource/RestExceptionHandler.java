package com.github.markojevtic.restfulapi.resource;

import com.github.markojevtic.restfulapi.resource.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class RestExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> validationException(final IllegalArgumentException exception) throws Exception {

        logger.error("Unexpected error occurred. Error message: {}", exception.getMessage(), exception);

        return ResponseEntity.status(BAD_REQUEST).body(ErrorDto.builder()
                .message(exception.getMessage()).build());
    }

}
