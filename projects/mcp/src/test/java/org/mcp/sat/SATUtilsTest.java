package org.mcp.sat;

import org.junit.Assert;
import org.junit.Test;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.mcp.sat.SATUtils;

public class SATUtilsTest {

    @Test
    public void testBitvectorCreation() {
        FormulaFactory factory = new FormulaFactory();
        Variable[] bitvec = SATUtils.bitvector(factory, 4, 0, false);

        Assert.assertEquals(4, bitvec.length);
        Assert.assertEquals("v_0", bitvec[0].name());
        Assert.assertEquals("v_1", bitvec[1].name());
        Assert.assertEquals("v_2", bitvec[2].name());
        Assert.assertEquals("v_3", bitvec[3].name());

        Variable[] bitvecReversed = SATUtils.bitvector(factory, 4, 0, true);
        Assert.assertEquals("v_3", bitvecReversed[0].name());
        Assert.assertEquals("v_2", bitvecReversed[1].name());
        Assert.assertEquals("v_1", bitvecReversed[2].name());
        Assert.assertEquals("v_0", bitvecReversed[3].name());
    }

    @Test
    public void testConcatBitvectors() {
        FormulaFactory factory = new FormulaFactory();
        Variable[] bv1 = SATUtils.bitvector(factory, 2, 0, false);
        Variable[] bv2 = SATUtils.bitvector(factory, 2, 2, false);
        Variable[] concatenated = SATUtils.concatBitvectors(bv1, bv2);

        Assert.assertEquals(4, concatenated.length);
        Assert.assertEquals("v_0", concatenated[0].name());
        Assert.assertEquals("v_1", concatenated[1].name());
        Assert.assertEquals("v_2", concatenated[2].name());
        Assert.assertEquals("v_3", concatenated[3].name());
    }
}
