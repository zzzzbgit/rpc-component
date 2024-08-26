package com.shudun.base.exception;

public class ConnectionRefusedException extends RuntimeException{

    public ConnectionRefusedException(String s) {
        super(s);
    }
}
