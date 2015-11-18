package org.eclipse.moquette.fce.common;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.exception.FceSystemFailureException;
import org.eclipse.moquette.fce.model.ManagedInformation;
import org.eclipse.moquette.plugin.AuthenticationProperties;

import java.io.UnsupportedEncodingException;


public final class FceHashUtil {

	public static String getFceHash(ManagedInformation managedInfo) {
		return FceHashUtil.getFceHash(managedInfo.getUserIdentifier(), "");
	}
	
	public static String getFceHash(String username, byte[] pw) {
		try {
			return FceHashUtil.getFceHash(username, new String(pw, CharEncoding.UTF_8));
		} catch (UnsupportedEncodingException e) {
			new FceSystemFailureException(e);
		}
		return null;
	}
	
	public static String getFceHash(String username, String pw) {
		return DigestUtils.sha1Hex(username+pw+loadPepperFromServer());
	}
	
	public static boolean validateClientIdHash(AuthenticationProperties props){
		try {
			return StringUtils.equals(DigestUtils.sha1Hex(props.getUsername()+new String(props.getPassword(), CharEncoding.UTF_8)),props.getClientId());
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

	private static String loadPepperFromServer(){
		return "blabla";
	}
	
}