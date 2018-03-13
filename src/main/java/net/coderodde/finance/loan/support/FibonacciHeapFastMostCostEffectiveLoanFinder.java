package net.coderodde.finance.loan.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
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
     * Maps an actor to a sorted linked list of most cost-effective lenders.
     */
    private final Map<Actor<I>, LenderListNode<I>> actorToLenderListHeadMap;
    
    public FibonacciHeapFastMostCostEffectiveLoanFinder(
            ActorGraph<I> actorGraph) {
        this.actorGraph = 
                Objects.requireNonNull(
                        actorGraph, 
                        "The input actor graph is null.");
        
        int actorGraphSize = actorGraph.getNumberOfActors();
        this.actorToLenderListHeadMap = new HashMap<>(actorGraphSize);
        populateEffectiveInterestRateLists();
    }
    
    @Override
    public MostCostEffectiveLoan<I>
          findLenders(Actor<I> actor, 
                      double requiredPrincipal, 
                      double maximumInterestRate) {
        double collectedPotential = 0.0;
        
        
        return null;
    }
    
    private void populateEffectiveInterestRateLists() {
        for (Actor<I> targetActor : actorGraph.getActorSet()) {
            computeEffectiveInterestRateList(targetActor);
        }
    }
    
    /**
     * Computes an entire loan tree leading to {@code startActor}.
     * 
     * @param borrowingActor the borrowing actor. 
     */
    private void computeEffectiveInterestRateList(Actor<I> targetActor) {
        Queue<HeapNode<I>> open = new FibonacciHeap<>();
        Set<Actor<I>> closed = new HashSet<>();
        LenderListNode<I> lastLenderListNode = null;
        
        // Priority queue initialization:
        for (Actor<I> sourceActor 
                : actorGraph.getIncomingArcs(targetActor)) {
            open.add(new HeapNode<>(sourceActor,
                                    targetActor, 
                                    actorGraph
                                        .getInterestRate(sourceActor,
                                                         targetActor)));
        }
        
        // The actual search:
        while (!open.isEmpty()) {
            HeapNode<I> currentHeapNode = open.remove();
            Actor<I> currentSourceActor = currentHeapNode.getSourceActor();
            
            double effectiveInterestRate = 
                    currentHeapNode.getEffectiveInterestRate();
            closed.add(targetActor);
            
            if (lastLenderListNode == null) {
                lastLenderListNode = 
                        new LenderListNode<>(currentSourceActor,
                                             effectiveInterestRate);
                
                actorToLenderListHeadMap.put(targetActor, lastLenderListNode);
            } else {
                LenderListNode<I> lenderListNode = 
                        new LenderListNode<>(currentSourceActor, 
                                             effectiveInterestRate);
                
                lastLenderListNode.setNextLenderListNode(lenderListNode);
                lastLenderListNode = lenderListNode;
            }
            
            for (Actor<I> lendingActor 
                    : actorGraph.getIncomingArcs(currentSourceActor)) {
                if (!closed.contains(lendingActor)) {
                    double nextInterestRate = 
                            combineInterestRates(
                                    effectiveInterestRate,
                                    actorGraph.getInterestRate(
                                            lendingActor, 
                                            currentSourceActor));
                    
                    open.add(new HeapNode<>(lendingActor, 
                                            currentSourceActor, 
                                            nextInterestRate));
                }
            }
        }
    }
    
    private static double combineInterestRates(double interestRate1,
                                               double interestRate2) {
        return interestRate1 + interestRate2 + interestRate1 * interestRate2;
    }
    
    private static final class LenderListNode<I> {
        private final Actor<I> actor;
        private final double effectiveInterestRate;
        private LenderListNode<I> nextLenderListNode;
        
        LenderListNode(Actor<I> actor, double effectiveInterestRate) {
            this.actor = actor;
            this.effectiveInterestRate = effectiveInterestRate;
        }
        
        void setNextLenderListNode(LenderListNode<I> nextLenderListNode) {
            this.nextLenderListNode = nextLenderListNode;
        }
        
        Actor<I> getActor() {
            return actor;
        }
        
        double getEffectiveInterestRate() {
            return effectiveInterestRate;
        }
        
        LenderListNode<I> getNextLenderListNode() {
            return nextLenderListNode;
        }
    }
}
