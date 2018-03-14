package net.coderodde.finance.loan.support;

public class FibonacciHeapMostCostEffectiveLoanFinderTest 
        extends AbstractMostCostEffectiveLoanFinderTest {
    
    public FibonacciHeapMostCostEffectiveLoanFinderTest() {
        super(new FibonacciHeapMostCostEffectiveLoanFinder<>());
    }
}
