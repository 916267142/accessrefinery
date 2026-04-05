package org.iam.variables.statics;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import org.iam.variables.dynamics.OperableLabel;
import org.iam.variables.dynamics.OperableLabelFactory;
import org.iam.variables.dynamics.OperableLabelType;

import java.util.Set;

public class RegexpSetLabel extends Label {
    private final Set<String> value;
    private OperableLabel cachedOperableLabel;

    public RegexpSetLabel(Set<String> value) {
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
        RegexpSetLabel that = (RegexpSetLabel) o;
        return value.equals(that.value);
    }

    @Override
    public OperableLabel convert() {
        if (cachedOperableLabel != null) {
            return cachedOperableLabel;
        }
        Automaton.setMinimizeAlways(true);
        Automaton combinedAutomaton = value.stream()
               .map(RegExp::new)
               .map(RegExp::toAutomaton)
               .reduce(Automaton::union)
               .orElse(new Automaton());

        // String regexps = String.join("|", value);
        // Automaton combinedAutomaton = new RegExp(regexps).toAutomaton();
        // combinedAutomaton.minimize();
        // Automaton combinedAutomaton = union(value);
        combinedAutomaton.minimize();
        cachedOperableLabel = OperableLabelFactory.createVar(OperableLabelType.AUTOMATON, combinedAutomaton);
        return cachedOperableLabel;
    }


    public static Automaton union(Set<String> regexes) {
        Automaton automaton = null;
        for (String regex : regexes) {
            if (automaton == null) automaton = new RegExp(regex).toAutomaton();
            else {
                Automaton a = new RegExp(regex).toAutomaton();
                // Automaton.setMinimization(2);
                automaton = automaton.union(a);
            }
        }
        return automaton;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
