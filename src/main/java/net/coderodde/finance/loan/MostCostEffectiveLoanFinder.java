package net.coderodde.finance.loan;

/**
 * This interface defines the API for all most cost effective loan finder 
 * algorithms.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 1, 2018)
 * @param <I> the actor identity type.
 */
public interface MostCostEffectiveLoanFinder<I> {
    
    /**
     * Computes a most cost effective loans for the input actor in the input 
     * actor graph such that the actor receives {@code requiredPrincipal} with 
     * minimal interest rates or as much as possible while obeying the interest
     * rate constraints.
     * 
     * @param actor               the debt actor.
     * @param requiredPrincipal   the required principal.
     * @param maximumInterestRate the maximum allowed effective interest rate.
     * @return the object describing the loan arrangements.
     */
    public MostCostEffectiveLoan<I>  findLenders(Actor<I> actor,
                                                 double requiredPrincipal,
                                                 double maximumInterestRate);
}
