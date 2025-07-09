package org.example.calculator.exceptions;

public class InputException extends RuntimeException {

    public InputException(String errorMessage) {
        super(errorMessage);
    }
}
