package com.jianyue.lightning.exception;

/**
 * @author FLJ
 * @date 2022/12/19
 * @time 14:54
 * @Description lightning exception
 */
public class LightningException extends RuntimeException {

    public LightningException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public LightningException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        // only once init
        if (getCause() != null) {
            throw new LightningException("The cause for this exception already exists .");
        }
        return super.initCause(cause);
    }
}
