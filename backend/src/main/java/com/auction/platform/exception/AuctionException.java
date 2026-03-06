package com.auction.platform.exception;

public abstract class AuctionException extends RuntimeException {
    public AuctionException(String message) {
        super(message);
    }
}