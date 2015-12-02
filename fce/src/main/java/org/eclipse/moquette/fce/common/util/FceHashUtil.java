package org.eclipse.moquette.fce.common.util;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.moquette.plugin.AuthenticationProperties;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

/**
 * Utility class for hashing and validating hashes....
 * 
 * @author lants1
 *
 */
public final class FceHashUtil {

	private final static Logger log = Logger.getLogger(FceHashUtil.class.getName());

	public static String getFceHash(String username) {
		return DigestUtils.sha256Hex(username);
	}

	public static boolean validateUsernameHash(AuthenticationProperties props) {
		try {
			if (props.getUsername() == null || props.getPassword() == null) {
				return false;
			}

			boolean result = DigestUtils.sha256Hex(props.getUsername())
					.equals(new String(props.getPassword(), CharEncoding.UTF_8));
			log.info("username hash validated:" + result);
			return result;
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

}