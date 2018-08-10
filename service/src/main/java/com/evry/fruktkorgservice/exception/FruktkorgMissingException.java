package com.evry.fruktkorgservice.exception;

public class FruktkorgMissingException extends Exception {
    private long fruktkorgId;

    public FruktkorgMissingException(String message, long fruktkorgId) {
        super(message);
        this.fruktkorgId = fruktkorgId;
    }

    public long getFruktkorgId() {
        return fruktkorgId;
    }

    public void setFruktkorgId(long fruktkorgId) {
        this.fruktkorgId = fruktkorgId;
    }
}
