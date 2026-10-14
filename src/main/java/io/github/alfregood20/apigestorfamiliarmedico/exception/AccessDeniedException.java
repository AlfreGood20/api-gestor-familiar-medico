package io.github.alfregood20.apigestorfamiliarmedico.exception;

public class AccessDeniedException extends RuntimeException{

    public AccessDeniedException(String message) {
        super(message);
    }
}
