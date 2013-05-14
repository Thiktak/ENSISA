package td.exo3;

import td.exo3.ECSigner.ECSigner;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import td.exo3.ECSigner.ECKeyPairGenerator;

/**
 * Une classe de démonstration de ECSigner
 *
 * @author Patrick Guichet
 */
public class ECSignerTest {

    public static void main(String[] args) {
        try {
            // Création de l'objet signant et vérifiant une signature
            ECSigner ecs = new ECSigner("SHA256withECDSA");
            // Générateur de clés basé sur la courbe (en caractéristique 2) "c2pnb208w1"
            ECKeyPairGenerator eckp = new ECKeyPairGenerator("c2pnb208w1");
            KeyPair kp = eckp.getECKeyPair();
            // Affichage des clés
            System.out.printf("Clée privée :\n\t%s\n", kp.getPrivate().toString());
            System.out.printf("Clée publique :\n\t%s\n", kp.getPublic().toString());
            // Calcul de la signature d'un fichier
            String tag = ecs.signFile("src/td2/ECSigner.java", kp.getPrivate());
            System.out.printf("Tag signature : %s\n", tag);
            // Vérification de la signature de ce même fichier
            System.out.printf(
                    "Vérification : %B\n", ecs.verifyFile("src/td2/ECSigner.java",
                    kp.getPublic(),
                    tag));
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(ECSignerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
