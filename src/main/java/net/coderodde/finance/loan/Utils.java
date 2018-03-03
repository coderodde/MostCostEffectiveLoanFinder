package net.coderodde.finance.loan;

/**
 * This class provides some common utility methods.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 2, 2018)
 */
public final class Utils {
    
    private Utils() {}
    
    /**
     * Checks that the input potential is a finite, non-NaN, non-negative value.
     * 
     * @param potential the potential to check.
     * @return the input value.
     */
    public static double checkPotential(double potential) {
        return checkDoubleIsPositiveOrZero(
                potential, 
                "The input potential is NaN.", 
                "The input potential is negative: " + potential + ".", 
                "The input potential is positive infinite.");
    }
    
    /**
     * Checks that the input requested potential is a finite, non-NaN,
     * non-negative value.
     * 
     * @param requestedPotential the requested potential to check.
     * @return the input value.
     */
    public static double checkRequestedPotential(double requestedPotential) {
        return checkDoubleIsPositiveOrZero(
                requestedPotential,
                "The input requested potential is NaN.",
                "The input requested potential is negative: " + 
                        requestedPotential + ".",
                "The input requested potential is positive infinite.");
    }
    
    /**
     * Checks that the input maximum interest rate is a finite, non-NaN, 
     * non-negative value.
     * 
     * @param maximumInterestRate the maximum interest rate to check.
     * @return the input value.
     */
    public static double checkMaximumInterestRate(double maximumInterestRate) {
        return checkDoubleIsPositiveOrZero(
                maximumInterestRate,
                "The input maximum interest rate is NaN.",
                "The input maximum interest rate is negative: " +
                        maximumInterestRate + ".",
                "The input maximum interest rate is positive infinite.");
    }
    
    /**
     * Checks that the input interest rate is a finite, non-NaN, 
     * non-negative value.
     * 
     * @param interestRate the interest rate to check.
     * @return the input value.
     */
    public static double checkInterestRate(double interestRate) {
        return checkDoubleIsPositiveOrZero(
                interestRate,
                "The input interest rate is NaN.",
                "The input interest rate is negative: " +
                        interestRate + ".",
                "The input interest rate is positive infinite.");
    }
    
    private static double checkDoubleIsPositiveOrZero(
            double targetValue,
            String errorMessageOnNaN,
            String errorMessageOnNegative,
            String errorMessageOnInfinite) {
        if (Double.isNaN(targetValue)) {
            throw new IllegalArgumentException(errorMessageOnNaN);
        }
        
        if (targetValue < 0.0) {
            throw new IllegalArgumentException(errorMessageOnNegative);
        }
        
        if (Double.isInfinite(targetValue)) {
            throw new IllegalArgumentException(errorMessageOnInfinite);
        }
        
        return targetValue;
    }
}
