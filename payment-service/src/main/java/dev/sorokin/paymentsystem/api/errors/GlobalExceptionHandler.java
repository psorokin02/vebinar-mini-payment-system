package dev.sorokin.paymentsystem.api.errors;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // TODO: добавить обработку MethodArgumentNotValidException
    // TODO: добавить обработку ResponseStatusException


    // TODO: раскомментировать чтобы обрабатывать generic exceptions
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiError> handleGeneric(
//            Exception ex,
//            HttpServletRequest request
//    ) {
//        logger.error("Handling exception", ex);
//        ApiError body = new ApiError(
//                LocalDateTime.now(),
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(body);
//    }

}
