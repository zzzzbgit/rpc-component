package com.leyi.base.exception;

public class ConnectionRefusedException extends RuntimeException{

    public ConnectionRefusedException(String s) {
        super(s);
    }
}
