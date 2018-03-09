package net.coderodde.finance.loan;

import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The {@link net.coderodde.finance.loan.ActorGraph} unit tests.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 2, 2018)
 */
public class ActorGraphTest {
    
    private static final double EPSILON = 0.001;
    
    @Test
    public void testActorAdd() {
        Actor<String> alice = new Actor<>("Alice");
        Actor<String> janice = new Actor<>("Janice");
        Actor<String> bob = new Actor<>("Bob");
        ActorGraph<String> graph = new ActorGraph<>();
        
        assertEquals(0, graph.getNumberOfActors());
        graph.addActor(alice, 500.0);
        assertEquals(1, graph.getNumberOfActors());
        graph.addActor(janice, 300.0);
        assertEquals(2, graph.getNumberOfActors());
        graph.addActor(bob, 100.0);
        assertEquals(3, graph.getNumberOfActors());
        
        assertEquals(500.0, graph.getActorPotential(alice), EPSILON);
        assertEquals(300.0, graph.getActorPotential(janice), EPSILON);
        assertEquals(100.0, graph.getActorPotential(bob), EPSILON);
    }
    
    @Test
    public void testAddArc() {
        Actor<String> alice = new Actor<>("Alice");
        Actor<String> janice = new Actor<>("Janice");
        Actor<String> bob = new Actor<>("Bob");
        ActorGraph<String> graph = new ActorGraph<>();
        
        graph.addActor(alice, 500.0);
        graph.addActor(janice, 300.0);
        graph.addActor(bob, 100.0);
        
        assertEquals(0, graph.getNumberOfArcs());
        graph.addArc(alice, bob, 0.2);
        assertEquals(1, graph.getNumberOfArcs());
        graph.addArc(janice, bob, 0.199);
        assertEquals(2, graph.getNumberOfArcs());
        graph.addArc(alice, bob, 0.2); // Must not increment the arc count.
        assertEquals(2, graph.getNumberOfArcs());
        
        Collection<Actor<String>> bobsLenders = graph.getIncomingArcs(bob);
        
        assertEquals(2, bobsLenders.size());
        assertTrue(bobsLenders.contains(alice));
        assertTrue(bobsLenders.contains(janice));
        
        graph.addArc(alice, janice, 0.3);
        assertEquals(3, graph.getNumberOfArcs());
        
        Collection<Actor<String>> janicesLenders =
                graph.getIncomingArcs(janice);
        
        assertEquals(1, janicesLenders.size());
        
        assertTrue(janicesLenders.contains(alice));
        
        Collection<Actor<String>> alicesLenders = graph.getIncomingArcs(alice);
        
        assertTrue(alicesLenders.isEmpty());
    }
    
    @Test
    public void testRemoveActor() {
        Actor<String> alice = new Actor<>("Alice");
        Actor<String> janice = new Actor<>("Janice");
        Actor<String> bob = new Actor<>("Bob");
        ActorGraph<String> graph = new ActorGraph<>();
        
        graph.addActor(alice, 500.0);
        graph.addActor(janice, 300.0);
        graph.addActor(bob, 100.0);
        
        graph.addArc(alice, bob, 0.2);
        graph.addArc(janice, bob, 0.199);
        graph.addArc(alice, janice, 0.3);
        
        assertTrue(graph.hasArc(alice, bob));
        assertTrue(graph.hasArc(janice, bob));
        assertTrue(graph.hasArc(alice, janice));
        assertEquals(3, graph.getNumberOfActors());
        assertEquals(3, graph.getNumberOfArcs());
        
        graph.removeActor(bob);
        assertEquals(1, graph.getNumberOfArcs());
        
        assertTrue(graph.hasArc(alice, janice));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testThrowsOnHasArcWithNonExistentSourceActor() {
        ActorGraph<String> graph = new ActorGraph<>();
        Actor<String> sourceActor = new Actor<>("Bob");
        Actor<String> targetActor = new Actor<>("Alice");
        graph.addActor(targetActor, 10.0);
        graph.hasArc(sourceActor, targetActor);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testThrowsOnHasArcWithNonExistentTargetActor() {
        ActorGraph<String> graph = new ActorGraph<>();
        Actor<String> sourceActor = new Actor<>("Bob");
        Actor<String> targetActor = new Actor<>("Alice");
        graph.addActor(sourceActor, 10.0);
        graph.hasArc(sourceActor, targetActor);
    }
    
    @Test
    public void testRemoveArc() {
        Actor<String> alice = new Actor<>("Alice");
        Actor<String> janice = new Actor<>("Janice");
        Actor<String> bob = new Actor<>("Bob");
        ActorGraph<String> graph = new ActorGraph<>();
        
        graph.addActor(alice, 500.0);
        graph.addActor(janice, 300.0);
        graph.addActor(bob, 100.0);
        
        graph.addArc(alice, bob, 0.2);
        graph.addArc(janice, bob, 0.199);
        graph.addArc(alice, janice, 0.3);
        
        assertTrue(graph.hasArc(janice, bob));
        assertEquals(3, graph.getNumberOfArcs());
        graph.removeArc(janice, bob);
        assertEquals(2, graph.getNumberOfArcs());
        assertFalse(graph.hasArc(janice, bob));
        
        assertTrue(graph.hasArc(alice, bob));
        assertEquals(2, graph.getNumberOfArcs());
        graph.removeArc(alice, bob);
        assertEquals(1, graph.getNumberOfArcs());
        assertFalse(graph.hasArc(alice, bob));
        
        assertTrue(graph.hasArc(alice, janice));
        assertEquals(1, graph.getNumberOfArcs());
        graph.removeArc(alice, janice);
        assertEquals(0, graph.getNumberOfArcs());
        assertFalse(graph.hasArc(alice, janice));
    }
    
    @Test
    public void testAddFromAnotherGraph() {
        ActorGraph<String> graph1 = new ActorGraph<>();
        ActorGraph<String> graph2 = new ActorGraph<>();
        
        Actor<String> actor1 = new Actor<>("Alice");
        Actor<String> actor2a = new Actor<>("Bob I");
        Actor<String> actor2b = new Actor<>("Bob II");
        Actor<String> actor2c = new Actor<>("Bob III");
        
        graph1.addActor(actor1, 100.0);
        graph2.addActor(actor2a, 200.0);
        graph2.addActor(actor2b, 200.0);
        graph2.addActor(actor2c, 200.0);
        
        graph2.addArc(actor2a, actor2b, 200);
        graph2.addArc(actor2b, actor2c, 200);
        graph2.addArc(actor2c, actor2a, 200);
        
        assertEquals(3, graph2.getNumberOfArcs());
        
        assertEquals(graph1, actor1.getActorGraph());
        assertEquals(graph2, actor2a.getActorGraph());
        assertEquals(graph2, actor2b.getActorGraph());
        assertEquals(graph2, actor2c.getActorGraph());
        
        graph1.addActor(actor2a, 200);
        assertEquals(1, graph2.getNumberOfArcs());
        
        assertEquals(graph1, actor1.getActorGraph());
        assertEquals(graph1, actor2a.getActorGraph());
        assertEquals(graph2, actor2b.getActorGraph());
        assertEquals(graph2, actor2c.getActorGraph());
        assertTrue(graph2.hasArc(actor2b, actor2c));
        assertFalse(graph2.hasArc(actor2c, actor2b));
    }
}
