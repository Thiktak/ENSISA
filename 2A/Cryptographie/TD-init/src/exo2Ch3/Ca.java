/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exo2Ch3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

/**
 * Une classe implémentant une autorité de certification
 *
 * @author P. Guichet
 */
public class Ca {

    // Le DN du CA
    private static final String DN = "CN=RootCA OU=M2 O=miage C=FR";
    // L'alias permettant la récupération du certificat autosigné du CA
    private static final String ALIAS = "miageCA";
    // Le chemin du fichier contenant le keystore du CA
    private static final String CA_KS_FILE = "cadir/ksca.ks";
    // L'OID de l'algorithme SHA-1
    private static final String SHA1_OID = "1.3.14.3.2.26";
    // L'OID de l'algorithme SHA1withRSA
    private static final String SHA1_WITH_RSA_OID = "1.2.840.113549.1.1.5";
    // L'OID de l'extension Basic Constraint
    private static final String BASIC_CONSTRAINT_OID = "2.5.29.19";
    // L'OID de l'extension SubjectKeyIdentifier
    private static final String SUBJECT_KEY_IDENTIFIER_OID = "2.5.29.14";
    // L'OID de l'extension keyUsage
    private static final String KEY_USAGE_OID = "2.5.29.15";
    // L'OID de l'extension extKeyUsage
    private static final String EXT_KEY_USAGE_OID = "2.5.29.37";
    // L'OID de l'extension altName
    private static final String SUBJECT_ALT_NAME_OID = "2.5.29.17";
    // La valeur de l'extension keyUsage pour une autorité racine
    private static final int CA_KEY_USAGE_VALUE =
            KeyUsage.digitalSignature | KeyUsage.nonRepudiation | KeyUsage.cRLSign | KeyUsage.keyCertSign;
    // La valeur de l'extension keyUsage pour un certificat de serveur
    private static final int SV_KEY_USAGE_VALUE =
            KeyUsage.keyAgreement | KeyUsage.keyEncipherment | KeyUsage.digitalSignature;
    // Délimiteur début certificat
    private static final String CERT_BEGIN = "-----BEGIN CERTIFICATE-----\n";
    // Délimiteur fin certificat
    private static final String CERT_END = "\n-----END CERTIFICATE-----";
    // Le générateur de numéros de série
    private static SerialIdGenerator sIdGen;
    // Bloc d'initialisation du générateur

    static {
        try {
            sIdGen = new SerialIdGenerator();
        } catch (Exception ex) {
            Logger.getLogger(Ca.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    // Le certificat du CA
    private Certificate caCert;
    // La clé privée du CA
    private PrivateKey caPk;

    /**
     * Construction d'une instance de la classe
     *
     * @param passwd le mot de passe protégeant le keystore du CA
     * @throws GeneralSecurityException si la fabrication/récupération du
     * certificat du CA échoue
     * @throws IOException si une erreur d'entrée-sortie se produit, par exemple
     * sérialisation du keystore corrompue
     */
    public Ca(char[] passwd) throws GeneralSecurityException, IOException {
        KeyStore ksCa = KeyStore.getInstance("JCEKS");
        File caDir = new File(CA_KS_FILE);
        if (caDir.exists()) {
            // Le keystore existe déjà il suffit de le charger
            ksCa.load(new BufferedInputStream(new FileInputStream(caDir)), passwd);
            // puis de récupérer le certificat du CA et la clé privée associée
            this.caCert = (Certificate) ksCa.getCertificate(ALIAS);
            this.caPk = (PrivateKey) ksCa.getKey(ALIAS, passwd);
        } else {
            // le keystore n'existe pas il faut construire la paire de clés publique, privée
            // et empaqueter la clé publique dans un certificat X509 autosigné
            installCA(ksCa, passwd, caDir);
        }
    }

    /**
     * Méthode d'aide pour la fabrication d'une CA qui n'existe pas encore
     *
     * @param ks le keystore du CA
     * @param passwd le mot de passe qui protège le keystore
     * @param caDir le fichier où sera sérialisé le keystore
     * @throws GeneralSecurityException si la fabrication/récupération du
     * certificat du CA échoue
     * @throws IOExceptionsi une erreur d'entrée-sortie se produit, par exemple
     * sérialisation du keystore corrompue
     */
    private void installCA(KeyStore ks, char[] passwd, File caDir)
            throws GeneralSecurityException, IOException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair caKp = kpg.generateKeyPair();
        this.caPk = caKp.getPrivate();
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        // le numéro de série de ce certificat
        certGen.setSerialNumber(BigInteger.ONE);
        // le nom de l'émetteur (et du sujet)
        X500Principal caDn = new X500Principal(DN);
        certGen.setIssuerDN(caDn);
        // le nom du sujet
        certGen.setSubjectDN(caDn);
        Calendar calendar = Calendar.getInstance();
        // le début de la période de validité
        Date notBefore = calendar.getTime();
        certGen.setNotBefore(notBefore);
        calendar.set(2010, 11, 31);
        // la fin de la période de validité
        certGen.setNotAfter(calendar.getTime());
        // la clé publique enveloppée dans le certificat
        certGen.setPublicKey(caKp.getPublic());
        // l'algorithme de signature
        certGen.setSignatureAlgorithm(SHA1_WITH_RSA_OID);
        // extension définissant l'usage de la clé
        certGen.addExtension(
                KEY_USAGE_OID, false, new KeyUsage(CA_KEY_USAGE_VALUE));
        // extension BasicConstraint
        certGen.addExtension(
                BASIC_CONSTRAINT_OID, true, new BasicConstraints(Integer.MAX_VALUE));
        // extension subjectKeyIdentifier
        certGen.addExtension(
                SUBJECT_KEY_IDENTIFIER_OID,
                false,
                new SubjectKeyIdentifierStructure(caKp.getPublic()));
        this.caCert = (Certificate) certGen.generate(this.caPk);
        ks.load(null, passwd);
        // Insérer le certificat dans le keystore
        ks.setCertificateEntry(ALIAS, caCert);
        // Insérer la clé privée associée dans le keystore
        KeyStore.PrivateKeyEntry pke =
                new KeyStore.PrivateKeyEntry(caPk, new Certificate[]{this.caCert});
        ks.setEntry(ALIAS, pke, new KeyStore.PasswordProtection(passwd));
        // Sauvegarder le keystore nouvellement créé
        OutputStream out = new BufferedOutputStream(new FileOutputStream(caDir));
        ks.store(out, passwd);
    }

    /**
     * Génération d'un certificat pour l'identification d'un serveur
     *
     * @param dn le nom distingué du serveur
     * @param altName le nom alternatif du serveur
     * @param pk la clé publique devant être enrobée dans le certificat
     * @return un certificat (norme X509 v3) empaquettan la clé publique pk
     * @throws GeneralSecurityException si la fabrication du certificat échoue
     * @throws IOException si la fabrication du numéro de série échoue
     */
    X509Certificate generateServerCertificate(String dn, String altName, PublicKey pk)
            throws GeneralSecurityException, IOException {
        X509V3CertificateGenerator certServGen = this.caCert;
        X500Principal subjectName = new X500Principal("CN=Test V3 Certificate");

        certServGen.setSubjectDN(subjectName);
        certServGen.setPublicKey(pk);
        return certServGen.generate(this.caPk, "BC");
    }

    /**
     * Exportation du certificat du CA en DER encodé Base64
     *
     * @param file le fichier où exporter le certificat
     * @param le certificat à exporter
     * @throws GeneralSecurityException si l'encodage DER échoue
     * @throws IOException si l'exportation échoue
     */
    public static void exportCertificate(File file, Certificate cert)
            throws GeneralSecurityException, IOException {
    }

    /**
     * Exportation du certificat du CA en DER encodé base64
     *
     * @param fileName le nom du fichier où exporter le certificat
     * @param le certificat à exporter
     * @throws GeneralSecurityException si l'encodage DER échoue
     * @throws IOException si l'exportation échoue
     */
    public static void exportCertificate(String fileName, Certificate cert)
            throws GeneralSecurityException, IOException {
        exportCertificate(new File(fileName), cert);
    }
}