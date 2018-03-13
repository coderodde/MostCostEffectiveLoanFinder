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
import net.coderodde.finance.loan.Utils;

/**
 * This abstract class implements a preprocessing most cost-effective finders.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 13, 2018)
 * @param <I> the actor identity type.
 */
abstract class AbstractPreprocessingMostCostEffectiveLoanFinder<I> 
extends AbstractMostCostEffectiveLoanFinderBase
implements MostCostEffectiveLoanFinder<I> {

    /**
     * The actor graph being preprocessed.
     */
    private final ActorGraph<I> actorGraph;
    
    /**
     * The actual priority queue implementation.
     */
    private final Queue<HeapNode<I>> open;
    
    /**
     * Maps each actor to the list head of its lender list.
     */
    private final Map<Actor<I>, LenderListNode<I>> actorToLenderListHeadMap;
    
    protected AbstractPreprocessingMostCostEffectiveLoanFinder(
            ActorGraph<I> actorGraph,
            Queue<HeapNode<I>> open) {
        this.actorGraph = actorGraph;
        this.open = open;
        this.actorToLenderListHeadMap = 
                new HashMap<>(actorGraph.getNumberOfActors());
        preprocessGraph();
    }
    
    private void preprocessGraph() {
        for (Actor<I> startingActor : actorGraph.getActorSet()) {
            preprocessSingleActor(startingActor);
        }
    }
    
    private void preprocessSingleActor(Actor<I> startingActor) {
        open.clear();
        Set<Actor<I>> closed = new HashSet<>();
        LenderListNode<I> lastLenderListNode = null;
        
        // Priority queue initialization:
        for (Actor<I> sourceActor : actorGraph.getIncomingArcs(startingActor)) {
            open.add(new HeapNode<>(
                        sourceActor, 
                        null, 
                        actorGraph.getInterestRate(sourceActor, 
                                                   startingActor)));
        }
        
        // The actual search:
        while (!open.isEmpty()) {
            HeapNode<I> currentHeapNode = open.remove();
            Actor<I> currentSourceActor = currentHeapNode.getSourceActor();
            double effectiveInterestRate = 
                    currentHeapNode.getEffectiveInterestRate();
            
            closed.add(currentSourceActor);
            
            if (lastLenderListNode == null) {
                lastLenderListNode = 
                        new LenderListNode<>(currentSourceActor,
                                             effectiveInterestRate);
                actorToLenderListHeadMap.put(currentSourceActor,
                                             lastLenderListNode);
            } else {
                LenderListNode lenderListNode = 
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
                                            null,
                                            nextInterestRate));
                }
            }
        }
    }
    
    @Override
    public MostCostEffectiveLoan<I> findLenders(Actor<I> actor, 
                                                double requestedPotential,
                                                double maximumInterestRate) {
        Objects.requireNonNull(actor, "The input actor is null.");
        checkActorBelongsToGraph(actor);
        Utils.checkRequestedPotential(requestedPotential);
        Utils.checkMaximumInterestRate(maximumInterestRate);
        
        Map<Actor<I>, Double> solutionPotentialFunction = new HashMap<>();
        Map<Actor<I>, Actor<I>> directionFunction = new HashMap<>();
        double collectedPrincipal = 0.0;
        Actor<I> previousActor = actor;
        
        for (LenderListNode<I> node = actorToLenderListHeadMap.get(actor);
                node != null && collectedPrincipal < requestedPotential;
                node = node.getNextLenderListNode()) {
            Actor<I> lender = node.getActor();
            double potentialIncrease =
                    Math.min(requestedPotential - collectedPrincipal,
                             actor.getActorGraph().getActorPotential(lender));
            collectedPrincipal += potentialIncrease;
            solutionPotentialFunction.put(lender, potentialIncrease);
            directionFunction.put(lender, previousActor);
            previousActor = lender;
        }
        
        return new MostCostEffectiveLoan<>(actor,
                                           collectedPrincipal,
                                           requestedPotential,
                                           maximumInterestRate,
                                           solutionPotentialFunction,
                                           directionFunction);
    }
    
    private void checkActorBelongsToGraph(Actor<I> actor) {
        if (actorGraph != actor.getActorGraph()) {
            throw new IllegalStateException(
                    "The input actor does not belong to the " +
                    "preprocessed graph.");
        }
    }
    
    /**
     * This inner static class implements a singly-linked list of lenders that
     * is sorted by effective interest rates with the head of the list having 
     * the smallest effective interest rate.
     * 
     * @param <I> the actor identity type.
     */
    private static final class LenderListNode<I> {
        
        /**
         * The lending actor.
         */
        private final Actor<I> actor;
        
        /**
         * The effective interest rate the lending actor can offer.
         */
        private final double effectiveInterestRate;
        
        /**
         * The next most cost-effective lender.
         */
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
