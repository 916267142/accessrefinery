package org.mcp.variables.statics;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import org.batfish.datamodel.Prefix;

import java.util.Set;

public enum LabelType {
    REGEXP(String.class, RegexpLabel.class),
    PREFIX(Prefix.class, PrefixLabel.class),
    RANGE(Range.class, RangeLabel.class),
    INTEGER_SET(Sets.SetView.class, IntegerSetLabel.class),
    REGEXP_SET(Set.class, RegexpSetLabel.class),
    PREFIX_SET(Set.class, PrefixSetLabel.class),
    RANGE_SET(Set.class, RangeSetLabel.class),
    INTEGER_SET_SET(Set.class, IntegerSetSetLabel.class);

    private final Class<?> valueClass;
    private final Class<? extends Label> labelClass;

    LabelType(Class<?> valueClass, Class<? extends Label> labelClass) {
        this.valueClass = valueClass;
        this.labelClass = labelClass;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public Class<? extends Label> getLabelClass() {
        return labelClass;
    }
}
