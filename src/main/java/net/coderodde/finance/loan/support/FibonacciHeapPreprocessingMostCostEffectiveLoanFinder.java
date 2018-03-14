package net.coderodde.finance.loan.support;

import net.coderodde.finance.loan.ActorGraph;

/**
 * This class preprocesses the actor graph in order to answer the loan queries
 * much faster.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 11, 2018)
 * @param <I> the actor identity type.
 */
public final class FibonacciHeapPreprocessingMostCostEffectiveLoanFinder<I> 
        extends AbstractPreprocessingMostCostEffectiveLoanFinder<I> {

    public FibonacciHeapPreprocessingMostCostEffectiveLoanFinder(
            ActorGraph<I> actorGraph) {
        super(actorGraph, new FibonacciHeap<>());
    }
}
