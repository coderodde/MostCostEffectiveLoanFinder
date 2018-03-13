package net.coderodde.finance.loan.support;

import net.coderodde.finance.loan.Actor;

/**
 * This class implements a heap node for holding the search state.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 11, 2018)
 */
final class HeapNode<I> implements Comparable<HeapNode<I>> {
    
    /**
     * Borrowing actor.
     */
    private final Actor<I> targetActor;
    
    /**
     * Lending actor.
     */
    private final Actor<I> sourceActor;
    
    /**
     * Effective interest rate.
     */
    private final double effectiveInterestRate;
    
    HeapNode(Actor<I> sourceActor,
             Actor<I> targetActor,
             double effectiveInterestRate) {
        this.sourceActor = sourceActor;
        this.targetActor = targetActor;
        this.effectiveInterestRate = effectiveInterestRate;
    }
    
    Actor<I> getSourceActor() {
        return sourceActor;
    }
        
    Actor<I> getTargetActor() {
        return targetActor;
    }
    
    double getEffectiveInterestRate() {
        return effectiveInterestRate;
    }

    @Override
    public int compareTo(HeapNode<I> o) {
        return Double.compare(effectiveInterestRate,
                              o.getEffectiveInterestRate());
    }
}
