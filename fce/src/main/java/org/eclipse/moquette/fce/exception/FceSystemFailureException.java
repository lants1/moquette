package org.eclipse.moquette.fce.exception;

public class FceSystemFailureException extends RuntimeException{

	private static final long serialVersionUID = 1730708539283481755L;

	public FceSystemFailureException(String msg)
    {
        super(msg);
    }
	
	public FceSystemFailureException(Exception e)
    {
        super(e);
    }
}
