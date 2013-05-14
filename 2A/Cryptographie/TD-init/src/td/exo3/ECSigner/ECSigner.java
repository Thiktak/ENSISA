package td.exo3.ECSigner;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * @author Thiktak
 */
public class ECSigner {

    // qui construira une instance de la classe spécialisée pour le calcul et la vérification de signatures selon l'algorithme dont le nom est transmis en paramètre, par exemple SHA1withECDSA. 
    public ECSigner(String algo) {
        // Signature signer  = Signature.getInstance("SHA1withECDSA", bc);
    }

    // qui permettra la signature du fichier file avec la clé privée privateKey. La chaîne renvoyée sera la représentation en base64 du tableau de bytes résultant de la signature. 
    public String signFile(File file, PrivateKey privateKey) {
        return null;
    }

    // qui vérifiera que tagB64 est bien la signature du fichier file calculée avec la clé privée associée à publicKey. 
    public boolean verifyFile(File file, PublicKey publicKey, String tagB64) {
        return false;
    }

    // qui vérifiera que tagB64 est bien la signature du fichier de nom fileName calculée avec la clé privée 
    public boolean verifyFile(String fileName, PublicKey publicKey, String tagB64) {
        return false;
    }

    public String signFile(String srctd2ECSignerjava, PrivateKey aPrivate) {
        return null;
    }
}
