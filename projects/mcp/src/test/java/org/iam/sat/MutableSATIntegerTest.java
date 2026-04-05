package org.iam.sat;

import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import static org.junit.Assert.*;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class MutableSATIntegerTest {
    @Test
    public void testMutableSATInteger() {
        FormulaFactory _factory = new FormulaFactory();

        MutableSATInteger firstDomain = MutableSATInteger.makeFromIndex(_factory, 3, 0, true);
        MutableSATInteger secondDomain = MutableSATInteger.makeFromIndex(_factory, 10, 3, false);

        Formula formula3 = firstDomain.value(3);
        Formula formula5 = firstDomain.value(5);

        Formula formula10 = secondDomain.value(10);
        Formula formula11 = secondDomain.value(11);

        Formula firstDomainFormula = _factory.or(formula5, formula3);
        Formula secondDomainFormula = _factory.or(formula10, formula11);

        Formula totalFormula = _factory.and(firstDomainFormula, secondDomainFormula);

        List<Long> values = ImmutableList.of(3L, 5L);
        List<Long> values2 = ImmutableList.of(10L, 11L);

        assertTrue(firstDomain.getValuesSatisfying(totalFormula, 2).equals(values));
        assertTrue(secondDomain.getValuesSatisfying(totalFormula, 2).equals(values2));
    }
}
