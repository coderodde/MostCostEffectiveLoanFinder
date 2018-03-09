package net.coderodde.finance.loan.support;

public class BinaryHeapMostCostEffectiveLoanFinderTest 
        extends AbstractDefaultMostCostEffectiveLoanFinderTest {
    
    public BinaryHeapMostCostEffectiveLoanFinderTest() {
        super(new BinaryHeapMostCostEffectiveLoanFinder<>());
    }
}
