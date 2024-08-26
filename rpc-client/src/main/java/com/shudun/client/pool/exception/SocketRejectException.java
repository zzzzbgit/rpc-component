package com.shudun.client.pool.exception;

public class SocketRejectException extends RuntimeException{

    public SocketRejectException() {
    }

    public SocketRejectException(String message) {
        super(message);
    }

    public SocketRejectException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketRejectException(Throwable cause) {
        super(cause);
    }
}
