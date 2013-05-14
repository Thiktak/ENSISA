package exoCh3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author Thiktak
 */
public class main {

    public static void main(String[] args) {
        try {
            KeyStoreTools kst;
            kst = new KeyStoreTools("JCEKS", "store.ks", "azerty".toCharArray());

            System.out.println(kst.listCertificates());
            System.out.println(kst.listPrivateKeys("td3exo1".toCharArray()));

            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128);

            SecretKey key = kg.generateKey();

            kst.importSecretKey(key, "key6", null);

            kst.importCertificates(new File("msca.cer"), new String[]{"key7"});

            kst.save(new File("kstore.ks"), "x75DT7Rdx8tdZK".toCharArray());

            kst = new KeyStoreTools("JCEKS", "ksca.ks", "x75DT7Rdx8tdZK".toCharArray());

            System.out.println(kst.listCertificates());
            System.out.println(kst.listPrivateKeys("td3exo1".toCharArray()));

        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
