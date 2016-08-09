package com.fcs.common.exceptions;

/**
 * 用于封装UnCheckException
 */
public class InvokeNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -684314835123125276L;

	
    public InvokeNotFoundException(String message) {
    	super(message);
    }

    public InvokeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvokeNotFoundException(Throwable cause) {
        super(cause);
    }
}
