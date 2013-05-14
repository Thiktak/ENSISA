package td.exo3;

import java.security.Provider;
import java.security.Provider.Service;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Une classe permettant la manipulation des providers de services cryptographiques.
 * @author P. Guichet & J. Lepagnot
 */
public class ProviderTools {

    /**
     * Description des services offerts par les providers installés.
     * @return Un String décrivant les services offerts par les providers installés.
     */
    public static String listProviders() {
        StringBuilder sb = new StringBuilder();
        // Boucle sur les providers
        for (Provider provider : Security.getProviders()) {
            sb.append(String.format("Provider : %s version : %3.1f\n", provider.getName(), provider.getVersion()));
            // Ensemble des implémentations des services ordonnés par type et nom d'algorithme
            // Comme Service n'implémente pas l'interface Comparable on le muni d'un comparateur
            // basé sur l'ordre lexicographique (type service, nom algorithme)
            SortedSet<Provider.Service> services = new TreeSet<>(
                new Comparator<Provider.Service>() {
                // La fonction de comparaison
                @Override
                public int compare(Service s1, Service s2) {
                    return (s1.getType() + '.'+ s1.getAlgorithm()).compareTo(s2.getType() + '.' + s2.getAlgorithm());
                }
            });
            // Les services implémentés par le provider courant sont introduits dans l'ensemble
            services.addAll(provider.getServices());
            for (Provider.Service service : services) {
                sb.append(String.format("\t%-28s%-48s\t%s\n", service.getType(), service.getAlgorithm(), service.getClassName()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Insertion d'un nouveau provider.
     * @param name du nouveau provider
     * @param info une brève description du provider
     * @param version la version courante du provider
     */
    public static void installProvider(String name, String info, double version) {
        Security.addProvider(new Provider(name, version, info) {});
    }

    /**
     * Installation d'un nouveau service auprès d'un provider.
     * @param providerName le nom du provider
     * @param type le type du service, par exemple SecureRandom
     * @param serviceName le nom du service
     * @param className le nom de la classe d'implémentation du service
     */
    public static void installService(String providerName, String type, String serviceName, String className) {
        Provider provider = Security.getProvider(providerName);
        provider.put(type + "." + serviceName, className);
    }
    
    /**
     * Exemple d'utilisation de la classe.
     * @param args les éventuels arguments transmis en ligne de commande
     */
    public static void main(String[] args) {
        try {
            // liste les services offerts par les providers installés
            System.out.println(ProviderTools.listProviders());
            // installe un nouveau provider
            ProviderTools.installProvider("PG", "Guichet-Security-Provider-v1.0 implements BBS", 1.0);
            // installe un nouveau service
            ProviderTools.installService("PG", "SecureRandom", "BBSPRNG", "td2.BBSSecureRandom");
            // liste les services offerts par les providers installés
            System.out.println(ProviderTools.listProviders());
            // teste le nouveau service installé
            SecureRandom BBS = SecureRandom.getInstance("BBSPRNG");
            byte[] test = new byte[10];
            BBS.nextBytes(test);
            for (int i = 0; i < 10; i++) {
                System.out.println(test[i]);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProviderTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
