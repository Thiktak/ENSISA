package td.exo2;

import java.io.PrintStream;

import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;

/**
 *
 * @author Olivarès Georges <dev@olivares-georges.fr>
 */
public class ProviderTools {

    /**
     * Get list of Providers
     *
     * @return Provider[]
     */
    public static Provider[] getProviders() {
        return Security.getProviders();
    }

    /**
     * Print into StrintStream the list of Provider and their Services
     *
     * @param out
     */
    private static void printProviders(PrintStream out) {
        StringBuilder tmp = new StringBuilder();

        // Print Providers
        for (Provider p : ProviderTools.getProviders()) {
            tmp
                    .append(p.getName()).append(" v")
                    .append(p.getVersion()).append(" ")
                    .append(p.getInfo()).append(" : ")
                    .append("\n")
                    .append("\twith services : ");

            // Print Services
            for (Service s : p.getServices()) {
                tmp
                        .append("\n\t\t").append(s.getType()).append(" (")
                        .append(s.getAlgorithm())
                        .append(")");
            }
            tmp.append("\n");
        }
        out.println(tmp.toString());
    }

    /**
     * Install Provider
     *
     * @param providerName
     * @param providerInfo
     * @param version
     * @return
     */
    public static int installProvider(String providerName, String providerInfo, double version) {
        // Provider is an abstract class
        // So ... we make an Anonymous Class
        Provider provider = new Provider(providerName, version, providerInfo) {
        };
        return Security.addProvider(provider);
    }

    /**
     * Instann Service into Provider
     *
     * @param providerName
     * @param type
     * @param serviceName
     * @param className
     */
    public static Object installService(String providerName, String type, String serviceName, String className) {

        return Security.getProvider(providerName).put(type + "." + serviceName, className);
    }

    public static void main(String[] args) {
        try {
            ProviderTools.printProviders(System.out);
            ProviderTools.installProvider("PG", "BBS", 1.0);
            ProviderTools.installService("PG", "SecureRandom", "BBSPRNG", "td2.BBSSecureRander");
            ProviderTools.printProviders(System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}