package net.coderodde.finance.loan.support;

/**
 *
 * @author Rodion "rodde" Efremov
 */
abstract class AbstractMostCostEffectiveLoanFinderBase {
    
    /**
     * Returns the combined interest rate given two target interest rates.
     * 
     * @param interestRate1 the first interest rate.
     * @param interestRate2 the second interest rate.
     * @return the combined interest rate.
     */
    protected double combineInterestRates(double interestRate1, 
                                          double interestRate2) {
        return interestRate1 + interestRate2 + interestRate1 * interestRate2;
    }
}
