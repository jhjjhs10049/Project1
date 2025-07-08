package org.zerock.b01.security.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class Custom404Handler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public RedirectView handle404(NoHandlerFoundException ex) {
        return new RedirectView("/main");
    }
}
