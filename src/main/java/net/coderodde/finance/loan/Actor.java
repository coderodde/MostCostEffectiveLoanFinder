package net.coderodde.finance.loan;

import java.util.Objects;

/**
 * This class implements an actor in a loan graph. Each actor can interact with
 * other actors. These relationships are implemented via actor graph directed
 * arcs. Each actor can have identity that might be name, ID, or any other 
 * token.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 1, 2018)
 * @param <I> the identity type.
 */
public final class Actor<I> {
    
    /**
     * The identity of this actor.
     */
    private final I identity;
    
    /**
     * The actor graph this actor belongs to.
     */
    private ActorGraph<I> ownerGraph;
    
    public Actor(I identity) {
        this.identity = Objects.requireNonNull(identity,
                                               "The input identity is null.");
    }
    
    public ActorGraph<I> getActorGraph() {
        return ownerGraph;
    }
    
    public I getIdentity() {
        return identity;
    }
    
    @Override
    public String toString() {
        return "[Actor, " + identity.toString() + "]";
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(identity);
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        
        if (other == this) {
            return true;
        }
        
        if (!getClass().equals(other.getClass())) {
            return false;
        }
        
        Actor o = (Actor) other;
        return getIdentity().equals(o.getIdentity());
    }
    
    void setOwnerActorGraph(ActorGraph<I> ownerGraph) {
        this.ownerGraph = ownerGraph;
    }
}
