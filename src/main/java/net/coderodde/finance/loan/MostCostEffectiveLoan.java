package net.coderodde.finance.loan;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class implements a most cost-effective loan.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 2, 2018)
 * @param <I> the actor identity type.
 */
public final class MostCostEffectiveLoan<I> {
    
    /**
     * The actor requesting actor.
     */
    private final Actor<I> lenderActor;
    
    /**
     * The actual potential the target actor can lend. The value of this field
     * may be smaller than {@code requestedPotential} in case the requested 
     * potential is too large and/or the graph is not sufficiently large.
     */
    private final double potential;
    
    /**
     * The requested potential. The value of this field may be larger than the 
     * value of {@code potential} in case the requested potential is too large
     * and/or the graph is not sufficiently large.
     */
    private final double requestedPotential;
    
    /**
     * The maximum interest rate the actor can afford.
     */
    private final double maximumInterestRate;
    
    /**
     * Maps each relevant actor to the potential he/she issued to the lender 
     * actor directly or indirectly.
     */
    private Map<Actor<I>, Double> potentialMap = new HashMap<>();
    
    /**
     * Maps each relevant actor to another actor to whom he issued some 
     * potential.
     */
    private Map<Actor<I>, Actor<I>> directionMap = new HashMap<>();

    public MostCostEffectiveLoan(Actor<I> lenderActor,
                                 double potential,
                                 double requestedPotential,
                                 double maximumInterestRate,
                                 Map<Actor<I>, Double> potentialMap,
                                 Map<Actor<I>, Actor<I>> directionMap) {
        this.lenderActor =
                Objects.requireNonNull(lenderActor, 
                                       "The input lender actor is null.");
        this.potential = Utils.checkPotential(potential);
        this.requestedPotential = 
                Utils.checkRequestedPotential(requestedPotential);
        this.maximumInterestRate =
                Utils.checkMaximumInterestRate(maximumInterestRate);
        this.potentialMap = new HashMap<>(potentialMap);
        this.directionMap = new HashMap<>(directionMap);
    }
    
    public Actor<I> getLenderActor() {
        return lenderActor;
    }
    
    public double getReceivedPotential() {
        return potential;
    }
    
    public double getRequestedPotential() {
        return requestedPotential;
    }
    
    public double getMaximumInterestRate() {
        return maximumInterestRate;
    }
    
    public Map<Actor<I>, Double> getPotentialMapView() {
        return Collections.unmodifiableMap(potentialMap);
    }
    
    public Map<Actor<I>, Actor<I>> getDirectionMap() {
        return Collections.unmodifiableMap(directionMap);
    }
    
    // Used for unit testing.
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (o == this) {
            return true;
        }
        
        if (!getClass().equals(o.getClass())) {
            return false;
        }
        
        MostCostEffectiveLoan<I> other = (MostCostEffectiveLoan<I>) o;
        
        return getLenderActor().equals(other.getLenderActor())
                && getReceivedPotential() == other.getReceivedPotential() 
                && getRequestedPotential() == other.getRequestedPotential()
                && getMaximumInterestRate() == other.getMaximumInterestRate()
                && potentialMap.equals(other.potentialMap)
                && directionMap.equals(other.directionMap);
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = 
                new StringBuilder("[Loan, actor = ")
                .append(lenderActor)
                .append(",\npotential = ")
                .append(potential)
                .append(",\nrequested potential = ")
                .append(requestedPotential)
                .append(",\nmaximum interest rate = ")
                .append(maximumInterestRate)
                .append(",\npotentials:");
        
        for (Map.Entry<Actor<I>, Double> entry : potentialMap.entrySet()) {
            stringBuilder.append("\n")
                         .append(entry.getKey())
                         .append(" -> ")
                         .append(entry.getValue());
        }
                
        for (Map.Entry<Actor<I>, Actor<I>> entry : directionMap.entrySet()) {
            stringBuilder.append("\n")
                         .append(entry.getKey())
                         .append(" -> ")
                         .append(entry.getValue());
        }
                
        return stringBuilder.append("]").toString();
    }
}
