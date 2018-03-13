package net.coderodde.finance.loan.support;

/**
 * This class implements the default most cost effective loan finder using
 * Fibonacci heap.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 1, 2018)
 * @param <I> the actor identity type.
 */
public final class FibonacciHeapMostCostEffectiveLoanFinder<I>
        extends AbstractMostCostEffectiveLoanFinder<I> {
    
    public FibonacciHeapMostCostEffectiveLoanFinder() {
        super(new FibonacciHeap<HeapNode<I>>());
    }
}
