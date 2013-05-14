package exoCh3;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Certificate;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;

/**
 *
 * @author Thiktak
 */
public class KeyStoreTools {

    private KeyStore ks;
    private char[] storepass;
    private static Map<String, String> OID_MAP = new HashMap<>();

    static {
        OID_MAP.put("1.2.840.113549.1.9.1", "emulAdress");
        OID_MAP.put("1.2.840.131549.1.9.2", "unstructuredAdress");
        OID_MAP.put("1.2.840.113549.1.9.8", "unstructuredAdress");
        OID_MAP.put("1.2.840.113549.1.9.16", "S/MIME Object Identifier Registery");
    }

    public KeyStoreTools(String type, File file, char[] passwd) throws FileNotFoundException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        System.out.println("init");

        InputStream is = new BufferedInputStream(new FileInputStream(file));

        this.ks = KeyStore.getInstance(type);
        this.ks.load(is, passwd);
    }

    public KeyStoreTools(String type, String fileName, char[] passwd) throws FileNotFoundException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        this(type, new File(fileName), passwd);
    }

    public static String toString(PublicKey certificat) {
        StringBuilder tmp = new StringBuilder();

        if (certificat instanceof RSAPublicKey) {
            RSAPublicKey rsaCertificat = (RSAPublicKey) certificat;
            tmp.append("Alias :")
                    .append("\nDétenteur :")
                    .append("\nAutorité de certification :")
                    //.append("\nValable du ")
                    //.append(certificat.getNotBefore())
                    .append(" ... au ... ")
                    //.append(certificat.getNotAfter())
                    //.append("\nClé publique de type :")
                    //.append(certificat.getSigAlgName())
                    .append("\nModule de chiffrement :")
                    .append(rsaCertificat.getEncoded())
                    .append("\nExposant public :")
                    .append(rsaCertificat.getPublicExponent());
        } else if (certificat instanceof DSAPublicKey) {

            DSAPublicKey dsaCertificat = (DSAPublicKey) certificat;
            tmp.append("Alias :")
                    .append("\nDétenteur :")
                    .append("\nAutorité de certification :")
                    .append("\nValable du")
                    .append(" ... au ... ")
                    .append("\nClé publique de type :")
                    .append("\nModule de chiffrement :")
                    /*.append(dsaCertificat.getModulus())*/
                    .append("\nExposant public :") /*.append(dsaCertificat.getPublicExponent())*/;
        } else {
            tmp.append("?");
        }

        return tmp.toString();
    }

    public static String toString(PrivateKey certificat) {
        StringBuilder tmp = new StringBuilder("PRIVATE KEY\n");

        if (certificat instanceof RSAPrivateKey) {
            RSAPrivateKey rsaPk = (RSAPrivateKey) certificat;
            tmp.append("Clé privée RSA\n");
            tmp.append("\tModule de chiffrement/déchiffrement : ").append('\n');
            tmp.append("\t\t").append(rsaPk.getModulus()).append('\n');
            tmp.append("\tExposant de chiffrement : ").append('\n');
            tmp.append("\t\t").append(rsaPk.getPrivateExponent()).append('\n');
        } else if (certificat instanceof DSAPrivateKey) {
            tmp.append("Clé privée DSA\n");
            DSAPrivateKey dsaPk = (DSAPrivateKey) certificat;
            DSAParams dsaParams = dsaPk.getParams();
            tmp.append("\tParamÃ¨tres globaux\n");
            tmp.append("\t\tP : ").append(dsaParams.getP()).append('\n');
            tmp.append("\t\tQ : ").append(dsaParams.getQ()).append('\n');
            tmp.append("\t\tG : ").append(dsaParams.getG()).append('\n');
            tmp.append("\tClé privée\n");
            tmp.append("\t\tY = ").append(dsaPk.getX());
        } else {
            throw new IllegalArgumentException("Clé de type non traitée!..");
        }
        return tmp.toString();
    }

    public static String toString(X509Certificate cert) throws CertificateEncodingException {
        StringBuilder tmp = new StringBuilder();
        tmp
                .append("Détenteur : ").append(cert.getSubjectDN().toString())
                .append("\n")
                .append("Autorité de certification : ").append(cert.getIssuerDN().toString())
                .append("\n")
                .append("Valable du ").append(cert.getNotBefore())
                .append("\n")
                .append("Valable jusqu'au ").append(cert.getNotAfter())
                .append("\n")
                .append("Clé publique de type : ").append(cert.getPublicKey().getFormat())
                .append("\n")
                .append("Module de chiffrement : ").append(cert.getEncoded())
                .append("\n");
        return tmp.toString();
    }

    public String listCertificates() throws KeyStoreException, CertificateEncodingException {
        StringBuilder tmp = new StringBuilder("List of certificats :\n");
        for (Iterator<String> it = Collections.list(this.ks.aliases()).iterator(); it.hasNext();) {
            String alias = it.next();

            if (this.ks.isCertificateEntry(alias)) {
                tmp.append(" - ").append(KeyStoreTools.toString((X509Certificate) this.ks.getCertificate(alias)).toString()).append("\n");
            }
        }
        return tmp.toString();
    }

    public String listPrivateKeys(char[] passwd) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        Logger.getLogger(KeyStoreTools.class.getName()).log(Level.INFO, "List private Key");
        StringBuilder tmp = new StringBuilder("List of private keys :\n");
        for (Iterator<String> it = Collections.list(this.ks.aliases()).iterator(); it.hasNext();) {
            String alias = it.next();

            if (this.ks.isKeyEntry(alias)) {
                try {
                    Key pk = this.ks.getKey(alias, passwd);
                    if (pk instanceof PrivateKey) {
                        tmp.append(" - ").append((PrivateKey) pk).append("\n");
                    }
                } catch (UnrecoverableKeyException e) {
                    tmp.append(" - ").append("bad password").append("\n");
                }
            }
        }
        return tmp.toString();
    }

    public void importSecretKey(SecretKey key, String alias, char[] passwd) throws KeyStoreException {
        Logger.getLogger(KeyStoreTools.class.getName()).log(Level.INFO, "Import Secret Key");
        this.ks.setKeyEntry(alias, key, passwd, null);
    }

    public void importCertificate(Certificate cert, String alias) {
        Logger.getLogger(KeyStoreTools.class.getName()).log(Level.INFO, "Import Certificat");
        //this.ks.setCertificateEntry(alias, cert);
    }

    public void importCertificates(File file, String[] aliases) throws FileNotFoundException, CertificateException, IOException {
        FileInputStream fin = new FileInputStream(file);
        
        CertificateFactory f = CertificateFactory.getInstance("X.509");
        List<Certificate> certificates = new LinkedList<>();
        int i = 0;
        while(fin.available() > 0) {
            importCertificate(f.generateCertificate(fin), aliases[i++]);
         //certificates.add(f.generateCertificate(fin));   
        }
    }

    public void save(File file, char[] passwd) {
        //OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        //this.ks.store(os, ksPasswd);
    }
}
