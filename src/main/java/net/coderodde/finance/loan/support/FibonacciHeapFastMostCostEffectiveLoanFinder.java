package net.coderodde.finance.loan.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.coderodde.finance.loan.Actor;
import net.coderodde.finance.loan.ActorGraph;
import net.coderodde.finance.loan.MostCostEffectiveLoan;
import net.coderodde.finance.loan.MostCostEffectiveLoanFinder;

/**
 * This class preprocesses the actor graph in order to answer the loan queries
 * much faster.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 11, 2018)
 */
public final class FibonacciHeapFastMostCostEffectiveLoanFinder<I> 
        implements MostCostEffectiveLoanFinder<I> {

    /**
     * The actor graph to preprocess.
     */
    private ActorGraph<I> actorGraph;
    
    /**
     * The matrix mapping an actor pair <code>(u, v)</code> to the effective
     * interest rate from <code>u</code> to <code>v</code>.
     */
    private final Map<Actor<I>, 
                      Map<Actor<I>, Double>> effectiveInterestRateMatrix;
    
    /**
     * Maps an actor to a sorted linked list of most cost-effective lenders.
     */
    private final Map<Actor<I>, LenderListNode<I>> actorToLenderListMap;
    
    public FibonacciHeapFastMostCostEffectiveLoanFinder(ActorGraph<I> actorGraph) {
        this.actorGraph = 
                Objects.requireNonNull(
                        actorGraph, 
                        "The input actor graph is null.");
        
        int actorGraphSize = actorGraph.getNumberOfActors();
        this.effectiveInterestRateMatrix = new HashMap<>(actorGraphSize);
        this.actorToLenderListMap = new HashMap<>(actorGraphSize);
        createEffectiveInterestRateMatrix();
        populateEffectiveInterestRateMatrix();
    }
    
    @Override
    public MostCostEffectiveLoan<I>
          findLenders(Actor<I> actor, 
                      double requiredPrincipal, 
                      double maximumInterestRate) {
        double collectedPotential = 0.0;
        
        
        return null;
    }
    
    private void createEffectiveInterestRateMatrix() {
        int actorGraphSize = actorGraph.getNumberOfActors();
        
        for (Actor<I> actor1 : actorGraph.getActorSet()) {
            effectiveInterestRateMatrix.put(actor1, 
                                            new HashMap<>(actorGraphSize));
            
            for (Actor<I> actor2 : actorGraph.getActorSet()) {
                effectiveInterestRateMatrix.get(actor1).put(actor2, 0.0);
            }
        }
    }
    
    private void populateEffectiveInterestRateMatrix() {
        for (Actor<I> targetActor : actorGraph.getActorSet()) {
            computeEffectiveInterestRateTree(targetActor);
        }
    }
    
    /**
     * Computes an entire loan tree leading to {@code startActor}.
     * 
     * @param targetActor the target actor. 
     */
    private void computeEffectiveInterestRateTree(Actor<I> targetActor) {
        
    }
    
    private static final class LenderListNode<I> {
        private final Actor<I> actor;
        private final double effectiveInterestRate;
        private LenderListNode<I> nextInterestRateMatrixEntry;
        
        LenderListNode(Actor<I> actor, double effectiveInterestRate) {
            this.actor = actor;
            this.effectiveInterestRate = effectiveInterestRate;
        }
        
        void setNextInterestRateMatrixEntry(
                LenderListNode<I> nextInterestRateMatrixEntry) {
            this.nextInterestRateMatrixEntry = nextInterestRateMatrixEntry;
        }
        
        Actor<I> getActor() {
            return actor;
        }
        
        double getEffectiveInterestRate() {
            return effectiveInterestRate;
        }
        
        LenderListNode<I> getNextInterestRateMatrixEntry() {
            return nextInterestRateMatrixEntry;
        }
    }
}
