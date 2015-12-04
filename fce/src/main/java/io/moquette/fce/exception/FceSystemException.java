package io.moquette.fce.exception;

/**
 * FceSystemException = RuntimeException for every fatal Exception in the plugin
 * which is a reason to stop the server.
 * 
 * @author lants1
 *
 */
public class FceSystemException extends RuntimeException {

	private static final long serialVersionUID = 1730708539283481755L;

	public FceSystemException(String msg) {
		super(msg);
	}

	public FceSystemException(Exception e) {
		super(e);
	}
	
	public FceSystemException(String msg, Throwable e) {
		super(e);
	}
}
