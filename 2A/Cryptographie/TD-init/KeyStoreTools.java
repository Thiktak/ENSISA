package td3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

/**
 * Classe pr�sentant des m�thodes permettant la manipulation ais�e des keystores.
 * @author Julien Lepagnot
 */
public class KeyStoreTools {

    // Le keystore de l'instance
    private KeyStore ks;

    // Le mot de passe du keystore
    private char[] storepass;

    // Associations des OID pouvant appara�tre dans un nom distingu�
    // � compl�ter avec les OID de p�fixe 1.2.840.113549.1.9
    private static final Map<String, String> OID_MAP = new HashMap<>();
    static {
        OID_MAP.put("1.2.840.113549.1.9.1", "emailAddress");
        OID_MAP.put("1.2.840.113549.1.9.2", "unstructuredName");
        OID_MAP.put("1.2.840.113549.1.9.8", "unstructuredAddress");
        OID_MAP.put("1.2.840.113549.1.9.16", "S/MIME Object Identifier Registry");
    }

// TODO...

    /**
     * D�monstration de la classe.
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Nouvelle instance de la classe initialis�e avec le fichier store.ks
            KeyStoreTools kst = new KeyStoreTools("JCEKS", "store.ks", "azerty".toCharArray());

            // Liste les certificats et cl�s priv�es du keystore
            System.out.println(kst.listCertificates());
            System.out.println(kst.listPrivateKeys("td3exo1".toCharArray()));

            // Obtient une instance d'un g�n�rateur de cl�s secr�tes pour l'AES
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            // Sp�cifie la longueur de la cl� (128 bits)
            kg.init(128);
            // G�n�re la cl�
            SecretKey key = kg.generateKey();
            // Ins�re la cl� dans le keystore en lui associant l'alias key6
            kst.importSecretKey(key, "key6", null);

            // Ins�re le certificat msca.cer en lui associant l'alias key7
            kst.importCertificates(new File("msca.cer"), new String[]{"key7"});

            // Sauvegarde le keystore dans le fichier kstore.ks avec un nouveau mot de passe
            kst.save(new File("kstore.ks"), "x75DT7Rdx98tdZK".toCharArray());

            // Nouvelle instance de la classe initialis�e avec le fichier kstore.ks
            kst = new KeyStoreTools("JCEKS", "kstore.ks", "x75DT7Rdx98tdZK".toCharArray());

            // Liste les certificats et cl�s priv�es du nouveau keystore
            System.out.println(kst.listCertificates());
            System.out.println(kst.listPrivateKeys("td3exo1".toCharArray()));
        } catch (GeneralSecurityException | IOException ex) {
            Logger.getLogger(KeyStoreTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
