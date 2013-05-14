package sgbd.agentapplication;

/**
 *
 * @author Thiktak
 */
public class Abstraction {

    public class TemperatureAbstract {

        private TemperatureControl control;
        private float kelvin; // T°C

        public TemperatureAbstract() {
        }

        public void init(TemperatureControl control) {
        }

        public void setValue(float k) {
            this.kelvin = k;
        }
    }
}
