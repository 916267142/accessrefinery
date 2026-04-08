package org.mcp.variables.statics;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

import org.batfish.MutableBDDInteger;
import org.batfish.datamodel.Prefix;
import org.mcp.variables.dynamics.BddOperableLabel;
import org.mcp.variables.dynamics.OperableLabel;
import org.mcp.variables.dynamics.OperableLabelFactory;
import org.mcp.variables.dynamics.OperableLabelType;

import java.util.Set;

public class PrefixSetLabel extends Label {
    private final Set<Prefix> value;

    private OperableLabel cachedOperableLabel;

    private BDDFactory bddFactory;

    public PrefixSetLabel(Set<Prefix> value) {
        this.value = value;
    }

    public void setBddFactory(BDDFactory bddFactory) {
        this.bddFactory = bddFactory;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefixSetLabel that = (PrefixSetLabel) o;
        return value.equals(that.value);
    }

    @Override
    public OperableLabel convert() {
        if (cachedOperableLabel != null) {
            return cachedOperableLabel;
        }
        BDD bdd = value.stream().map(v -> {
            MutableBDDInteger bddInteger = MutableBDDInteger.makeFromIndex(bddFactory, 32, 0, false);
            return bddInteger.toBDD(v);
        }).reduce(BDD::or).orElse(bddFactory.one());
        cachedOperableLabel = OperableLabelFactory.createVar(OperableLabelType.BDD, bdd);
        ((BddOperableLabel) cachedOperableLabel).setBddFactory(bddFactory);
        return cachedOperableLabel;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
