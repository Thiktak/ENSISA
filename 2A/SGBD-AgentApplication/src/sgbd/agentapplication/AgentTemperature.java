package sgbd.agentapplication;

/**
 *
 * @author Thiktak Possède deux facettes présentation => 2 classes
 */
public class AgentTemperature {

    public class TemperaturePresJauge extends Object /*Bao_jauge*/ {

        private TemperatureControl control; // control référence

        public TemperaturePresJauge() {
        }

        // Access to control
        public void init(TemperatureControl control) {
            this.control = control;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public float getValue() {
            return this.value;
        }

        //méthode de la BAO surchargée
        //@Overrride
        protected void BaoUsaChangeJaugeValue(float degreCelcius) {
            // modification par manipulation de l'utilisateur
            control.userChangeJaugeValue(degreCelcius);
        }
    }

    public class TemperaturePresTextField extends Object /*BaoTextField*/ {

        private TemperatureControl control;

        // ...
        private void PressTextField() {
        }
    }
}
