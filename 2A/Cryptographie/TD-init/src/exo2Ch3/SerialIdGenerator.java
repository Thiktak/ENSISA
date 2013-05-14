package exo2Ch3;

import java.math.BigInteger;

/**
 *
 * @author Thiktak
 */
class SerialIdGenerator {
    private static BigInteger counter = BigInteger.ONE;
    
    synchronized public static BigInteger generate() {
        return (SerialIdGenerator.counter = SerialIdGenerator.counter.add(BigInteger.ONE));
    }
}
