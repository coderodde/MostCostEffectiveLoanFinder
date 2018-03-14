package net.coderodde.finance.loan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.coderodde.finance.loan.support.BinaryHeapMostCostEffectiveLoanFinder;
import net.coderodde.finance.loan.support.BinaryHeapPreprocessingMostCostEffectiveLoanFinder;
import net.coderodde.finance.loan.support.FibonacciHeapPreprocessingMostCostEffectiveLoanFinder;

/**
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 14, 2018)
 */
public final class Main {
    
    private static final int ACTORS = 1000;
    private static final int ARCS = 10_000;
    private static final double MAXIMUM_POTENTIAL = 100.0;
    private static final double MAXIMUM_INTEREST_RATE = 0.1;
    private static final double REQUIRED_POTENTIAL = 1000.0;
    private static final double REQUIRED_MAXIMUM_INTEREST_RATE = 2.0;
    
    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        ActorGraph<Integer> actorGraph = 
                createRandomActorGraph(ACTORS,
                                       ARCS, 
                                       MAXIMUM_POTENTIAL,
                                       MAXIMUM_INTEREST_RATE, 
                                       random);
        Actor<Integer> startingActor =
                choose(new ArrayList<>(actorGraph.getActorSet()), 
                       random);
        
        System.out.println("Seed = " + seed);
        System.out.println("Starting actor: " + startingActor);
        
        long startTime = System.currentTimeMillis();
        MostCostEffectiveLoanFinder<Integer> finder =
                new BinaryHeapPreprocessingMostCostEffectiveLoanFinder<>(
                        actorGraph);
        long endTime = System.currentTimeMillis();
        
        System.out.println(
                "Preprocessing took " + (endTime - startTime) + " ms.");
        
        startTime = System.currentTimeMillis();
        MostCostEffectiveLoan<Integer> loan = 
                finder.findLenders(startingActor,
                                   REQUIRED_POTENTIAL,
                                   REQUIRED_MAXIMUM_INTEREST_RATE);
        endTime = System.currentTimeMillis();
        
        System.out.println("Query took " + (endTime - startTime) + " ms.");
        
        startTime = System.currentTimeMillis();
        MostCostEffectiveLoan<Integer> loan2 = 
                new BinaryHeapMostCostEffectiveLoanFinder<Integer>()
                        .findLenders(startingActor,
                                     REQUIRED_POTENTIAL, 
                                     REQUIRED_MAXIMUM_INTEREST_RATE);
        endTime = System.currentTimeMillis();
        System.out.println(
                "Non-preprocessed query took " + (endTime - startTime) 
                                               + " ms.");
        System.out.println("Loan: " + loan);
        System.out.println("Loan2: " + loan2);
    }
    
    private static ActorGraph<Integer> 
        createRandomActorGraph(int actors,
                               int arcs,
                               double maxPotential,
                               double maxInterestRate,
                               Random random) {
        ActorGraph<Integer> actorGraph = new ActorGraph<>();
        List<Actor<Integer>> actorList = new ArrayList<>();
        
        for (int id = 0; id < actors; id++) {
            Actor<Integer> actor = new Actor<>(id);
            actorList.add(actor);
            actorGraph.addActor(actor, maxPotential * random.nextDouble());
        }
        
        while (actorGraph.getNumberOfArcs() < arcs) {
            Actor<Integer> sourceActor = choose(actorList, random);
            Actor<Integer> targetActor = choose(actorList, random);
            
            if (!sourceActor.equals(targetActor)) {
                double interestRate = maxInterestRate * random.nextDouble();
                actorGraph.addArc(sourceActor, targetActor, interestRate);
            }
        }
        
        return actorGraph;
    }
        
    private static <T> T choose(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
}
