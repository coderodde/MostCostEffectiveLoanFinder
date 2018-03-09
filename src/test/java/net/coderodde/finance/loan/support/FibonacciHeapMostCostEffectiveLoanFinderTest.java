package net.coderodde.finance.loan.support;

public class FibonacciHeapMostCostEffectiveLoanFinderTest 
        extends AbstractDefaultMostCostEffectiveLoanFinderTest {
    
    public FibonacciHeapMostCostEffectiveLoanFinderTest() {
        super(new FibonacciHeapMostCostEffectiveLoanFinder<>());
    }
}
