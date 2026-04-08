package org.iam.policy.grammer;

import org.iam.policy.model.MCPPolicy;
import org.iam.utils.PolicyParser;
import org.junit.Assert;
import org.junit.Test;
// import org.iam.utils.Printer;
// import org.batfish.datamodel.Prefix;
// import com.google.common.collect.Range;
import org.mcp.core.MCPBitVector;
import org.mcp.core.MCPFactory;
import org.mcp.core.MCPFactory.MCPType;
import org.mcp.variables.statics.LabelType;

import java.util.Set;

public class MCPPolicyTest {
//    String rootPath = System.getProperty("user.dir");
//    String rootPath = "/home/simple/Desktop/accessrefinery";
//    This example is from Zelkova : "Semantic-based Automated Reasoning for
//    AWS Access Policies using SMT"

  @Test
  public void testZelkovaPolicy() {
    Policy policyP = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/model/ZelkovaPolicyP.json"));
    Policy policyQ = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/model/ZelkovaPolicyQ.json"));

    MCPFactory mcpFactory = new MCPFactory(MCPType.SAT);
    MCPPolicy.setMCPFactory(mcpFactory);
    MCPPolicy mcpPolicyP = new MCPPolicy(policyP);
    MCPPolicy mcpPolicyQ = new MCPPolicy(policyQ);

    MCPBitVector mcpP = mcpPolicyP.getMCPNodeCalculation();
    MCPBitVector mcpQ = mcpPolicyQ.getMCPNodeCalculation();

    // P => Q is true
    MCPBitVector res = mcpP.and(mcpQ.not());
    Assert.assertTrue(res.isZero());

//    String filePathP = rootPath + "/data/testP.dot";
//    String filePathQ = rootPath + "/data/testQ.dot";
//
//    Printer.writeStringToFile(mcpFactory.dot(mcpP), filePathP);
//    Printer.writeStringToFile(mcpFactory.dot(mcpQ), filePathQ);

    // Q => P is false
    res = mcpQ.and(mcpP.not());
    Assert.assertNotEquals(0, (int) (res).satCount());
  }

  // This example is from Access Analyzer : "Stratified Abstraction of Access Control Policies"

  @Test
  public void testStratified() {
    Policy policy1 = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/model/StratifiedPolicy1.json"));
    Policy policy2 = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/model/StratifiedPolicy2.json"));
    Policy policy3 = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/model/StratifiedPolicy3.json"));

    MCPFactory mcpFactory = new MCPFactory(MCPType.SAT);
    MCPPolicy.setMCPFactory(mcpFactory);

    MCPPolicy mcpPolicy1 = new MCPPolicy(policy1);
    MCPPolicy mcpPolicy2 = new MCPPolicy(policy2);
    MCPPolicy mcpPolicy3 = new MCPPolicy(policy3);

    MCPBitVector mcp1 = mcpPolicy1.getMCPNodeCalculation();
    MCPBitVector mcp2 = mcpPolicy2.getMCPNodeCalculation();
    MCPBitVector mcp3 = mcpPolicy3.getMCPNodeCalculation();

//    String filePath1 = rootPath + "/data/test1.dot";
//    String filePath2 = rootPath + "/data/test2.dot";
//    String filePath3 = rootPath + "/data/test3.dot";

//    Printer.writeStringToFile(mcpFactory.dot(mcp1), filePath1);
//    Printer.writeStringToFile(mcpFactory.dot(mcp2), filePath2);
//    Printer.writeStringToFile(mcpFactory.dot(mcp3), filePath3);

    MCPBitVector res = (mcp1.or(mcp3)).biimp(mcp2);
    
    // SATSolver miniSat = MiniSat.miniSat(mcpFactory.getSatFactory());
    // miniSat.add((Formula)res.getValue());
    // Formula formula = (Formula)res.getValue();
    // boolean isTautology = formula.isTautology();
    // System.out.println(isTautology);
    // Tristate result = miniSat.sat();

    Assert.assertTrue(mcpFactory.isTautology(res));
  }

  @Test
  public void testStratifiedPolicyWithNoFiles() {
    MCPFactory mcpFactory = new MCPFactory(MCPType.SAT);
    Set.of(".*", "vpc-a", "vpc-b").forEach(value->
            mcpFactory.addVar("SrcVpc", LabelType.REGEXP, value)
    );
    Set.of(".*", "o-1", "o-2").forEach(value->
            mcpFactory.addVar("OrgID", LabelType.REGEXP, value)
    );
   mcpFactory.updates();

   MCPBitVector vpcA = mcpFactory.getVarFillOtherDomain("SrcVpc", "vpc-a");
   MCPBitVector vpcB = mcpFactory.getVarFillOtherDomain("SrcVpc", "vpc-b");
   MCPBitVector vpc_ = mcpFactory.getVarFillOtherDomain("SrcVpc", ".*");

   MCPBitVector org1 = mcpFactory.getVarFillOtherDomain("OrgID", "o-1");
   MCPBitVector org2 = mcpFactory.getVarFillOtherDomain("OrgID", "o-2");

//    MCPBitVector vpcA = mcpFactory.getDomainMCP("SrcVpc", "vpc-a")
//            .and(mcpFactory.getDomainMCP("OrgID", ".*"));
//    MCPBitVector vpcB = mcpFactory.getDomainMCP("SrcVpc", "vpc-b")
//            .and(mcpFactory.getDomainMCP("OrgID", ".*"));
//    MCPBitVector vpc_ = mcpFactory.getDomainMCP("SrcVpc", ".*")
//            .and(mcpFactory.getDomainMCP("OrgID", ".*"));
//
//    MCPBitVector org1 = mcpFactory.getDomainMCP("OrgID", "o-1")
//            .and(mcpFactory.getDomainMCP("SrcVpc", ".*"));
//    MCPBitVector org2 = mcpFactory.getDomainMCP("OrgID", "o-2")
//            .and(mcpFactory.getDomainMCP("SrcVpc", ".*"));
//    MCPBitVector org_ = mcpFactory.getDomainMCP("OrgID", ".*")
//            .and(mcpFactory.getDomainMCP("SrcVpc", ".*"));

    MCPBitVector P1S1 = vpcA.or(vpcB);
    MCPBitVector P1S2 = org2;
    MCPBitVector P1S3 = (vpc_.diff(org1)).and(vpcB);
    MCPBitVector P1 = P1S1.or(P1S2).diff(P1S3);

    MCPBitVector P2S1 = vpcA;
    MCPBitVector P2S2 = org2;
    MCPBitVector P2S3 = P1S1.and(org1);
    MCPBitVector P2 = P2S1.or(P2S2).or(P2S3);
    MCPBitVector P3 = org2.and(vpcB);

    MCPBitVector equation = (P1.or(P3)).biimp(P2);

//    String filePath1 = rootPath + "/data/test11.dot";
//    String filePath2 = rootPath + "/data/test22.dot";
//    String filePath3 = rootPath + "/data/test33.dot";
//
//    Printer.writeStringToFile(mcpFactory.dot(P1), filePath1);
//    Printer.writeStringToFile(mcpFactory.dot(P2), filePath2);
//    Printer.writeStringToFile(mcpFactory.dot(P3), filePath3);

    Assert.assertTrue(mcpFactory.isTautology(equation));
  }

  // @Test
  // public void testGenerateDotFile() {                                                                                             
  //   MCPFactory mcpFactory = new MCPFactory();
  //   Set.of(".*", "dept.*/user1", "dept1/user.*").forEach(value->
  //         mcpFactory.addVar("Resource", LabelType.REGEXP, value)
  //   );

  //   Set.of(Prefix.parse("10.10.10.10/0"), 
  //           Prefix.parse("112.0.0.0/24"), 
  //           Prefix.parse("113.0.0.0/24")).forEach(value->
  //         mcpFactory.addVar("IpAddress", LabelType.PREFIX, value)
  //   );

  //   Range<Integer> range1 = Range.greaterThan(6);
  //   Range<Integer> range2 = Range.lessThan(12);
  //   Range<Integer> range3 = Range.atLeast(0);
  //   Set.of(range1, range2, range3).forEach(value->
  //         mcpFactory.addVar("Time", LabelType.RANGE, value)
  //   );

  //   mcpFactory.updates();

  //   MCPBitVector res1 = mcpFactory.getVar("Resource", "dept.*/user1");
  //   MCPBitVector res2 = mcpFactory.getVar("Resource", "dept1/user.*");

  //   MCPBitVector pre1 = mcpFactory.getVar("IpAddress", Prefix.parse("112.0.0.0/24"));
  //   MCPBitVector pre2 = mcpFactory.getVar("IpAddress", Prefix.parse("113.0.0.0/24"));

  //   MCPBitVector time1 = mcpFactory.getVar("Time", range1);
  //   MCPBitVector time2 = mcpFactory.getVar("Time", range2);
  //   MCPBitVector time3 = mcpFactory.getVar("Time", range3);
    
  //   MCPBitVector P1S1 = res1.or(res2);
  //   MCPBitVector P1S2 = pre1.or(pre2);
  //   MCPBitVector P1S3 = time1;
  //   MCPBitVector P1 = P1S1.and(P1S2).and(P1S3);

  //   MCPBitVector P2S1 = res1.not();
  //   MCPBitVector P2S2 = pre1;
  //   MCPBitVector P2S3 = time3;
  //   MCPBitVector P2 = P2S1.and(P2S2).and(P2S3);

  //   MCPBitVector P3 = P1.diff(P2);

  //   MCPBitVector I1D1 = res2;
  //   MCPBitVector I1D2 = pre1;
  //   MCPBitVector I1D3 = time1;
  //   MCPBitVector I1 = I1D1.and(I1D2).and(I1D3);

  //   MCPBitVector I2D1 = res2;
  //   MCPBitVector I2D2 = pre2;
  //   MCPBitVector I2D3 = time1;
  //   MCPBitVector I2 = I2D1.and(I2D2).and(I2D3).or(I1);

  //   String rootPath = "/home/simple/accessrefinery";
  //   String filePath1 = rootPath + "/result/test1.dot";
  //   String filePath2 = rootPath + "/result/test2.dot";
  //   String filePath3 = rootPath + "/result/test3.dot";
  //   String filePath4 = rootPath + "/result/intent1.dot";
  //   String filePath5 = rootPath + "/result/intent2.dot";

  //   Printer.writeStringToFile(mcpFactory.dot(P1), filePath1);
  //   Printer.writeStringToFile(mcpFactory.dot(P2), filePath2);
  //   Printer.writeStringToFile(mcpFactory.dot(P3), filePath3);
  //   Printer.writeStringToFile(mcpFactory.dot(I1), filePath4);
  //   Printer.writeStringToFile(mcpFactory.dot(I2), filePath5);
  // }
}