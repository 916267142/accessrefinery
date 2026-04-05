//package org.iam;
//
//import org.iam.config.Parameter;
//import org.iam.grammer.Policy;
//import org.iam.grammer.serializer.JsonFindings;
//import org.iam.grammer.Finding;
//import org.iam.core.MinerFactory;
//import org.iam.smt.CVC5Solver.CVC5Validator;
//import org.iam.smt.Z3Solver.Z3Validator;
//import org.iam.utils.PolicyParser;
//import org.iam.utils.TimeMeasure;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.lang.management.ManagementFactory;
//import java.lang.management.MemoryMXBean;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Set;
//
//public class MinerFactoryTest {
//    @Test
//    public void STRA_CVC5_mineIntentTest1() {
//        TimeMeasure timeMeasure = new TimeMeasure();
//        Policy policyP = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/stratified/StratifiedPolicy1.json"));
//        System.out.println(policyP);
//        Parameter.setActiveSolver(Parameter.SolverType.CVC5);
//        Parameter.setActiveLogic(Parameter.LogicType.STRATIFIED);
//        MinerFactory minerFactory = new MinerFactory();
//        Set<Finding> ansFindings = minerFactory.mineIntent(policyP, timeMeasure);
//        JsonFindings jsonFindings = new JsonFindings(ansFindings);
//
//        Path outputPath = Paths.get("D:/Code/StratifiedCVC5Finding1.json");
//        JsonFindings.printToFile(jsonFindings, outputPath);
//
//        try {
//            timeMeasure.writeToFile("D:/Code/StratifiedCVC5Time1.csv");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Assertions.assertEquals(3, ansFindings.size());
//    }
//
//    @Test
//    public void STRA_Z3_mineIntentTest1() {
//        TimeMeasure timeMeasure = new TimeMeasure();
//        Policy policyP = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/stratified/StratifiedPolicy1.json"));
//        System.out.println(policyP);
//        Parameter.setActiveSolver(Parameter.SolverType.Z3);
//        Parameter.setActiveLogic(Parameter.LogicType.STRATIFIED);
//        MinerFactory minerFactory = new MinerFactory();
//        Set<Finding> ansFindings = minerFactory.mineIntent(policyP, timeMeasure);
//        JsonFindings jsonFindings = new JsonFindings(ansFindings);
//
//        Path outputPath = Paths.get("D:/Code/StratifiedZ3Finding1.json");
//        JsonFindings.printToFile(jsonFindings, outputPath);
//
//        try {
//            timeMeasure.writeToFile("D:/Code/StratifiedZ3Time1.csv");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Assertions.assertEquals(3, ansFindings.size());
//    }
//
//    @Test
//    public void STRA_CVC5_mineIntentTest4() {
//        TimeMeasure timeMeasure = new TimeMeasure();
//        Policy policyP = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/stratified/StratifiedPolicy4.json"));
//        Parameter.setActiveSolver(Parameter.SolverType.CVC5);
//        Parameter.setActiveLogic(Parameter.LogicType.STRATIFIED);
//        MinerFactory minerFactory = new MinerFactory();
//        Set<Finding> ansFindings = minerFactory.mineIntent(policyP, timeMeasure);
//        System.out.println("implication" + CVC5Validator.implicationTimeCounter / 1e9);
//        System.out.println("intersection" + CVC5Validator.intersectionTimeCounter / 1e9);
//
//        JsonFindings jsonFindings = new JsonFindings(ansFindings);
//
//        Path outputPath = Paths.get("D:/Code/StratifiedCVC5Finding4.json");
//        JsonFindings.printToFile(jsonFindings, outputPath);
//
//        try {
//            timeMeasure.writeToFile("D:/Code/StratifiedCVC5Time4.csv");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Assertions.assertEquals(100, ansFindings.size());
//    }
//
//    @Test
//    public void STRA_Z3_mineIntentTest4() {
//        TimeMeasure timeMeasure = new TimeMeasure();
//        Policy policyP = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/stratified/StratifiedPolicy4.json"));
////        System.out.println(policyP);
//        Parameter.setActiveSolver(Parameter.SolverType.Z3);
//        Parameter.setActiveLogic(Parameter.LogicType.STRATIFIED);
//        MinerFactory minerFactory = new MinerFactory();
//        Set<Finding> ansFindings = minerFactory.mineIntent(policyP, timeMeasure);
//        System.out.println("implication" + Z3Validator.implicationTimeCounter / 1e9);
//        System.out.println("intersection" + Z3Validator.intersectionTimeCounter / 1e9);
//
//        JsonFindings jsonFindings = new JsonFindings(ansFindings);
//
//        Path outputPath = Paths.get("D:/Code/StratifiedZ3Finding4.json");
//        JsonFindings.printToFile(jsonFindings, outputPath);
//
//        try {
//            timeMeasure.writeToFile("D:/Code/StratifiedZ3Time4.csv");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Assertions.assertEquals(100, ansFindings.size());
//    }
//
//    @Test
//    public void testMemory() {
//        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
//        System.out.println("Max Memory: " + memoryBean.getHeapMemoryUsage().getMax() / 1024 / 1024 + "MB");
//        System.out.println("Used Memory: " + memoryBean.getHeapMemoryUsage().getUsed() / 1024 / 1024 + "MB");
//    }
//
//    @Test
//    public void STRA_Z3_mineIntentTest5() {
//        TimeMeasure timeMeasure = new TimeMeasure();
//        Policy policyP = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/stratified/rw_001.json"));
//        System.out.println(policyP);
//        Parameter.setActiveSolver(Parameter.SolverType.Z3);
//        Parameter.setActiveLogic(Parameter.LogicType.STRATIFIED);
//        MinerFactory minerFactory = new MinerFactory();
//        Set<Finding> ansFindings = minerFactory.mineIntent(policyP, timeMeasure);
//        JsonFindings jsonFindings = new JsonFindings(ansFindings);
//
//        Path outputPath = Paths.get("D:/Code/StratifiedZ3Finding5.json");
//        JsonFindings.printToFile(jsonFindings, outputPath);
//
//        try {
//            timeMeasure.writeToFile("D:/Code/StratifiedZ3Time5.csv");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Assertions.assertEquals(4, ansFindings.size());
//    }
//
//    @Test
//    public void reduceFindingTest() {
//        TimeMeasure timeMeasure = new TimeMeasure();
//        Policy policyP = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/miner/reduceFindingPolicyP.json"));
//        System.out.println(policyP);
//        Parameter.setActiveSolver(Parameter.SolverType.Z3);
//        Parameter.setActiveLogic(Parameter.LogicType.STRATIFIED);
//        Parameter.isReduced = true;
//        MinerFactory minerFactory = new MinerFactory();
//        Set<Finding> ansFindings = minerFactory.mineIntent(policyP, timeMeasure);
//        ansFindings = minerFactory.reduceIntent(policyP, ansFindings);
//        JsonFindings jsonFindings = new JsonFindings(ansFindings);
//
//        Path outputPath = Paths.get("D:/Code/ReducedFinding1.json");
//        JsonFindings.printToFile(jsonFindings, outputPath);
//    }
//}
