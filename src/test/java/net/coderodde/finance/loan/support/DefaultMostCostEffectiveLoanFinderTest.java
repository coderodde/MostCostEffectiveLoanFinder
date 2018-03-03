package net.coderodde.finance.loan.support;

import net.coderodde.finance.loan.Actor;
import net.coderodde.finance.loan.ActorGraph;
import net.coderodde.finance.loan.MostCostEffectiveLoan;
import org.junit.Test;

/**
 * Tests
 * {@link net.coderodde.finance.loan.support.DefaultMostCostEffectiveLoanFinder}
 * .
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 2, 2018)
 */
public class DefaultMostCostEffectiveLoanFinderTest {
    
    @Test
    public void test1() {
        ActorGraph<String> graph = new ActorGraph<>();
        Actor<String> actorA = new Actor<>("A");
        Actor<String> actorB = new Actor<>("B");
        Actor<String> actorC = new Actor<>("C");
        Actor<String> actorD = new Actor<>("D");
        
        graph.addActor(actorA, 0);
        graph.addActor(actorB, 10);
        graph.addActor(actorC, 20);
        graph.addActor(actorD, 15);
        
        graph.addArc(actorB, actorD, 0.05);
        graph.addArc(actorD, actorC, 0.2);
        graph.addArc(actorC, actorB, 0.15);
        graph.addArc(actorB, actorA, 0.1);
        
        DefaultMostCostEffectiveLoanFinder<String> finder = 
                new DefaultMostCostEffectiveLoanFinder<>();
        
        MostCostEffectiveLoan<String> loan = 
                finder.findLenders(actorA, 35.0, 0.6);
        
        System.out.println(loan);
    }   
}
