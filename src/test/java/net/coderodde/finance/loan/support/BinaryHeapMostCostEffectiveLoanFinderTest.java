package net.coderodde.finance.loan.support;

public class BinaryHeapMostCostEffectiveLoanFinderTest 
        extends AbstractMostCostEffectiveLoanFinderTest {
    
    public BinaryHeapMostCostEffectiveLoanFinderTest() {
        super(new BinaryHeapMostCostEffectiveLoanFinder<>());
    }
}
