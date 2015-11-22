package org.eclipse.moquette.fce.exception;

public class FceSystemException extends RuntimeException{

	private static final long serialVersionUID = 1730708539283481755L;

	public FceSystemException(String msg)
    {
        super(msg);
    }
	
	public FceSystemException(Exception e)
    {
        super(e);
    }
}
