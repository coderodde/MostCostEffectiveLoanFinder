package net.coderodde.finance.loan;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static net.coderodde.finance.loan.Utils.checkPotential;

/**
 * This class implements a directed actor graph.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 1, 2018)
 * @param <I> the actor identity type.
 */
public final class ActorGraph<I> {
    
    /**
     * This map maps each actor node in the graph to the maximum number of 
     * resource units the node can lend. By definition, this value cannot be 
     * less than zero.
     */
    private final Map<Actor<I>, Double> potentialMap = new HashMap<>();
    
    /**
     * Maps each arc in the actor graph to interest rate the tail actor can
     * offer to the head actor.
     */
    private final Map<Actor<I>, Map<Actor<I>, Double>> 
            interestRateMap = new HashMap<>();
    
    /**
     * Maps each actor <code>A</code> to the list of all other actors which have
     * incoming arcs to <code>A</code>.
     */
    private final Map<Actor<I>, Set<Actor<I>>> incomingActors = 
            new HashMap<>();
    
    /**
     * Caches the current number of arcs in this actor graph.
     */
    private int numberOfArcs;
    
    /**
     * Returns the number of actors present in this actor graph.
     * 
     * @return the number of actors.
     */
    public int getNumberOfActors() {
        return potentialMap.size();
    }
    
    /**
     * Returns the number of arcs in this actor graph.
     * 
     * @return the number of arcs. 
     */
    public int getNumberOfArcs() {
        return numberOfArcs;
    }
    
    /**
     * Adds a new actor to the graph with a specified potential. If the input 
     * actor is already in this graph, the potential and maximum interest rate
     * are updated.
     * 
     * @param actor               a new actor.
     * @param potential           the potential of the actor.
     */
    public void addActor(Actor<I> actor, double potential) {
        Objects.requireNonNull(actor, "The input actor is null.");
        potentialMap.put(actor, checkPotential(potential));
        
        if (!actorBelongsToThisGraph(actor)) {
            if (actorBelongsToOtherGraph(actor)) {
                // If the input actor belongs to another graph, we need to 
                // disconnect it from there:
                actor.getActorGraph().removeActor(actor);
            }
            
            actor.setOwnerActorGraph(this);
            interestRateMap.put(actor, new HashMap<>());
            incomingActors.put(actor, new HashSet<>());
        }
    }
    
    /**
     * Removes the actor and completely disconnects it from this graph.
     * 
     * @param actor the actor to remove.
     */
    public void removeActor(Actor<I> actor) {
        Objects.requireNonNull(actor, "The input actor is null.");
        potentialMap.remove(actor);
        numberOfArcs -= incomingActors.get(actor).size();
        numberOfArcs -= interestRateMap.get(actor).size();
        
        for (Actor<I> incomingActor : incomingActors.get(actor)) {
            interestRateMap.get(incomingActor).remove(actor);
        }
        
        for (Actor<I> outgoingActor : interestRateMap.get(actor).keySet()) {
            incomingActors.get(outgoingActor).remove(actor);
        }
        
        interestRateMap.remove(actor);
        incomingActors.remove(actor);
        actor.setOwnerActorGraph(null);
    }
    
    /**
     * Adds a new actor arc to this actor graph with a specified interest rate.
     * 
     * @param sourceActor  the loan source actor.
     * @param targetActor  the loan target actor.
     * @param interestRate the interest rate {@code sourceActor} can offer to 
     *                     {@code targetActor}.
     */
    public void addArc(Actor<I> sourceActor, 
                       Actor<I> targetActor, 
                       double interestRate) {
        checkArc(sourceActor, targetActor);
        checkNotSelfLoop(sourceActor, targetActor);
        
        if (!interestRateMap.get(sourceActor).containsKey(targetActor)) {
            // Once here, the input arc does not exist in this graph so 
            // increment the number of arcs counter.
            numberOfArcs++;
        }
        
        interestRateMap.get(sourceActor)
                       .put(targetActor, 
                            Utils.checkInterestRate(interestRate));
        incomingActors.get(targetActor).add(sourceActor);
    }
    
    /**
     * Checks whether the given arc is in this graph.
     * 
     * @param sourceActor
     * @param targetActor
     * @return {@code true} only if there is a an arc from {@code sourceActor} 
     *         to {@code targetActor}.
     */
    public boolean hasArc(Actor<I> sourceActor, Actor<I> targetActor) {
        checkArc(sourceActor, targetActor);
        return interestRateMap.get(sourceActor).containsKey(targetActor);
    }
    
    /**
     * Makes sure the arc <code>(sourceActor, targetActor)</code> does not
     * appear in this graph.
     * 
     * @param sourceActor the tail actor.
     * @param targetActor the head actor.
     */
    public void removeArc(Actor<I> sourceActor, Actor<I> targetActor) {
        checkArc(sourceActor, targetActor);
        
        if (interestRateMap.get(sourceActor).containsKey(targetActor)) {
            numberOfArcs--;
            interestRateMap.get(sourceActor).remove(targetActor);
            incomingActors.get(targetActor).remove(sourceActor);
        }
    }
    
    /**
     * Clears this graph.
     */
    public void clear() {
        potentialMap.clear();
        interestRateMap.clear();
        incomingActors.clear();
    }
    
    /**
     * Returns a set view of incoming actors.
     * 
     * @param actor the target actor.
     * @return a set view of incoming actors.
     */
    public Set<Actor<I>> getIncomingArcs(Actor<I> actor) {
        Objects.requireNonNull(actor, "The input actor is null.");
        checkActorIsInGraph(
                actor,            
                "The input actor (" + actor + ") is not in this graph.");
        return Collections.unmodifiableSet(incomingActors.get(actor));
    }
    
    /**
     * Returns a set view of outgoing actors.
     * 
     * @param actor the target actor.
     * @return a set view of outgoing actors.
     */
    public Set<Actor<I>> getOutgoingArcs(Actor<I> actor) {
        Objects.requireNonNull(
                actor,
                "The input actor (" + actor + ") is not in this graph.");
        checkActorIsInGraph(
                actor, 
                "The input actor (" + actor + ") is not in this graph.");
        return Collections.unmodifiableSet(interestRateMap.get(actor).keySet());
    }
    
    /**
     * Returns the interest rate {@code sourceActor} can offer to 
     * {@code targetActor}. 
     * 
     * @param sourceActor the lender actor.
     * @param targetActor the actor receiving the actor.
     * @return the interest rate offered to {@code sourceActor} to 
     *         {@code targetActor}.
     */
    public double getInterestRate(Actor<I> sourceActor, Actor<I> targetActor) {
        Objects.requireNonNull(sourceActor, "The input source actor is null.");
        Objects.requireNonNull(targetActor, "The input target actor is null.");
        checkActorIsInGraph(sourceActor, 
                            "The source actor is not in this graph.");
        checkActorIsInGraph(targetActor,
                            "The target actor is not in this graph.");
        checkArcExists(sourceActor, targetActor);
        return interestRateMap.get(sourceActor).get(targetActor);
    }
    
    /**
     * Returns the potential of an input actor.
     * 
     * @param actor the target actor.
     * @return the potential of an input actor.
     */
    public double getActorPotential(Actor<I> actor) {
        Objects.requireNonNull(actor, "The input actor is null.");
        checkActorIsInGraph(actor, "The input actor is not in this graph.");
        return potentialMap.get(actor);
    }
    
    /**
     * Checks that the given actors are not {@code null}, and that both the 
     * input actors are in this graph.
     * 
     * @param sourceActor the source actor.
     * @param targetActor the target actor.
     */
    private void checkArc(Actor<I> sourceActor, Actor<I> targetActor) {
        Objects.requireNonNull(sourceActor, "The source actor is null.");
        Objects.requireNonNull(targetActor, "The target actor is null.");
        checkActorIsInGraph(
                sourceActor,
                "The input source actor (" + sourceActor 
                                           + ") is not in this graph.");
        checkActorIsInGraph(
                targetActor, 
                "The input target actor (" + targetActor 
                                           + ") is not in this graph.");
    }
    
    // Returns true only if the input actor belongs to this graph:
    private boolean actorBelongsToThisGraph(Actor<I> actor) {
        return actor.getActorGraph() == this;
    }
    
    // Returns true only if the input graph does not belong to this graph and
    // its owner graph is not set to null:
    private boolean actorBelongsToOtherGraph(Actor<I> actor) {
        return !actorBelongsToThisGraph(actor) && actor.getActorGraph() != null;
    }
    
    // This validation method is called only after we make sure the two input
    // actors are in the graph, so this one simply checks that there is an arc
    // from the source actor to the target actor.
    private void checkArcExists(Actor<I> sourceActor, Actor<I> targetActor) {
        if (!interestRateMap.get(sourceActor).containsKey(targetActor)) {
            throw new IllegalStateException(
                    "The input arc (" + sourceActor + ", " + targetActor +
                    ") is not in this graph.");
        }
    }
    
    // Makes sure the input actor is in this graph.
    private void checkActorIsInGraph(Actor<I> actor, String errorMessage) {
        if (actor.getActorGraph() != this) {
            throw new IllegalStateException(errorMessage);
        }
    }
    
    // Makes sure that the two input actors are not same.
    private void checkNotSelfLoop(Actor<I> actor1, Actor<I> actor2) {
        if (actor1.equals(actor2)) {
            throw new IllegalArgumentException(
                    "Self-loops are not allowed. Trying to create a " + 
                    "self-loop for " + actor1 + ".");
        }
    }
}
