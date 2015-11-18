package org.eclipse.moquette.fce.exception;

public class FceNoAuthorizationPossibleException extends Exception{

	private static final long serialVersionUID = 6018496403796990742L;

	public FceNoAuthorizationPossibleException(String msg)
    {
        super(msg);
    }
}
