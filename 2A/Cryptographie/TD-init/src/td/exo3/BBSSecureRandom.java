package td.exo3;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;

/**
 * Une classe implémentant le générateur aléatoire cryptographiquement sûr de
 * Blum, Blum et Shub.
 *
 * @author Julien Lepagnot
 */
public class BBSSecureRandom extends SecureRandomSpi {

    // valeurs précalculées
    private static final BigInteger two = BigInteger.valueOf(2L);
    private static final BigInteger three = BigInteger.valueOf(3L);
    private static final BigInteger four = BigInteger.valueOf(4L);
    private static final BigInteger ecart = two.pow(80);
    // générateur cryptographiquement sûr de base
    private static final SecureRandom rand = new SecureRandom();
    // module du générateur
    private BigInteger N;
    // état du générateur
    private BigInteger reg;

    /**
     * Construction d'une instance utilisant un module donné.
     *
     * @param N Le module donné
     */
    public BBSSecureRandom(BigInteger N) {
        this.N = N;
    }

    /**
     * Construction d'une instance utilisant un module aléatoire.
     */
    public BBSSecureRandom() {
        // génère N à partir des entiers premiers p et q congrus à 3 modulo 4
        BigInteger p = getPrime(512);
        BigInteger q = getPrime(512);
        // p et q doivent différer d'au moins 2 ^ 80
        while (p.subtract(q).abs().compareTo(ecart) == -1) {
            q = getPrime(512);
        }
        N = p.multiply(q);
    }

    /**
     * Retourne un nombre premier congru à 3 modulo 4.
     *
     * @param bitLength Longueur de l'entier retourné en bits
     */
    private static BigInteger getPrime(int bitLength) {
        BigInteger p;
        do {
            // génère un entier premier p
            p = BigInteger.probablePrime(bitLength, rand);
            // (p mod 4) doit être égal à 3
        } while (!p.mod(four).equals(three));
        return p;
    }

    /**
     * Accesseur pour le module du générateur.
     */
    public BigInteger getMod() {
        return N;
    }

    /**
     * Génère la graine du générateur.
     *
     * @param numBytes Longueur de la graine en octets
     */
    @Override
    protected byte[] engineGenerateSeed(int numBytes) {
        byte[] r = new byte[numBytes];
        do {
            rand.nextBytes(r);
            // la graine doit être différente de 0 et 1
        } while (new BigInteger(r).compareTo(two) == -1);
        return r;
    }

    /**
     * Initialisation d'un tableau d'octets au moyen de BBS.
     *
     * @param bytes Le tableau d'octets à initialiser
     */
    @Override
    protected void engineNextBytes(byte[] bytes) {
        if (reg == null) {
            // initialise la graine du générateur si ce n'était pas fait
            engineSetSeed(engineGenerateSeed(((N.bitLength() + 7) / 8)));
        }
        // pour chaque octet i du tableau en entrée de la méthode...
        for (int i = 0; i < bytes.length; i++) {
            int result = 0;
            // pour chaque bit j qui compose cette octet...
            for (int j = 0; j < 8; j++) {
                // calcule le prochain état du générateur
                reg = reg.modPow(two, N);
                // affecte au bit j la valeur du bit de poids faible de l'état courant
                result = (result << 1) | (reg.testBit(0) == true ? 1 : 0);
            }
            // convertie en byte l'entier ainsi généré
            bytes[i] = (byte) result;
        }
    }

    /**
     * Met à jour la graine du générateur.
     *
     * @param seed Une graine à combiner avec la graine courante
     */
    @Override
    protected void engineSetSeed(byte[] seed) {
        // soit r la graine donnée
        BigInteger r = new BigInteger(seed);
        if (reg == null) {
            // si la graine du générateur n'a pas été initialisée, on la fixe à r
            reg = r;
        } else {
            // sinon, on ajoute r à la graine courante
            reg = reg.add(r);
        }
        // rend la graine strictement inférieure à N
        if (reg.compareTo(N) != -1) {
            reg = reg.mod(N);
        }
        // la graine doit être différente de 0 et 1
        if (reg.compareTo(two) == -1) {
            reg = two;
        }
    }
}
