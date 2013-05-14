package sgbd.agentapplication;

import AgentTemperature.TemperaturePresJauge;
import java.awt.Color;

/**
 *
 * @author Thiktak
 */
public class Control {

    public class TemperatureControl {

        private Application application; // ref
        private AgentTemperature.TemperaturePresJauge presJauge; // présentation
        private AgentTemperature.TemperaturePresTextField presTextField; // présentation
        private Abstraction.TemperatureAbstract abstraction; // abstraction

        public TemperatureControl() {
            this.application = new Application();

            this.presJauge = new AgentTemperature.TemperaturePresJauge();
            this.presTextField = new AgentTemperature.TemperaturePresTextField();
            this.abstraction = new Abstraction.TemperatureAbstract();
        }

        public void init(Application application) {
            this.application = application;
        }

        public void userChangeJaugeValue(float degreCelcius) {
            float kelvin, temperatureAuth[] = new float[1];
            presTextfield.setValue(degreCelcius); // répercute la valeur dans l<a zone d'édition
            kelvin = (float) (degreCelcius + 273.15); // Transforme en degré kelvin

            abstraction.setValue(kelvin);

            if (application.temperatureChanged(kelvin, temperatureAuth) == false) {
                // La montée en température n'a pas pu être possible en raison d'une pression trop élevée (faible)
                float newDegreCelcius = (float) (temperatureAuth[0] - 273.15);
                presJauge.setValue(newDegreCelcius);
                presTextField.setValue(newDegreCelcius);
                presTextField.Bao_setBackground(Color.RED);
                
                abstraction.setValue(temperatureAuth[0]);
            }
        }

        public void userChangeTextFieldValue(float degreCelcius) {
            float kelvin, temperatureAuth[] = new float[1];
            presJauge.setValue(degreCelcius);
            kelvin = (float) (degreCelcius + 273.15);
        }

        public void userValidTextFieldValue(float degreCelcius) {
            // L'utilisation valide le champde saisie
            // Il faut reporter la valeur de la jauge (correcte) dans le champ de saisie
            presTextField.setValue(presJauge.getValue());
        }

        public void setValue(float value) {
        }
    }
}
}
