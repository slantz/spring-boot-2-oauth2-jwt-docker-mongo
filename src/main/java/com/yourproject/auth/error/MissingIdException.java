package com.yourproject.auth.error;

public class MissingIdException extends RuntimeException {

    private String s;

    /**
     * Constructs an instance of this class with the unmatched format specifier.
     */
    public MissingIdException(String s) {
        if (s == null)
            throw new NullPointerException();
        this.s = s;
    }

    public String getMessage() {
        return "DB object is missing id ['" + s + "']";
    }
}
