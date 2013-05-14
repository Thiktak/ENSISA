package td.exo3.ECSigner;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.Random;

/**
 *
 * @author Thiktak
 */
public class ECKeyPairGenerator {
    
    KeyPairGenerator kpg;
    
    //  qui construira une instance de la classe permettant la génération de paire de clés basée sur la courbe dont le nom aura été passé en paramètre. 
    public ECKeyPairGenerator(String curveName) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Provider bc = new org.bouncycastle.jce.provider.BouncyCastleProvider();
        Security.addProvider(bc);
        
        
        // courbe
        ECGenParameterSpec genSpec = new ECGenParameterSpec("P-224");
        this.kpg = KeyPairGenerator.getInstance("ECDSA", bc);
        
        this.kpg.initialize(genSpec, new java.security.SecureRandom());
        
        /*
        Provider bc = new org.bouncycastle.jce.provider.BouncyCastleProvider();
        // et l'installer parmi les providers reconnus
        // cette étape est facultative
        Security.addProvider(bc);
        // Quelque soit le provider utilisé
        // Spécifier la courbe utilisée 
        // ici c'est la courbe NIST P-224
        ECGenParameterSpec genSpec = new ECGenParameterSpec("P-224");
        // Obtenir un générateur de paires de clés ECDSA
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA");
        // ou de manière alternative si le provider bc a été installé
        // kpg = KeyPairGenerator.getInstance("ECDSA", bc);

        // Initialiser le générateur de paires de clés
        // ici rand est une instance de java.security.SecureRandom           
        kpg.initialize(genSpec, rand);
        //Générer la paire de clés ECDSA
        KeyPair kp = kpg.generateKeyPair();
        // Typiquement à ce point prKey = kp.getPrivate(), 
        // la clé privée serait stockée dans un KeyStore,
        // tandis que pubKey = kp.getPublic(), 
        // la clé publique serait empaquetée dans un certificat

        // Obtenir une instance de Signature spécialisée dans ECDSA
        Signature signer = Signature.getInstance(curveName);
        // ou si le provider bc a été installé
        // Signature signer  = Signature.getInstance("SHA1withECDSA", bc);

        // Initiliser l'objet signant avec la clé privée
        signer.initSign(prKey);
        * */
    }

    // qui générera une paire de clé. 
    public KeyPair generateKeyPair() {
        return this.kpg.generateKeyPair();
    }

    public KeyPair getECKeyPair() {
        return this.generateKeyPair();
    }
}
