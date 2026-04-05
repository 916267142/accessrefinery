package org.iam.variables.statics;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.iam.variables.dynamics.OperableLabel;
import org.iam.variables.dynamics.OperableLabelFactory;
import org.iam.variables.dynamics.OperableLabelType;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntegerSetLabel extends Label {
    private final Sets.SetView<Integer> value;
    private OperableLabel cachedOperableLabel;

    public IntegerSetLabel(Sets.SetView<Integer> value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerSetLabel that = (IntegerSetLabel) o;
        return value.equals(that.value);
    }

    @Override
    public OperableLabel convert() {
        if (cachedOperableLabel != null) {
            return cachedOperableLabel;
        }
        cachedOperableLabel = OperableLabelFactory.createVar(OperableLabelType.INTEGER_SET, value);
        return cachedOperableLabel;
    }

    public static IntegerSetLabel getAllVariable(int low, int high) {
        return new IntegerSetLabel(Sets.union(ImmutableSet.copyOf(
                IntStream.rangeClosed(low, high)
                        .boxed()
                        .collect(Collectors.toList())), Sets.newHashSet()));
    }

    @Override
    public Object getValue() {
        return value;
    }
}