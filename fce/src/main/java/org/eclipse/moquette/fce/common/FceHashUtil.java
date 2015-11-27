package org.eclipse.moquette.fce.common;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.fce.model.ManagedInformation;
import org.eclipse.moquette.plugin.AuthenticationProperties;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;


public final class FceHashUtil {

	private final static Logger log = Logger.getLogger(FceHashUtil.class.getName());
	
	public static String getFceHashWithPepper(ManagedInformation managedInfo) {
		return FceHashUtil.getFceHashWithPepper(managedInfo.getUserIdentifier(), "");
	}
	
	public static String getFceHashWithPepper(String username, byte[] pw) {
		try {
			return FceHashUtil.getFceHashWithPepper(username, new String(pw, CharEncoding.UTF_8));
		} catch (UnsupportedEncodingException e) {
			new FceSystemException(e);
		}
		return null;
	}
	
	public static String getFceHashWithPepper(String username, String pw) {
		return DigestUtils.sha256Hex(username+pw+loadPepperFromServer());
	}
	
	public static String getFceHash(String username, String pw) {
		return DigestUtils.sha256Hex(username+pw);
	}
	
	public static boolean validateClientIdHash(AuthenticationProperties props){
		try {
			boolean result = StringUtils.equals(DigestUtils.sha256Hex(props.getUsername()+new String(props.getPassword(), CharEncoding.UTF_8)),props.getClientId());
			log.fine("client id validated:" + result);
			return result;
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

	private static String loadPepperFromServer(){
		return "blabla";
	}
	
}