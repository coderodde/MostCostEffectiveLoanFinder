package net.coderodde.finance.loan.support;

import net.coderodde.finance.loan.Actor;
import net.coderodde.finance.loan.ActorGraph;
import net.coderodde.finance.loan.MostCostEffectiveLoan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
    
    private static final double EPSILON = 0.001;
    
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
        
        assertEquals(35.0, loan.getReceivedPotential(), EPSILON);
        assertEquals(35.0, loan.getRequestedPotential(), EPSILON);
        
        assertEquals(10.0, loan.getPotentialMapView().get(actorB), EPSILON);
        assertEquals(20.0, loan.getPotentialMapView().get(actorC), EPSILON);
        assertEquals(5.0,  loan.getPotentialMapView().get(actorD), EPSILON);
        
        assertEquals(actorA, loan.getDirectionMap().get(actorB));
        assertEquals(actorB, loan.getDirectionMap().get(actorC));
        assertEquals(actorC, loan.getDirectionMap().get(actorD));
        
        loan = finder.findLenders(actorA, 35.0, 0.5);
        
        assertEquals(30.0, loan.getReceivedPotential(), EPSILON);
        assertEquals(35.0, loan.getRequestedPotential(), EPSILON);
        
        assertEquals(10.0, loan.getPotentialMapView().get(actorB), EPSILON);
        assertEquals(20.0, loan.getPotentialMapView().get(actorC), EPSILON);
        
        assertEquals(actorA, loan.getDirectionMap().get(actorB));
        assertEquals(actorB, loan.getDirectionMap().get(actorC));
        
        loan = finder.findLenders(actorA, 50.0, 0.7);
        
        assertEquals(45.0, loan.getReceivedPotential(), EPSILON);
        assertEquals(50.0, loan.getRequestedPotential(), EPSILON);
        
        assertEquals(10.0, loan.getPotentialMapView().get(actorB), EPSILON);
        assertEquals(20.0, loan.getPotentialMapView().get(actorC), EPSILON);
        assertEquals(15.0, loan.getPotentialMapView().get(actorD), EPSILON);
        
        assertEquals(actorA, loan.getDirectionMap().get(actorB));
        assertEquals(actorB, loan.getDirectionMap().get(actorC));
        assertEquals(actorC, loan.getDirectionMap().get(actorD));
    }   
    
    @Test
    public void test2() {
        ActorGraph<String> graph = new ActorGraph<>();
        Actor<String> actorA = new Actor<>("A");
        Actor<String> actorB = new Actor<>("B");
        Actor<String> actorC = new Actor<>("C");
        Actor<String> actorD = new Actor<>("D");
        
        graph.addActor(actorA, 4.0);
        graph.addActor(actorB, 10.0);
        graph.addActor(actorC, 9.0);
        graph.addActor(actorD, 8.0);
        
        graph.addArc(actorB, actorA, 0.4);
        graph.addArc(actorC, actorA, 0.3);
        graph.addArc(actorD, actorA, 0.2);
        
        DefaultMostCostEffectiveLoanFinder<String> finder = 
                new DefaultMostCostEffectiveLoanFinder<>();
        
        MostCostEffectiveLoan<String> loan = 
                finder.findLenders(actorA, 5.0, 0.5);
        
        assertEquals(5.0, loan.getReceivedPotential(), EPSILON);
        assertEquals(5.0, loan.getRequestedPotential(), EPSILON);
        assertEquals(actorA, loan.getDirectionMap().get(actorD));
        assertNull(loan.getDirectionMap().get(actorB));
        assertNull(loan.getDirectionMap().get(actorC));
        
        System.out.println(loan);
    }
}
