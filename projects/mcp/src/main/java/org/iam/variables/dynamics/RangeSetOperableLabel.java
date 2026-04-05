package org.iam.variables.dynamics;

import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

class RangeSetOperableLabel extends OperableLabel {
    private final RangeSet<Integer> value;

    public RangeSetOperableLabel(RangeSet<Integer> value) {
        this.value = value;
    }

    @Override
    public OperableLabel union(OperableLabel other) {
        RangeSetOperableLabel otherRangeSetVar = (RangeSetOperableLabel) other;
        RangeSet<Integer> result = TreeRangeSet.create(this.value);
        result.addAll(otherRangeSetVar.value);
        return new RangeSetOperableLabel(result);
    }

    @Override
    public OperableLabel inter(OperableLabel other) {
        RangeSetOperableLabel otherRangeSetVar = (RangeSetOperableLabel) other;
        RangeSet<Integer> result = TreeRangeSet.create();
        result.addAll(this.value);
        result.removeAll(otherRangeSetVar.value.complement());
        return new RangeSetOperableLabel(result);
    }

    @Override
    public OperableLabel minus(OperableLabel other) {
        RangeSetOperableLabel otherRangeSetVar = (RangeSetOperableLabel) other;
        RangeSet<Integer> result = TreeRangeSet.create(this.value);
        result.removeAll(otherRangeSetVar.value);
        return new RangeSetOperableLabel(result);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RangeSetOperableLabel that = (RangeSetOperableLabel) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "RangeSetOperableLabel{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }
}