package com.ordermeow.api;

import com.ordermeow.api.product.ProductExceptions;
import com.ordermeow.api.user.UserExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AuthorizationServiceException.class})
    public void unauthorizedErrorCode(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler({ProductExceptions.BadProductName.class, ProductExceptions.BadProductDescription.class, ProductExceptions.BadProductPrice.class, ProductExceptions.InvalidFileException.class, UserExceptions.UserAlreadyExistsException.class})
    public void badRequestErrorCode(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({ProductExceptions.ProductNotFound.class})
    public void notFoundErrorCode(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({IOException.class})
    public void serverExceptionErrorCode(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
