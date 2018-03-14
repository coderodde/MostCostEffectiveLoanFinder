package net.coderodde.finance.loan.support;

import java.util.PriorityQueue;
import net.coderodde.finance.loan.ActorGraph;

/**
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 13, 2018)
 */
public final class BinaryHeapPreprocessingMostCostEffectiveLoanFinder<I>
extends AbstractPreprocessingMostCostEffectiveLoanFinder<I> {
    
    public BinaryHeapPreprocessingMostCostEffectiveLoanFinder(
            ActorGraph<I> actorGraph) {
        super(actorGraph, new PriorityQueue<>());
    }
}
