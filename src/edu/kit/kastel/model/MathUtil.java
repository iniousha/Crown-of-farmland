package edu.kit.kastel.model;

/**
 * this utility class provides mathematical helping methods.
 * @author ucktt
 */
public final class MathUtil {

    private MathUtil() {
    }

    /**
     * returns the greatest common divisor of two integers.
     * @param firstInteger the first integer
     * @param secondInteger the second integer
     * @return the greatest common divisor
     */
    public static int greatestCommonDivisor(int firstInteger, int secondInteger) {
        if (secondInteger == 0) {
            return firstInteger;
        }
        return greatestCommonDivisor(secondInteger, firstInteger % secondInteger);
    }

    /**
     * checks whether the given number is prime.
     * @param number the number to check
     * @return true if the number is prime; false otherwise
     */
    public static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i < number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns the greatest common divisor for two units,
     *     between the maximum of the GCD between their attack points and the GCD of their defence points.
     * @param attackPoint1 attack points of the first unit.
     * @param defencePoint1 defence points of the first unit
     * @param attackPoint2 attack points of the second unit
     * @param defencePoint2 defence points of the second unit
     * @return the maximum of GCD(attackPoint1, attackPoint2) and GCD(defencePoint1, defencePoint2)
     */
    public static int g3t(int attackPoint1, int defencePoint1, int attackPoint2, int defencePoint2) {
        int greatestCommonDivisorAttackPoint = greatestCommonDivisor(attackPoint1, attackPoint2);
        int greatestCommonDivisorDefencePoint = greatestCommonDivisor(defencePoint1, defencePoint2);
        return Math.max(greatestCommonDivisorAttackPoint, greatestCommonDivisorDefencePoint);
    }
}
