package org.iam.variables.dynamics;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

public class BddOperableLabel extends OperableLabel {
    private final BDD value;

    private BDDFactory bddFactory;

    public BddOperableLabel(BDD value) {
        this.value = value;
    }

    public BDDFactory getBddFactory() {
        return bddFactory;
    }

    public void setBddFactory(BDDFactory bddFactory) {
        this.bddFactory = bddFactory;
    }

    @Override
    public OperableLabel union(OperableLabel other) {
        BddOperableLabel otherBddVar = (BddOperableLabel) other;
        return new BddOperableLabel(this.value.or(otherBddVar.value));
    }

    @Override
    public OperableLabel inter(OperableLabel other) {
        BddOperableLabel otherBddVar = (BddOperableLabel) other;
        return new BddOperableLabel(this.value.and(otherBddVar.value));
    }

    @Override
    public OperableLabel minus(OperableLabel other) {
        BddOperableLabel otherBddVar = (BddOperableLabel) other;
        return new BddOperableLabel(this.value.and(otherBddVar.value.not()));
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BddOperableLabel that = (BddOperableLabel) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "BddOperableLabel{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean isEmpty() {
        return value.isZero();
    }
}