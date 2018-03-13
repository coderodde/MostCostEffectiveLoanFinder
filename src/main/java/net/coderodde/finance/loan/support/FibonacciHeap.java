package net.coderodde.finance.loan.support;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;

/**
 * This class implements an unindexed Fibonacci heap supporting only put and 
 * remove operations.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 9, 2018)
 * @param <E> the element type.
 */
public final class FibonacciHeap<E extends Comparable<? super E>> 
        extends AbstractQueue<E> {

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean offer(E e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E poll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E peek() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static final class FibonacciHeapNode<E> {
        private final E element;
        private FibonacciHeapNode<E> parent;
        private FibonacciHeapNode<E> left = this;
        private FibonacciHeapNode<E> right = this;
        private FibonacciHeapNode<E> child;
        private int degree;
        
        FibonacciHeapNode(E element) {
            this.element = element;
        }
    }
    
    private static final int DEFAULT_CHILD_ARRAY_LENGTH = 5;
    private static final double LOG_PHI = Math.log((1 + Math.sqrt(5)) / 2);
    private FibonacciHeapNode<E> minimumNode;
    private int size;
    private FibonacciHeapNode<E>[] array = 
            new FibonacciHeapNode[DEFAULT_CHILD_ARRAY_LENGTH];
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean add(E e) {
        FibonacciHeapNode<E> node = new FibonacciHeapNode<>(e);
        
        if (minimumNode != null) {
            node.left = minimumNode;
            node.right = minimumNode.right;
            minimumNode.right = node;
            node.right.left = node;
            
            if (e.compareTo(minimumNode.element) < 0) {
                minimumNode = node;
            }
        } else {
            minimumNode = node;
        }
        
        size++;
        return true;
    }

    @Override
    public E remove() {
        if (size == 0) {
            throw new IllegalStateException("This FibonacciHeap is empty.");
        }
        
        FibonacciHeapNode<E> z = minimumNode;
        FibonacciHeapNode<E> x = z.child;
        FibonacciHeapNode<E> tmpRight;
        int numberOfChildren = z.degree;
        
        while (numberOfChildren > 0) {
            tmpRight = x.right;
            
            x.left.right = x.right;
            x.right.left = x.left;
            
            x.left = minimumNode;
            x.right = minimumNode.right;
            minimumNode.right = x;
            x.right.left = x;
            
            x.parent = null;
            x = tmpRight;
            numberOfChildren--;
        }
        
        z.left.right = z.right;
        z.right.left = z.left;
        
        if (z == z.right) {
            minimumNode = null;
        } else {
            minimumNode = z.right;
            consolidate();
        }
        
        size--;
        return z.element;
    }
    
    @Override
    public void clear() {
        this.minimumNode = null;
        this.size = 0;
    }
    
    private void consolidate() {
        int arrayCapacity = ((int) Math.floor(Math.log(size) / LOG_PHI)) + 1;
        ensureArrayCapacity(arrayCapacity);
        Arrays.fill(array, null);
        
        FibonacciHeapNode<E> x = minimumNode;
        int rootListSize = 0;
        
        if (x != null) {
            rootListSize = 1;
            x = x.right;
            
            while (x != minimumNode) {
                rootListSize++;
                x = x.right;
            }
        }
        
        while (rootListSize > 0) {
            int degree = x.degree;
            FibonacciHeapNode<E> next = x.right;
            
            while (array[degree] != null) {
                FibonacciHeapNode<E> y = array[degree];
                
                if (x.element.compareTo(y.element) > 0) {
                    FibonacciHeapNode<E> tmp = y;
                    y = x;
                    x = tmp;
                }
                
                link(y, x);
                array[degree] = null;
                degree++;
            }
            
            array[degree] = x;
            x = next;
            rootListSize--;
        }
        
        minimumNode = null;
        
        for (FibonacciHeapNode<E> y : array) {
            if (y == null) {
                continue;
            }
            
            if (minimumNode == null) {
                minimumNode = y;
            } else {
                moveToRootList(y);
            }
        }
    }
    
    private void ensureArrayCapacity(int arrayCapacity) {
        if (arrayCapacity > array.length) {
            array = new FibonacciHeapNode[arrayCapacity];
        }
    }
    
    private void link(FibonacciHeapNode<E> y, FibonacciHeapNode<E> x) {
        y.left.right = y.right;
        y.right.left = y.left;
        y.parent = x;
        
        if (x.child == null) {
            x.child = y;
            y.right = y;
            y.left = y;
        } else {
            y.left = x.child;
            y.right = x.child.right;
            x.child.right = y;
            y.right.left = y;
        }
        
        x.degree++;
    }
    
    private void moveToRootList(FibonacciHeapNode<E> node) {
        node.left.right = node.right;
        node.right.left = node.left;
        
        node.left = minimumNode;
        node.right = minimumNode.right;
        minimumNode.right = node;
        node.right.left = node;
        
        if (node.element.compareTo(minimumNode.element) < 0) {
            minimumNode = node;
        }
    }
}
