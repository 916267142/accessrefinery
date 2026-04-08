package org.mcp.variables.statics;

import org.mcp.variables.dynamics.OperableLabel;
import org.mcp.variables.dynamics.OperableLabelFactory;
import org.mcp.variables.dynamics.OperableLabelType;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

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