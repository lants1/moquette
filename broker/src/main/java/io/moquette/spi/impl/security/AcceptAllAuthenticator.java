package io.moquette.spi.impl.security;

/**
 * Created by andrea on 8/23/14.
 */
public class AcceptAllAuthenticator implements IAuthenticator {
    public boolean checkValid(String username, byte[] password, String clientId) {
        return true;
    }
}
