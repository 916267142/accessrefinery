package org.iam.smt;

import org.iam.config.Parameter;
import org.iam.grammer.Policy;
import org.iam.utils.PolicyParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidatorFactoryTest {
    @Test
    public void checkImplicationTest() {
        ValidatorFactory validatorFactory = new ValidatorFactory();
        Policy policyP = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/implicationTestPolicyP.json"));
        Policy policyQ = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/implicationTestPolicyQ.json"));
        Policy policyR = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/implicationTestPolicyR.json"));
        Policy policyS = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/implicationTestPolicyS.json"));
        Policy policyT = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/implicationTestPolicyT.json"));
        Parameter.setActiveSolver(Parameter.SolverType.Z3);
        Assertions.assertTrue(validatorFactory.checkImplication(policyQ, policyP));
        Assertions.assertFalse(validatorFactory.checkImplication(policyP, policyR));
        Assertions.assertTrue(validatorFactory.checkImplication(policyS, policyT));
        Parameter.setActiveSolver(Parameter.SolverType.CVC5);
        Assertions.assertTrue(validatorFactory.checkImplication(policyQ, policyP));
        Assertions.assertFalse(validatorFactory.checkImplication(policyP, policyR));
        Assertions.assertTrue(validatorFactory.checkImplication(policyS, policyT));
    }

    @Test
    public void checkInterationTest() {
        ValidatorFactory validatorFactory = new ValidatorFactory();
        Policy policyP = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/interactionTestPolicyP.json"));
        Policy policyQ = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/interactionTestPolicyQ.json"));
        Policy policyR = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/interactionTestPolicyR.json"));
        Policy policyT = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/interactionTestPolicyT.json"));
        Policy policyS = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/interactionTestPolicyS.json"));
        Policy policyU = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/interactionTestPolicyU.json"));
        Policy policyV = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/interactionTestPolicyV.json"));
        Policy policyX = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/interactionTestPolicyX.json"));
        Policy policyY = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/smt/interactionTestPolicyY.json"));
        Parameter.setActiveSolver(Parameter.SolverType.Z3);
        Assertions.assertTrue(validatorFactory.checkIntersection(policyX, policyY));
        Assertions.assertTrue(validatorFactory.checkIntersection(policyQ, policyP));
        Assertions.assertFalse(validatorFactory.checkIntersection(policyP, policyR));
        Assertions.assertTrue(validatorFactory.checkIntersection(policyS, policyT));
        Assertions.assertFalse(validatorFactory.checkIntersection(policyU, policyV));
        Parameter.setActiveSolver(Parameter.SolverType.CVC5);
        Assertions.assertTrue(validatorFactory.checkIntersection(policyX, policyY));
        Assertions.assertTrue(validatorFactory.checkIntersection(policyQ, policyP));
        Assertions.assertFalse(validatorFactory.checkIntersection(policyP, policyR));
        Assertions.assertTrue(validatorFactory.checkIntersection(policyS, policyT));
        Assertions.assertFalse(validatorFactory.checkIntersection(policyU, policyV));
    }
}
