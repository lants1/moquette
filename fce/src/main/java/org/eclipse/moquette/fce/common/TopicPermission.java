package org.eclipse.moquette.fce.common;

/**
 * The Broker itself can read/write everything. Client have restricted access on
 * broker internal zones. This Enum represents the permission for the restricted
 * client access.
 */
public enum TopicPermission {
	READ(true, false), WRITE(false, true), READ_WRITE(true, true);
	
	private boolean readable;
	private boolean writeable;
	
	private TopicPermission(boolean readable, boolean writeable) {
		this.readable = readable;
		this.writeable = writeable;
	}
	
	public boolean isReadable(){
		return readable;
	}
	
	public boolean isWriteable(){
		return writeable;
	}
}
