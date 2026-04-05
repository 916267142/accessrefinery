package org.mcp.variables.dynamics;

import dk.brics.automaton.Automaton;

public class AutomatonOperableLabel extends OperableLabel {
    private final Automaton value;

    public AutomatonOperableLabel(Automaton value) {
        this.value = value;
    }

    @Override
    public OperableLabel union(OperableLabel other) {
        AutomatonOperableLabel otherAutomatonVar = (AutomatonOperableLabel) other;
        return new AutomatonOperableLabel(this.value.union(otherAutomatonVar.value));
    }

    @Override
    public OperableLabel inter(OperableLabel other) {
        AutomatonOperableLabel otherAutomatonVar = (AutomatonOperableLabel) other;
        return new AutomatonOperableLabel(this.value.intersection(otherAutomatonVar.value));
    }

    @Override
    public OperableLabel minus(OperableLabel other) {
        AutomatonOperableLabel otherAutomatonVar = (AutomatonOperableLabel) other;
        return new AutomatonOperableLabel(this.value.minus(otherAutomatonVar.value));
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutomatonOperableLabel that = (AutomatonOperableLabel) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "AutomatonOperableLabel{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }
}
