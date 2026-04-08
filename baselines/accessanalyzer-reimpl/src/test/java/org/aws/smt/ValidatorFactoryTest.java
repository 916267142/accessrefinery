package org.aws.smt;

import org.aws.config.Parameter;
import org.aws.grammar.Policy;
import org.aws.utils.PolicyParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidatorFactoryTest {
    @Test
    public void checkImplicationTest() {
        ValidatorFactory validatorFactory = new ValidatorFactory();
        Policy policyP = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/implicationTestPolicyP.json"));
        Policy policyQ = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/implicationTestPolicyQ.json"));
        Policy policyR = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/implicationTestPolicyR.json"));
        Policy policyS = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/implicationTestPolicyS.json"));
        Policy policyT = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/implicationTestPolicyT.json"));
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
        Policy policyP = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/interactionTestPolicyP.json"));
        Policy policyQ = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/interactionTestPolicyQ.json"));
        Policy policyR = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/interactionTestPolicyR.json"));
        Policy policyT = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/interactionTestPolicyT.json"));
        Policy policyS = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/interactionTestPolicyS.json"));
        Policy policyU = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/interactionTestPolicyU.json"));
        Policy policyV = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/interactionTestPolicyV.json"));
        Policy policyX = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/interactionTestPolicyX.json"));
        Policy policyY = PolicyParser.parseInput(PolicyParser.getTestFile("org/aws/smt/interactionTestPolicyY.json"));
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
