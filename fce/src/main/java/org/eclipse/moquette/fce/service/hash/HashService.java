package org.eclipse.moquette.fce.service.hash;

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
public class HashService {

	private final static Logger log = Logger.getLogger(HashService.class.getName());

	public String generateHash(String username) {
		return DigestUtils.sha256Hex(username);
	}

	public boolean validateUsernameHash(AuthenticationProperties props) {
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