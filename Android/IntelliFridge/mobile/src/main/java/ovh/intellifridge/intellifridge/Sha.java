package ovh.intellifridge.intellifridge;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.auth0.jwt.internal.org.bouncycastle.pqc.math.linearalgebra.ByteUtils.toHexString;

/**
 * Created by franc on 14-12-16.
 */

public class Sha {

    public static String computeSha1OfString(final String message)
            throws UnsupportedOperationException, NullPointerException {
        try {
            return computeSha1OfByteArray(message.getBytes(("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    private static String computeSha1OfByteArray(final byte[] message)
            throws UnsupportedOperationException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(message);
            byte[] res = md.digest();
            return toHexString(res);
        } catch (NoSuchAlgorithmException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }
}
