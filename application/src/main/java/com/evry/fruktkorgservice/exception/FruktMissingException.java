package com.evry.fruktkorgservice.exception;

public class FruktMissingException extends Exception {
    private String fruktType;

    public FruktMissingException(String message, String fruktType) {
        super(message);
        this.fruktType = fruktType;
    }

    public String getFruktType() {
        return fruktType;
    }

    public void setFruktType(String fruktType) {
        this.fruktType = fruktType;
    }
}
