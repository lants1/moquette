package org.eclipse.moquette.fce.common;

import org.eclipse.moquette.fce.model.ManagedInformation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public final class FceHashUtil {

	/**
	 * From a password, a number of iterations and a salt, returns the
	 * corresponding digest
	 * 
	 * @param iterationNb
	 *            int The number of iterations of the algorithm
	 * @param password
	 *            String The password to encrypt
	 * @param salt
	 *            byte[] The salt
	 * @return byte[] The digested password
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm doesn't exist
	 * @throws UnsupportedEncodingException
	 */
	public static String getHash(ManagedInformation managedInfo)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String password = managedInfo.getUserName()+managedInfo.getUserIdentifier();
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(loadPepperFromServer());
		byte[] input = digest.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < 1000; i++) {
			digest.reset();
			input = digest.digest(input);
		}
		return byteToBase64(input);
	}

	private static byte[] loadPepperFromServer(){
		// TODO Lan secure implementation
		return "blabla".getBytes();
	}
	
	/**
	 * From a base 64 representation, returns the corresponding byte[]
	 * 
	 * @param data
	 *            String The base64 representation
	 * @return byte[]
	 * @throws IOException
	 */
	private static byte[] base64ToByte(String data) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(data);
	}

	/**
	 * From a byte[] returns a base 64 representation
	 * 
	 * @param data
	 *            byte[]
	 * @return String
	 * @throws IOException
	 */
	private static String byteToBase64(byte[] data) {
		BASE64Encoder endecoder = new BASE64Encoder();
		return endecoder.encode(data);
	}
}