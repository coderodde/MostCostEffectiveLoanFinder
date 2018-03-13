package net.coderodde.finance.loan.support;

import java.util.PriorityQueue;

/**
 * This class implements the default most cost effective loan finder using a 
 * binary heap.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 1, 2018)
 * @param <I> the actor identity type.
 */
public final class BinaryHeapMostCostEffectiveLoanFinder<I>
        extends AbstractMostCostEffectiveLoanFinder<I> {

    public BinaryHeapMostCostEffectiveLoanFinder() {
        super(new PriorityQueue<HeapNode<I>>());
    }
}
