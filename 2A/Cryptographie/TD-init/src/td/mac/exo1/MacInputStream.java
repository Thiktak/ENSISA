package td.mac.exo1;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;

public class MacInputStream extends FilterInputStream {

    Mac mac;

    public MacInputStream(InputStream in, Mac mac) throws NoSuchAlgorithmException, InvalidKeyException {
        this.in = in;
        this.mac = mac;
        // SecretKeySpec ks = new SecretKeySpec(km, "HmacSHA256");
        // Initialiser le Mac
        // mac.init(ks);
    }

    public void mark() {
    }

    @Override
    public void reset() {
    }
    
    public static void main(String args[]) throws Exception {
        MacInputStream mis = new MacInputStream(System.in, Mac.getInstance("HmacSHA256"));
    }
}
