package org.iam.variables.statics;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.iam.variables.dynamics.OperableLabel;
import org.iam.variables.dynamics.OperableLabelFactory;
import org.iam.variables.dynamics.OperableLabelType;

import java.util.Set;

public class RangeSetLabel extends Label {
    private final Set<Range<Integer>> value;
    private OperableLabel cachedOperableLabel;

    public RangeSetLabel(Set<Range<Integer>> value) {
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
        RangeSetLabel that = (RangeSetLabel) o;
        return value.equals(that.value);
    }

    @Override
    public OperableLabel convert() {
        if (cachedOperableLabel != null) {
            return cachedOperableLabel;
        }
        RangeSet<Integer> combinedRangeSet = TreeRangeSet.create();
        value.forEach(combinedRangeSet::add);
        cachedOperableLabel = OperableLabelFactory.createVar(OperableLabelType.RANGE_SET, combinedRangeSet);
        return cachedOperableLabel;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
