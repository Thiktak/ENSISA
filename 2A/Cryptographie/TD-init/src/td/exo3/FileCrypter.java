package td.exo3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Une classe utilitaire pour le chiffrement déchiffrement de fichier
 * @author Patrick Guichet
 */
public class FileCrypter {
    // Le générateur aléatoire nécessaire à la fabrication des vecteurs d'initialisation
    private static final SecureRandom RAND = new SecureRandom();
    // La taille par défaut du buffer de lecture
    private static final int BUFFER_SIZE = 4096;
    // Une collection des noms d'algorithme nécessitant un vecteur d'initialisation
    // typiquement ce sont des algorithmes de chiffrement en continu dont l'état doit
    // être explicitement initialisé, par exemple HC128, Salsa20
    private final static Map<String, Integer> algoWithIV = new HashMap<String, Integer>();
    // initialisation de la map
    static {
        algoWithIV.put("HC128", 16);
        algoWithIV.put("Salsa20", 8);
    }
    // L'objet chargé du chiffrage/déchiffrage
    private Cipher cipher = null;
    // Indicateur mémorisant si un vecteur d'initialisation est nécessaire
    private boolean ivNeeded = false;

    /**
     * Construction d'une instance de la classe.
     * @param algoName le nom standard de l'algorithme : AES, DES,...
     * @param modeName le mode d'utilisation : ECB, CBC,...
     * @param paddingName le nom du rembourrage utilisé : Pkcs5Padding,...
     * @throws GeneralSecurityException si la construction de l'instance échoue,
     * typiquement à cause d'un nom non reconnu par le JCE.
     */
    public FileCrypter(String algoName, String modeName, String paddingName)
            throws GeneralSecurityException {
        this.cipher = Cipher.getInstance(makeCipherName(algoName, modeName, paddingName));
    }

    /**
     * Construction d'une instance de la classe.
     * @param algoName le nom standard de l'algorithme : AES, DES,...
     * @param modeName le mode d'utilisation : ECB, CBC,...
     * @param paddingName le nom du rembourrage utilisé : Pkcs5Padding,...
     * @param providerName le nom du provider devant implémenter le service
     * @throws GeneralSecurityException si la construction de l'instance échoue,
     * typiquement à cause d'un nom non reconnu par le JCE ou si le provider n'est pas installé.
     */
    public FileCrypter(String algoName, String modeName, String paddingName, String providerName)
            throws GeneralSecurityException {
        this.cipher = Cipher.getInstance(makeCipherName(algoName, modeName, paddingName), providerName);
    }

    /**
     * Méthode d'aide permettant la construction du nom standard complet de l'objet chiffrant
     * : AES/OFB/Pkcs5Padding,... La méthode initialise un flag mémorisant si un vecteur
     * d'initialisation est nécessaire.
     * @param algoName le nom standard de l'algorithme : AES, DES,...
     * @param modeName le mode d'utilisation : ECB, CBC,...
     * @param paddingName le nom du rembourrage utilisé : Pkcs5Padding,...
     * @return le nom standard complet de l'objet chiffrant.
     */
    private String makeCipherName(String algoName, String modeName, String paddingName) {
        StringBuilder sb = new StringBuilder(algoName);
        if (modeName == null) {
            // algorithme de chiffrement en continu type RC4
            if (algoWithIV.get(algoName) != null) {
                ivNeeded = true;
            }
            return sb.toString();
        }
        sb.append('/').append(modeName);
        if (!modeName.equalsIgnoreCase("ECB")) // mode distinct de ECB un vecteur d'initialisation est nécessaire
        {
            ivNeeded = true;
        }
        if (paddingName == null) {
            return sb.toString();
        }
        sb.append('/').append(paddingName);
        return sb.toString();
    }

    /**
     * Méthode d'aide chargée de crypter ou de décrypter le fichier
     * @param key la clé de chiffrement/déchiffrement
     * @param mode le mode d'utilisation : chiffrage ou déchiffrage
     * @param specs le vecteur d'initialisation, peut être <code>null</code>
     * @param in le flot entrant d'où extraire les octets à chiffrer ou à déchiffrer
     * @param out le flot sortant où insérer les octets chiffrés/déchiffrés
     * @throws GeneralSecurityException si l'opération de chiffrage ou déchiffrage échoue
     * @throws IOException si une opération d'entrée/sortie echoue
     */
    private void processFile(Key key, int mode, AlgorithmParameterSpec specs, InputStream in, OutputStream out)
            throws GeneralSecurityException, IOException {
        if (specs == null) {
            cipher.init(mode, key);
        } else {
            cipher.init(mode, key, specs);
        }
        // Construction du flot chiffrant/déchiffrant
        CipherOutputStream cipherOut = new CipherOutputStream(out, cipher);
        byte[] buffer = new byte[BUFFER_SIZE];
        int nBytes = 0;
        // boucle de chiffrement/déchiffrement
        while ((nBytes = in.read(buffer)) != -1) {
            cipherOut.write(buffer, 0, nBytes);
        }
        cipherOut.close();
    }

    /**
     * Chiffrement d'un fichier
     * @param key la clé de chiffrement
     * @param inFile le fichier à chiffrer
     * @param outFile le fichier chiffré
     * @throws GeneralSecurityException si l'opération de chiffrage échoue
     * @throws IOException si une opération d'entrée/sortie echoue
     */
    public void cryptFile(Key key, File inFile, File outFile)
            throws GeneralSecurityException, IOException {
        // Le flot d'octets à chiffrer
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(inFile));
        // Le flot d'octets chiffrés
        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(outFile));
        if (!ivNeeded) {
            processFile(key, Cipher.ENCRYPT_MODE, null, bin, bout);
        } else {
            // Fabrication d'un nonce pour l'initialisation
            int ivSize = cipher.getBlockSize();
            // Cas spéciaux des algorithmes de chiffrement en continu
            // qui nécessitent un vecteur d'initialisation.
            if (ivSize == 0) {
                Integer ivs = algoWithIV.get(cipher.getAlgorithm());
                if (ivs == null) {
                    throw new IllegalArgumentException("Algorithme non supporté!..");
                }
                ivSize = ivs;
            }
            byte[] bytes = new byte[ivSize];
            RAND.nextBytes(bytes);
            // Construction d'un vecteur d'initialisation
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(bytes);
            // Insertion du vecteur d'initialisation en tête du fichier chiffré
            bout.write(bytes);
            // traitement du fichier
            processFile(key, Cipher.ENCRYPT_MODE, ivSpec, bin, bout);
        }
    }

    /**
     * Chiffrement d'un fichier.
     * @param key la clé de chiffrement.
     * @param inFileName le nom du fichier à chiffrer.
     * @param outFileName le nom du fichier chiffré.
     * @throws GeneralSecurityException si l'opération de chiffrage échoue.
     * @throws IOException si une opération d'entrée/sortie echoue.
     */
    public void cryptFile(Key key, String inFileName, String outFileName)
            throws GeneralSecurityException, IOException {
        cryptFile(key, new File(inFileName), new File(outFileName));
    }

    /**
     * Déchiffrement d'un fichier.
     * @param key la clé de chiffrement.
     * @param inFile le fichier à déchiffrer.
     * @param outFile le fichier déchiffré.
     * @throws GeneralSecurityException si l'opération de chiffrage échoue.
     * @throws IOException si une opération d'entrée/sortie echoue.
     */
    public void decryptFile(Key key, File inFile, File outFile)
            throws GeneralSecurityException, IOException {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(inFile));
        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(outFile));
        if (!ivNeeded) {
            processFile(key, Cipher.DECRYPT_MODE, null, bin, bout);
        } else {
            int ivSize = cipher.getBlockSize();
            // Cas spéciaux des algorithmes de chiffrement en continu
            // qui nécessitent un vecteur d'initialisation.
            if (ivSize == 0) {
                Integer ivs = algoWithIV.get(cipher.getAlgorithm());
                if (ivs == null) {
                    throw new IllegalArgumentException("Algorithme non supporté!..");
                }
                ivSize = ivs;
            }
            byte[] bytes = new byte[ivSize];
            int nb = bin.read(bytes);
            if (nb != ivSize) {
                throw new IllegalBlockSizeException("Vecteur d'initialisation incorrect!..");
            }
            processFile(key, Cipher.DECRYPT_MODE, new IvParameterSpec(bytes), bin, bout);

        }
    }

    /**
     * Déchiffrement d'un fichier.
     * @param key la clé de chiffrement.
     * @param inFileName le nom du fichier à déchiffrer.
     * @param outFileName le nom du fichier chiffré.
     * @throws GeneralSecurityException si l'opération de chiffrage échoue.
     * @throws IOException si une opération d'entrée/sortie echoue.
     */
    public void decryptFile(Key key, String inFileName, String outFileName)
            throws GeneralSecurityException, IOException {
        decryptFile(key, new File(inFileName), new File(outFileName));
    }

    /**
     * Exemple d'utilisation de la classe
     * @param args les éventuels arguments transmis en ligne de commande
     */
    public static void main(String[] args) {
        try {
            // Pour pouvoir utiliser l'API BouncyCastle au travers du mécanisme standard du JCE
            Security.addProvider(new BouncyCastleProvider());
            // Création d'un chiffreur/déchiffreur basé sur l'algorithme Salsa20
            FileCrypter crypter = new FileCrypter("Salsa20", null, null);
            // Génération de la clé secrète pour l'algorithme
            KeyGenerator kg = KeyGenerator.getInstance("Salsa20");
            Key key = kg.generateKey();
            // Chiffrement fichier source
            crypter.cryptFile(key, "src/td2/FileCrypter.java", "FileCrypter.cry");
            // Déchiffrement
            crypter.decryptFile(key, "FileCrypter.cry", "FileCrypter.dat");
            // Comparaison des longueurs du fichier en clair et du fichier décrypté
            long srcLength = new File("src/td2/FileCrypter.java").length();
            long resultLength = new File("FileCrypter.dat").length();
            assert srcLength == resultLength : String.format(
                    "fichiers source et cible de longueur distincte %-8d : %-8d !..",
                    srcLength, resultLength);
        } catch (Exception ex) {
            Logger.getLogger(FileCrypter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}