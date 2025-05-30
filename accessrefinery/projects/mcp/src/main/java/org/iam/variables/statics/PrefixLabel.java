package org.iam.variables.statics;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

import org.iam.variables.dynamics.BddOperableLabel;
import org.iam.variables.dynamics.OperableLabel;
import org.iam.variables.dynamics.OperableLabelFactory;
import org.iam.variables.dynamics.OperableLabelType;
import org.batfish.MutableBDDInteger;
import org.batfish.datamodel.Prefix;

public class PrefixLabel extends Label {
    private final Prefix value;

    private OperableLabel cachedOperableLabel;

    private MutableBDDInteger bddInteger;

    private BDDFactory bddFactory;

    public PrefixLabel(Prefix value) {
        this.value = value;
    }

    public void setBddFactory(BDDFactory bddFactory) {
        this.bddFactory = bddFactory;
    }

    public MutableBDDInteger getBddInteger() {
        return bddInteger;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefixLabel that = (PrefixLabel) o;
        return value.equals(that.value);
    }

    @Override
    public OperableLabel convert() {
        if (cachedOperableLabel != null) {
            return cachedOperableLabel;
        }
        assert(bddFactory != null);
        bddInteger = MutableBDDInteger.makeFromIndex(bddFactory, 32, 0, false);
        BDD bdd = bddInteger.toBDD(value);
        cachedOperableLabel = OperableLabelFactory.createVar(OperableLabelType.BDD, bdd);
        ((BddOperableLabel) cachedOperableLabel).setBddFactory(bddFactory);
        return cachedOperableLabel;
    }

    @Override
    public Object getValue() {
        return value;
    }
}