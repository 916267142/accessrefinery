package org.iam.variables.statics;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.iam.variables.dynamics.OperableLabel;
import org.iam.variables.dynamics.OperableLabelFactory;
import org.iam.variables.dynamics.OperableLabelType;

public class RangeLabel extends Label {
    private final Range<Integer> value;
    private OperableLabel cachedOperableLabel;

    public RangeLabel(Range<Integer> value) {
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
        RangeLabel that = (RangeLabel) o;
        return value.equals(that.value);
    }

    @Override
    public OperableLabel convert() {
        if (cachedOperableLabel != null) {
            return cachedOperableLabel;
        }
        RangeSet<Integer> rangeSet = TreeRangeSet.create();
        rangeSet.add(value);
        cachedOperableLabel = OperableLabelFactory.createVar(OperableLabelType.RANGE_SET, rangeSet);
        return cachedOperableLabel;
    }

    @Override
    public Object getValue() {
        return value;
    }
}