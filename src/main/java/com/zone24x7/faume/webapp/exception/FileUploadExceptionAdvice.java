package com.zone24x7.faume.webapp.exception;

import com.zone24x7.faume.webapp.message.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Controller Advice for file upload exception
 */

@ControllerAdvice
public class FileUploadExceptionAdvice {

    /**
     * Exception handler for too large file
     *
     * @param exc exception occured
     * @return Response message
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseMessage> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new ResponseMessage("One or more files are too large!"));
    }
}
