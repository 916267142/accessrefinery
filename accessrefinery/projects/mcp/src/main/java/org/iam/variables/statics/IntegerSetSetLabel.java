package org.iam.variables.statics;

import com.google.common.collect.Sets;
import org.iam.variables.dynamics.OperableLabel;
import org.iam.variables.dynamics.OperableLabelFactory;
import org.iam.variables.dynamics.OperableLabelType;

import java.util.Set;
import java.util.stream.Collectors;

public class IntegerSetSetLabel extends Label {
    private final Set<Sets.SetView<Integer>> value;
    private OperableLabel cachedOperableLabel;

    public IntegerSetSetLabel(Set<Sets.SetView<Integer>> value) {
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
        IntegerSetSetLabel that = (IntegerSetSetLabel) o;
        return value.equals(that.value);
    }

    @Override
    public OperableLabel convert() {
        if (cachedOperableLabel != null) {
            return cachedOperableLabel;
        }
        Set<Integer> combinedSet = value.stream()
                .flatMap(Sets.SetView::stream)
                .collect(Collectors.toSet());
        Sets.SetView<Integer> finalSetView = Sets.union(combinedSet, Sets.newHashSet());
        cachedOperableLabel = OperableLabelFactory.createVar(OperableLabelType.INTEGER_SET, finalSetView);
        return cachedOperableLabel;
    }

    @Override
    public Object getValue() {
        return value;
    }
}