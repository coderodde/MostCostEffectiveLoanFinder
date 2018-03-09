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
 * This class implements the default most cost effective loan finder using a 
 * binary heap.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 1, 2018)
 * @param <I> the actor identity type.
 */
public final class BinaryHeapMostCostEffectiveLoanFinder<I>
        implements MostCostEffectiveLoanFinder<I> {

    /**
     * {@inheritDoc } 
     */
    @Override
    public MostCostEffectiveLoan<I> findLenders(Actor<I> actor, 
                                                double requestedPotential,
                                                double maximumInterestRate) {
        System.out.println("Binary");
        // Sanity checks:
        checkActorBelongsToGraph(actor);
        Utils.checkRequestedPotential(requestedPotential);
        ActorGraph<I> actorGraph = actor.getActorGraph();
        
        // Algorithm state:
        Queue<HeapNode<I>> open = new PriorityQueue<>();
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
            Actor<I> currentActor = currentHeapNode.getActor();
            Actor<I> previousActor = currentHeapNode.getPreviousActor();
            double effectiveInterestRate =
                    currentHeapNode.getEffectiveInterestRate();
            double potentialIncrease = 
                    Math.min(actorGraph.getActorPotential(currentActor),
                             requestedPotential - currentPrincipal);

            currentPrincipal += potentialIncrease;
            solutionPotentialFunction.put(currentActor, potentialIncrease);
            directionFunction.put(currentActor, previousActor);
            closed.add(currentActor);
            
            for (Actor<I> lendingActor :
                    actorGraph.getIncomingArcs(currentActor)) {
                if (!closed.contains(lendingActor)) {
                    double nextInterestRate = 
                            combineInterestRates(
                                    effectiveInterestRate,
                                    actorGraph.getInterestRate(lendingActor, 
                                                               currentActor));

                    if (nextInterestRate <= maximumInterestRate) {
                        open.add(new HeapNode<>(lendingActor,
                                                currentActor, 
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
    
    private static final class HeapNode<I> implements Comparable<HeapNode<I>> {

        private final Actor<I> actor;
        private final Actor<I> previousActor;
        private final double effectiveInterestRate;
        
        HeapNode(Actor<I> actor, 
                 Actor<I> previousActor,
                 double effectiveInterestRate) {
            this.actor = actor;
            this.previousActor = previousActor;
            this.effectiveInterestRate = effectiveInterestRate;
        }
        
        Actor<I> getActor() {
            return actor;
        }
        
        Actor<I> getPreviousActor() {
            return previousActor;
        }
        
        double getEffectiveInterestRate() {
            return effectiveInterestRate;
        }
        
        @Override
        public int compareTo(HeapNode<I> o) {
            return Double.compare(effectiveInterestRate, 
                                  o.effectiveInterestRate);
        }
    }

    private void checkActorBelongsToGraph(Actor<I> actor) {
        if (actor.getActorGraph() == null) {
            throw new IllegalStateException(
                    "The input actor does not belong to an actor graph.");
        }
    }
}
