package org.iam.variables.statics;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import org.iam.variables.dynamics.OperableLabel;
import org.iam.variables.dynamics.OperableLabelFactory;
import org.iam.variables.dynamics.OperableLabelType;

class RegexpLabel extends Label {
    private final String value;
    private OperableLabel cachedOperableLabel;

    public RegexpLabel(String value) {
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
        RegexpLabel that = (RegexpLabel) o;
        return value.equals(that.value);
    }

    @Override
    public OperableLabel convert() {
        if (cachedOperableLabel != null) {
            return cachedOperableLabel;
        }
        RegExp regExp = new RegExp(value);
        Automaton automaton = regExp.toAutomaton();
        cachedOperableLabel = OperableLabelFactory.createVar(OperableLabelType.AUTOMATON, automaton);
        return cachedOperableLabel;
    }

    @Override
    public Object getValue() {
        return value;
    }
}