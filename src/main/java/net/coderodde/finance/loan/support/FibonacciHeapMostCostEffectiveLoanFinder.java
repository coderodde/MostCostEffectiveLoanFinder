package net.coderodde.finance.loan.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import net.coderodde.finance.loan.Actor;
import net.coderodde.finance.loan.ActorGraph;
import net.coderodde.finance.loan.MostCostEffectiveLoan;
import net.coderodde.finance.loan.Utils;
import net.coderodde.finance.loan.MostCostEffectiveLoanFinder;

/**
 * This class implements the default most cost effective loan finder using
 * Fibonacci heap.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 1, 2018)
 * @param <I> the actor identity type.
 */
public final class FibonacciHeapMostCostEffectiveLoanFinder<I>
        implements MostCostEffectiveLoanFinder<I> {

    /**
     * {@inheritDoc } 
     */
    @Override
    public MostCostEffectiveLoan<I> findLenders(Actor<I> actor, 
                                                double requestedPotential,
                                                double maximumInterestRate) {
        // Sanity checks:
        checkActorBelongsToGraph(actor);
        Utils.checkRequestedPotential(requestedPotential);
        ActorGraph<I> actorGraph = actor.getActorGraph();
        
        // Algorithm state:
        Queue<HeapNode<I>> open = new FibonacciHeap<>();
        Set<Actor<I>> closed = new HashSet<>();
        Map<Actor<I>, Double> solutionPotentialFunction = new HashMap<>();
        Map<Actor<I>, Actor<I>> directionFunction = new HashMap<>();
        double currentPrincipal = 0.0;
        
        // Loop initialization:
        for (Actor<I> initialIncomingActor 
                : actorGraph.getIncomingArcs(actor)) {
            if (actorGraph.getInterestRate(initialIncomingActor, actor)
                    <= maximumInterestRate) {
                open.add(
                    new HeapNode(
                        initialIncomingActor,
                        actor,
                        actorGraph.getInterestRate(initialIncomingActor, actor)
                    )
                );
            }
        }
        
        while (!open.isEmpty() && currentPrincipal < requestedPotential) {
            HeapNode<I> currentHeapNode = open.remove();
            Actor<I> targetActor = currentHeapNode.getTargetActor();
            Actor<I> sourceActor = currentHeapNode.getSourceActor();
            double effectiveInterestRate =
                    currentHeapNode.getEffectiveInterestRate();
            double potentialIncrease = 
                    Math.min(actorGraph.getActorPotential(targetActor),
                             requestedPotential - currentPrincipal);

            currentPrincipal += potentialIncrease;
            solutionPotentialFunction.put(targetActor, potentialIncrease);
            directionFunction.put(targetActor, sourceActor);
            closed.add(targetActor);
            
            for (Actor<I> lendingActor :
                    actorGraph.getIncomingArcs(targetActor)) {
                if (!closed.contains(lendingActor)) {
                    double nextInterestRate = 
                            combineInterestRates(
                                    effectiveInterestRate,
                                    actorGraph.getInterestRate(lendingActor, 
                                                               targetActor));

                    if (nextInterestRate <= maximumInterestRate) {
                        open.add(new HeapNode<>(lendingActor,
                                                targetActor, 
                                                nextInterestRate));
                    }
                }
            }
        }
        
        return new MostCostEffectiveLoan<>(
                actor,
                currentPrincipal,
                requestedPotential,
                maximumInterestRate,
                solutionPotentialFunction,
                directionFunction);
    }
    
    private double combineInterestRates(double interestRate1,
                                        double interestRate2) {
        return interestRate1 + interestRate2 + interestRate1 * interestRate2;
    }

    private void checkActorBelongsToGraph(Actor<I> actor) {
        if (actor.getActorGraph() == null) {
            throw new IllegalStateException(
                    "The input actor does not belong to an actor graph.");
        }
    }
}
