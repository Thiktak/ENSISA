package sgbd.agentapplication;

/**
 *
 * @author Thiktak
 */
public class Application {

    private TemperatureControl controlTemperature;
    private QuantityControl controlQuantity;
    private PressionControl controlPression;
    private float R = (float) 8.31;

    public Application() {
    }

    // Notification if the temperature change
    public boolean temperatureChanged(float kelvin, float[] temperatureMaxPossible) {
        float pression, pressionMax, pressionMin;
        float nbMole;
        boolean result = true;

        // Détermine la pression
        nbMole = controleQuantity.getValue();
        pression = (nbMole * R * kelvin) / kelvin; // formule des GP
        pressionMax = controlPression.getMaxValue();
        pressionMin = controlPression.getMinValue();

        if (pression > pressionMax) {
            float newTemp;
            controlPression.setValue(pressionMax);

            //recalcule la T°
            newTemp = (pressionMax * kelvin) / (nbMole * R);
            temperatureAuth[0] = newTemp;
            result = false;
        } else if (pression < pressionMin) {
            // Traitement analogue
        } else {
            controlPression.setValue();
            temperatureAuth[0] = kelvin;
        }
        return result;
    }
}
