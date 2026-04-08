package org.mcp.variables.dynamics;

import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;
import dk.brics.automaton.Automaton;

public enum OperableLabelType {
    BDD(net.sf.javabdd.BDD.class, BddOperableLabel.class),
    AUTOMATON(Automaton.class, AutomatonOperableLabel.class),
    RANGE_SET(RangeSet.class, RangeSetOperableLabel.class),
    INTEGER_SET(Sets.SetView.class, MutableSetOperableLabel.class);

    private final Class<?> valueClass;
    private final Class<? extends OperableLabel> dynamicVarClass;

    OperableLabelType(Class<?> valueClass, Class<? extends OperableLabel> dynamicVarClass) {
        this.valueClass = valueClass;
        this.dynamicVarClass = dynamicVarClass;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public Class<? extends OperableLabel> getOperableLabelClass() {
        return dynamicVarClass;
    }
}