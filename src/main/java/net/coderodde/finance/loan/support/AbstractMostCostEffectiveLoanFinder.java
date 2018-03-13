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
 * This abstract class implements the main logic of most cost-effective loan 
 * finders.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 13, 2018)
 * @param <I> the actor identity type.
 */
abstract class AbstractMostCostEffectiveLoanFinder<I> 
        extends AbstractMostCostEffectiveLoanFinderBase
        implements MostCostEffectiveLoanFinder<I> {
    
    private final Queue<HeapNode<I>> open;
    
    protected AbstractMostCostEffectiveLoanFinder(Queue<HeapNode<I>> open) {
        this.open = open;
    }
    
    @Override
    public MostCostEffectiveLoan<I> findLenders(Actor<I> actor,
                                                double requestedPotential,
                                                double maximumInterestRate) {
        // Sanity checks:
        Objects.requireNonNull(actor, "The input actor is null.");
        checkActorBelongsToGraph(actor);
        Utils.checkRequestedPotential(requestedPotential);
        Utils.checkMaximumInterestRate(maximumInterestRate);
        ActorGraph<I> actorGraph = actor.getActorGraph();
        
        // Algorithm state:
        Set<Actor<I>> closed = new HashSet<>();
        Map<Actor<I>, Double> solutionPotentialFunction = new HashMap<>();
        Map<Actor<I>, Actor<I>> directionFunction = new HashMap<>();
        double collectedPrincipal = 0.0;
        
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
        
        while (!open.isEmpty() && collectedPrincipal < requestedPotential) {
            HeapNode<I> currentHeapNode = open.remove();
            Actor<I> targetActor = currentHeapNode.getTargetActor();
            Actor<I> sourceActor = currentHeapNode.getSourceActor();
            double effectiveInterestRate =
                    currentHeapNode.getEffectiveInterestRate();
            double potentialIncrease = 
                    Math.min(actorGraph.getActorPotential(sourceActor),
                             requestedPotential - collectedPrincipal);

            collectedPrincipal += potentialIncrease;
            solutionPotentialFunction.put(sourceActor, potentialIncrease);
            directionFunction.put(sourceActor, targetActor);
            closed.add(sourceActor);
            
            for (Actor<I> lendingActor :
                    actorGraph.getIncomingArcs(sourceActor)) {
                if (!closed.contains(lendingActor)) {
                    double nextInterestRate = 
                            combineInterestRates(
                                    effectiveInterestRate,
                                    actorGraph.getInterestRate(lendingActor, 
                                                               sourceActor));

                    if (nextInterestRate <= maximumInterestRate) {
                        open.add(new HeapNode<>(lendingActor,
                                                sourceActor, 
                                                nextInterestRate));
                    }
                }
            }
        }
        
        return new MostCostEffectiveLoan<>(actor,
                                           collectedPrincipal,
                                           requestedPotential,
                                           maximumInterestRate,
                                           solutionPotentialFunction,
                                           directionFunction);
    }
    
    /**
     * Checks whether the input actor belongs to an actor graph.
     * 
     * @param actor the actor to check.
     * @throws IllegalStateException if the input actor does not belong to an
     *                               actor graph.
     */
    private void checkActorBelongsToGraph(Actor<I> actor) {
        if (actor.getActorGraph() == null) {
            throw new IllegalStateException(
                    "The input actor does not belong to an actor graph.");
        }
    }
}
