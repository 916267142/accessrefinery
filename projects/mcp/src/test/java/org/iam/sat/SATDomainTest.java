package org.iam.sat;

import org.junit.Before;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SATDomainTest {

    private FormulaFactory factory;
    private List<Integer> firstDomainValues;
    private List<Integer> secondDomainValues;

    @Before
    public void setUp() {
        factory = new FormulaFactory();
        firstDomainValues = Arrays.asList(10, 20, 30, 40, 50);
        secondDomainValues = Arrays.asList(1, 2, 3, 4, 5);
    }

    @Test
    public void testNumBits() {
        assertEquals(0, SATDomain.numBits(0));
        assertEquals(0, SATDomain.numBits(1));
        assertEquals(1, SATDomain.numBits(2));
        assertEquals(2, SATDomain.numBits(3));
        assertEquals(2, SATDomain.numBits(4));
        assertEquals(3, SATDomain.numBits(5));
        assertEquals(3, SATDomain.numBits(8));
        assertEquals(4, SATDomain.numBits(9));
        assertEquals(4, SATDomain.numBits(16));
        assertEquals(5, SATDomain.numBits(17));
    }

    @Test
    public void testValue() {
        SATDomain<Integer> firstDomain = new SATDomain<>(factory, firstDomainValues, 0);
        SATDomain<Integer> secondDomain = new SATDomain<>(factory, secondDomainValues, 0);

        Formula formula20 = firstDomain.value(20);
        Formula formula10 = firstDomain.value(20);
   
        Formula formula1 = secondDomain.value(2);
        Formula formula2 = secondDomain.value(2);

        Formula totalFormula = factory.and(factory.or(formula20, formula10), factory.or(formula1, formula2));

        assertTrue(firstDomain.satAssignmentToValue(totalFormula).equals(20));
        assertTrue(secondDomain.satAssignmentToValue(totalFormula).equals(2));
    }

    // @Test(expected = IllegalArgumentException.class)
    // public void testValueWithInvalidInput() {
    //     SATDomain<Integer> domain = new SATDomain<>(factory, testValues, 0);
    //     domain.value(99); // 99 is not in domain values
    // }

}