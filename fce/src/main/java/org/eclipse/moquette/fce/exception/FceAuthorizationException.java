package org.eclipse.moquette.fce.exception;

/**
 * Exception stands for a unexpected behavior in the validation which rejects
 * the authorization but doesn't need to stop the server.
 * 
 * @author lants1
 *
 */
public class FceAuthorizationException extends Exception {

	private static final long serialVersionUID = 6018496403796990742L;

	public FceAuthorizationException(String msg) {
		super(msg);
	}
}
