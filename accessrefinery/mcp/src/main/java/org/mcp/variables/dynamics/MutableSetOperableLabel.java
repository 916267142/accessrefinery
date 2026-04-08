package org.mcp.variables.dynamics;

import com.google.common.collect.Sets;

public class MutableSetOperableLabel extends OperableLabel {
    private final Sets.SetView<Integer> value;

    public MutableSetOperableLabel(Sets.SetView<Integer> value) {
        this.value = value;
    }

    @Override
    public OperableLabel union(OperableLabel other) {
        MutableSetOperableLabel otherSetVar = (MutableSetOperableLabel) other;
        Sets.SetView<Integer> result = Sets.union(this.value, otherSetVar.value);
        return new MutableSetOperableLabel(result);
    }

    @Override
    public OperableLabel inter(OperableLabel other) {
        MutableSetOperableLabel otherSetVar = (MutableSetOperableLabel) other;
        Sets.SetView<Integer> result = Sets.intersection(this.value, otherSetVar.value);
        return new MutableSetOperableLabel(result);
    }

    @Override
    public OperableLabel minus(OperableLabel other) {
        MutableSetOperableLabel otherSetVar = (MutableSetOperableLabel) other;
        Sets.SetView<Integer> result = Sets.difference(this.value, otherSetVar.value);
        return new MutableSetOperableLabel(result);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableSetOperableLabel that = (MutableSetOperableLabel) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "MutableSetOperableLabel{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }
}