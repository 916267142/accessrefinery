package org.mcp.core;
import org.junit.Assert;
import org.junit.Test;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

import org.logicng.formulas.FormulaFactory;
import org.mcp.core.MCPBitVector;
import org.mcp.core.MCPFactory.MCPType;
import org.logicng.formulas.Formula;


public class MCPTest {
    private BDDFactory bddFactory;
    private FormulaFactory satFactory;

    public MCPTest() {
        // Initialize factories
        bddFactory = JFactory.init(100, 100);
        bddFactory.setVarNum(2); // Two variables for testing
        satFactory = new FormulaFactory();
    }

    @Test
    public void testBDDOperations() {
        // Create BDD variables
        BDD var1 = bddFactory.ithVar(0);
        BDD var2 = bddFactory.ithVar(1);
        
        MCPBitVector mcp1 = new MCPBitVector(var1, MCPType.BDD);
        MCPBitVector mcp2 = new MCPBitVector(var2, MCPType.BDD);
        mcp1.setFactory(bddFactory);
        mcp2.setFactory(bddFactory);

        // Test AND operation
        MCPBitVector andResult = mcp1.and(mcp2);
        Assert.assertFalse(andResult.isZero());
        
        // Test OR operation
        MCPBitVector orResult = mcp1.or(mcp2);
        Assert.assertFalse(orResult.isZero());
        
        // Test NOT operation
        MCPBitVector notResult = mcp1.not();
        Assert.assertNotEquals(mcp1, notResult);
        
        // Test equality
        MCPBitVector mcp1Copy = new MCPBitVector(var1.id(), MCPType.BDD);
        Assert.assertEquals(mcp1, mcp1Copy);
        
        // Test implication
        MCPBitVector impResult = mcp1.impWith(mcp2);
        Assert.assertNotNull(impResult);
    }

    @Test
    public void testComplexSATOperations() {
        // Create SAT variables
        Formula varA = satFactory.variable("A");
        Formula varB = satFactory.variable("B");
        Formula varC = satFactory.variable("C");
        
        // Create MCPs with variables
        MCPBitVector mcpA = new MCPBitVector(varA, MCPType.SAT);
        MCPBitVector mcpB = new MCPBitVector(varB, MCPType.SAT);
        MCPBitVector mcpC = new MCPBitVector(varC, MCPType.SAT);
        
        // Set factories
        mcpA.setFactory(satFactory);
        mcpB.setFactory(satFactory);
        mcpC.setFactory(satFactory);

        // 1. Test complex nested expressions
        // (A ∧ B) ∨ (¬A ∧ C)
        MCPBitVector complexExpr1 = mcpA.and(mcpB).or(mcpA.not().and(mcpC));
        // Should not be zero (tautology)
        Assert.assertFalse(complexExpr1.isZero());
        
        // 2. Test implication chain
        // (A → B) ∧ (B → C)
        MCPBitVector implicationChain = mcpA.imp(mcpB).and(mcpB.imp(mcpC));
        // Should not be zero
        Assert.assertFalse(implicationChain.isZero());
        
        // 3. Test distributive law
        // A ∧ (B ∨ C) vs (A ∧ B) ∨ (A ∧ C)
        MCPBitVector leftDistributive = mcpA.and(mcpB.or(mcpC));
        MCPBitVector rightDistributive = mcpA.and(mcpB).or(mcpA.and(mcpC));
        // Should be equivalent
        Assert.assertTrue(leftDistributive.biimp(rightDistributive).isOne());
        
        // 4. Test De Morgan's laws
        // ¬(A ∧ B) vs ¬A ∨ ¬B
        MCPBitVector demorganLeft = mcpA.and(mcpB).not();
        MCPBitVector demorganRight = mcpA.not().or(mcpB.not());
        // Should be equivalent
        Assert.assertTrue(demorganLeft.biimp(demorganRight).isOne());
        
        // 5. Test tautology and contradiction
        // A ∨ ¬A (tautology)
        MCPBitVector tautology = mcpA.or(mcpA.not());
        Assert.assertFalse(tautology.isZero());
        
        // A ∧ ¬A (contradiction)
        MCPBitVector contradiction = mcpA.and(mcpA.not());
        Assert.assertTrue(contradiction.isZero());
        
        // 6. Test ternary logic
        // (A → B) ∧ (B → C) ∧ (C → A)
        MCPBitVector ternaryLogic = mcpA.imp(mcpB)
                            .and(mcpB.imp(mcpC))
                            .and(mcpC.imp(mcpA));
        // Should be equivalent to A ↔ B ↔ C
        MCPBitVector ternaryEquiv = mcpA.biimp(mcpB).and(mcpB.biimp(mcpC));
        MCPBitVector temp = ternaryLogic.biimp(ternaryEquiv);
        System.out.println(((Formula)temp.getValue()).isTautology());
        Assert.assertTrue(ternaryLogic.biimp(ternaryEquiv).isOne());
        
        // 7. Test complex bi-implication
        // (A ∧ B) ↔ (C ∨ ¬A)
        MCPBitVector complexBiimp = mcpA.and(mcpB).biimp(mcpC.or(mcpA.not()));
        // Should not be zero (not a tautology)
        Assert.assertFalse(complexBiimp.isZero());
        
        // 8. Test with all variables false
        MCPBitVector allFalse = mcpA.not().and(mcpB.not()).and(mcpC.not());
        // Should not be zero (is satisfiable)
        Assert.assertFalse(allFalse.isZero());
        
        // 9. Test with all variables true
        MCPBitVector allTrue = mcpA.and(mcpB).and(mcpC);
        // Should not be zero (is satisfiable)
        Assert.assertFalse(allTrue.isZero());
        
        // 10. Test complex combination
        // (A ∨ B) ∧ (¬A ∨ C) ∧ (¬B ∨ ¬C)
        MCPBitVector complexCombination = mcpA.or(mcpB)
                                    .and(mcpA.not().or(mcpC))
                                    .and(mcpB.not().or(mcpC.not()));
        // Should not be zero (is satisfiable)
        Assert.assertFalse(complexCombination.isZero());
    }

    @Test
    public void testSATOperations() {
        // Create SAT variables
        Formula varA = satFactory.variable("A");
        Formula varB = satFactory.variable("B");
        
        MCPBitVector mcpA = new MCPBitVector(varA, MCPType.SAT);
        MCPBitVector mcpB = new MCPBitVector(varB, MCPType.SAT);
        mcpA.setFactory(satFactory);
        mcpB.setFactory(satFactory);

        // Test AND operation
        MCPBitVector andResult = mcpA.and(mcpB);
        Assert.assertFalse(andResult.isZero());
        
        // Test OR operation
        MCPBitVector orResult = mcpA.or(mcpB);
        Assert.assertFalse(orResult.isZero());
        
        // Test NOT operation
        MCPBitVector notResult = mcpA.not();
        Assert.assertNotEquals(mcpA, notResult);
        
        // Test equality
        MCPBitVector mcpACopy = new MCPBitVector(varA, MCPType.SAT);
        Assert.assertEquals(mcpA, mcpACopy);
        
        // Test implication
        MCPBitVector impResult = mcpA.impWith(mcpB);
        Assert.assertNotNull(impResult);
    }

    @Test
    public void testMixedTypeOperations() {
        // Create one BDD and one SAT variable
        BDD bddVar = bddFactory.ithVar(0);
        Formula satVar = satFactory.variable("A");
        
        MCPBitVector mcpBDD = new MCPBitVector(bddVar, MCPType.BDD);
        MCPBitVector mcpSAT = new MCPBitVector(satVar, MCPType.SAT);
        mcpBDD.setFactory(bddFactory);
        mcpSAT.setFactory(satFactory);

        try {
            // This should throw an exception
            mcpBDD.and(mcpSAT);
            Assert.fail("Expected exception when mixing BDD and SAT types");
        } catch (IllegalArgumentException e) {
            // Expected behavior
        }
    }

    @Test
    public void testFactoryMethods() {
        BDD bddVar = bddFactory.ithVar(0);
        MCPBitVector mcp = new MCPBitVector(bddVar, MCPType.BDD);
        
        // Test factory getter/setter
        mcp.setFactory(bddFactory);
        Assert.assertEquals(bddFactory, mcp.getFactory());
        
        // Test for SAT
        Formula satVar = satFactory.variable("A");
        MCPBitVector mcpSat = new MCPBitVector(satVar, MCPType.SAT);
        mcpSat.setFactory(satFactory);
        Assert.assertEquals(satFactory, mcpSat.getFactory());
    }

    @Test
    public void testSpecialCases() {
        // Test zero/one cases for BDD
        MCPBitVector bddZero = new MCPBitVector(bddFactory.zero(), MCPType.BDD);
        MCPBitVector bddOne = new MCPBitVector(bddFactory.one(), MCPType.BDD);
        Assert.assertTrue(bddZero.isZero());
        Assert.assertFalse(bddOne.isZero());
        
        // Test zero/one cases for SAT
        MCPBitVector satFalse = new MCPBitVector(satFactory.verum(), MCPType.SAT); // Adjust based on your definition
        MCPBitVector satTrue = new MCPBitVector(satFactory.falsum(), MCPType.SAT); 
        satFalse.setFactory(satFactory);
        satTrue.setFactory(satFactory);
        Assert.assertFalse(satFalse.isZero());
        Assert.assertTrue(satTrue.isZero());
    }

    @Test
    public void testToStringAndHashCode() {
        BDD bddVar = bddFactory.ithVar(0);
        MCPBitVector mcp = new MCPBitVector(bddVar, MCPType.BDD);
        
        // Test toString contains type information
        Assert.assertTrue(mcp.toString().contains("BDD"));
        
        // Test hashCode consistency
        MCPBitVector sameMCP = new MCPBitVector(bddVar.id(), MCPType.BDD);
        Assert.assertEquals(mcp.hashCode(), sameMCP.hashCode());
    }
}