package com.evry.fruktkorgservice.exception;

public class FruktkorgMissingException extends Exception {
    private long fruktkorgId;

    public FruktkorgMissingException(String message) {
        super(message);
    }

    public long getFruktkorgId() {
        return fruktkorgId;
    }

    public void setFruktkorgId(long fruktkorgId) {
        this.fruktkorgId = fruktkorgId;
    }
}
