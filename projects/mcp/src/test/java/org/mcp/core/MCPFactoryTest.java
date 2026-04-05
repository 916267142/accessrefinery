package org.mcp.core;

import org.batfish.datamodel.Prefix;
import org.junit.Assert;
import org.junit.Test;
import org.mcp.core.MCPBitVector;
import org.mcp.core.MCPFactory;
import org.mcp.core.MCPFactory.MCPType;
import org.mcp.variables.statics.LabelFactory;
import org.mcp.variables.statics.LabelType;

public class MCPFactoryTest {
    @Test
    public void testMCPFactory() {
        // This is the example from the README file. 
        MCPFactory mcp = new MCPFactory(MCPType.BDD);
        mcp.addVar("Res", LabelType.REGEXP, "dept*/user1.txt");
        mcp.addVar("Res", LabelType.REGEXP, "dept1/user*.txt");

        mcp.addVar("IP", LabelType.PREFIX, Prefix.parse("112.0.0.0/24"));
        mcp.addVar("IP", LabelType.PREFIX, Prefix.parse("113.0.0.0/24"));

        mcp.updates();

        MCPBitVector res1 = mcp.getVar("Res", "dept*/user1.txt");
        MCPBitVector res2 = mcp.getVar("Res", "dept1/user*.txt");
        MCPBitVector ip1 = mcp.getVar("IP", Prefix.parse("112.0.0.0/24"));
        MCPBitVector ip2 = mcp.getVar("IP", Prefix.parse("113.0.0.0/24"));
        MCPBitVector s1 = (res1.or(res2)).and(ip1.or(ip2));
        MCPBitVector s2 = res1.not().and(ip1);
        MCPBitVector s3 = res2.not().and(ip2);
        MCPBitVector policy = s1.diff(s2).diff(s3);
        MCPBitVector intent6 = res1.and(ip1);

        // I6∧¬P is satisfiable.
        Assert.assertTrue(!policy.and(intent6.not()).isZero());

        // I6∧P is unsatisfiable.
        Assert.assertTrue(policy.not().and(intent6).isZero());

        // I6∧P is satisfiable.
        Assert.assertTrue(!policy.and(intent6).isZero());

    }

    @Test
    public void testMCPFactoryRegexp() {
        MCPFactory mcp = new MCPFactory();

        mcp.addLabel("Action", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*a"));
        mcp.addLabel("Action", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*c"));
        mcp.addLabel("Action", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*d"));
        mcp.addLabel("Action", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*e"));

        mcp.addLabel("Principal", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(a)*"));
        mcp.addLabel("Principal", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "((a)*a)"));
        mcp.addLabel("Principal", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "((a)*a)|(a)*"));
        mcp.addLabel("Principal", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(a)*aa"));
        mcp.addLabel("Principal", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(a)*aab"));

        mcp.addLabel("Resource", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*"));
        mcp.addLabel("Resource", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*c"));

        mcp.updates();

        Assert.assertEquals(8, (int) mcp.getNumBits());
        Assert.assertEquals(5, mcp.getECs("Action").size());
        Assert.assertEquals(5, mcp.getECs("Principal").size());
        Assert.assertEquals(3, mcp.getECs("Resource").size());

        MCPBitVector domain1MCP1 = mcp.getLabel("Action", LabelFactory.createVar(LabelType.REGEXP, "(ab)*a"));
        MCPBitVector domain1MCP2 = mcp.getLabel("Action", LabelFactory.createVar(LabelType.REGEXP, "(ab)*c"));
        MCPBitVector domain1MCPAll = domain1MCP1.or(domain1MCP2);
        MCPBitVector domain1MCPAll2 = mcp.getLabel("Action", LabelFactory.createVar(LabelType.REGEXP, "(ab)*d"));

        MCPBitVector domain1Inter = domain1MCPAll.and(domain1MCPAll2);
        Assert.assertTrue(domain1Inter.isZero());

        MCPBitVector domain2MCP1 = mcp.getLabel("Principal", LabelFactory.createVar(LabelType.REGEXP, "(a)*"));
        MCPBitVector domain2MCP2 = mcp.getLabel("Principal", LabelFactory.createVar(LabelType.REGEXP, "((a)*a)"));
        MCPBitVector domain2MCP3 = mcp.getLabel("Principal", LabelFactory.createVar(LabelType.REGEXP, "(a)*aab"));
        MCPBitVector domain2MCPAll = domain2MCP1.or(domain2MCP2).or(domain2MCP3);

        Assert.assertEquals(3, mcp.getLabelECs("Principal", LabelFactory.createVar(LabelType.REGEXP, "(a)*")).size());
        Assert.assertEquals(2, mcp.getLabelECs("Principal", LabelFactory.createVar(LabelType.REGEXP, "((a)*a)")).size());
        Assert.assertEquals(1, mcp.getLabelECs("Principal", LabelFactory.createVar(LabelType.REGEXP, "(a)*aab")).size());

        MCPBitVector domain3MCPAll = mcp.getLabel("Resource", LabelFactory.createVar(LabelType.REGEXP, "(ab)*"));
        MCPBitVector domainAll = domain1MCPAll.and(domain2MCPAll).and(domain3MCPAll);
        MCPBitVector domainAll2 = domain1MCPAll2.and(domain2MCPAll).and(domain3MCPAll);

        MCPBitVector domainAllInter = domainAll.and(domainAll2);

//        String filePath = "/home/simple/Desktop/FindingsMiner/data/test.dot";
//        Printer.writeStringToFile(cube.dot((domainAll.and(domainAll2))), filePath);

        Assert.assertEquals(0, (int) (domainAll.and(domainAll2)).satCount());
        Assert.assertTrue(domainAll.impWith(domain1MCP1).satCount() != 0);
        Assert.assertEquals(0, (int) (domainAllInter).satCount());
    }

    @Test
    public void testMCPFactoryRegexp2() {
        MCPFactory mcp = new MCPFactory();

        mcp.addVar("Action", LabelType.REGEXP, "(ab)*a");
        mcp.addVar("Action", LabelType.REGEXP, "(ab)*c");
        mcp.addVar("Action", LabelType.REGEXP, "(ab)*d");
        mcp.addVar("Action", LabelType.REGEXP, "(ab)*e");

        mcp.addVar("Principal", LabelType.REGEXP, "(a)*");
        mcp.addVar("Principal", LabelType.REGEXP, "((a)*a)");
        mcp.addVar("Principal", LabelType.REGEXP, "((a)*a)|(a)*");
        mcp.addVar("Principal", LabelType.REGEXP, "(a)*aa");
        mcp.addVar("Principal", LabelType.REGEXP, "(a)*aab");

        mcp.addVar("Resource", LabelType.REGEXP, "(ab)*");
        mcp.addVar("Resource", LabelType.REGEXP, "(ab)*c");

        mcp.updates();

        Assert.assertEquals(8, (int) mcp.getNumBits());
        Assert.assertEquals(5, mcp.getECs("Action").size());
        Assert.assertEquals(5, mcp.getECs("Principal").size());
        Assert.assertEquals(3, mcp.getECs("Resource").size());

        MCPBitVector domain1MCP1 = mcp.getVar("Action", "(ab)*a");
        MCPBitVector domain1MCP2 = mcp.getVar("Action", "(ab)*c");
        MCPBitVector domain1MCPAll = domain1MCP1.or(domain1MCP2);
        MCPBitVector domain1MCPAll2 = mcp.getVar("Action","(ab)*d");

        MCPBitVector domain1Inter = domain1MCPAll.and(domain1MCPAll2);
        Assert.assertTrue(domain1Inter.isZero());

        MCPBitVector domain2MCP1 = mcp.getVar("Principal", "(a)*");
        MCPBitVector domain2MCP2 = mcp.getVar("Principal", "((a)*a)");
        MCPBitVector domain2MCP3 = mcp.getVar("Principal", "(a)*aab");
        MCPBitVector domain2MCPAll = domain2MCP1.or(domain2MCP2).or(domain2MCP3);

        Assert.assertEquals(3, mcp.getVarECs("Principal", "(a)*").size());
        Assert.assertEquals(2, mcp.getVarECs("Principal", "((a)*a)").size());
        Assert.assertEquals(1, mcp.getVarECs("Principal", "(a)*aab").size());

        MCPBitVector domain3MCPAll = mcp.getVar("Resource", "(ab)*");
        MCPBitVector domainAll = domain1MCPAll.and(domain2MCPAll).and(domain3MCPAll);
        MCPBitVector domainAll2 = domain1MCPAll2.and(domain2MCPAll).and(domain3MCPAll);

        MCPBitVector domainAllInter = domainAll.and(domainAll2);

        // String filePath = "/home/simple/Desktop/accessrefinery/data/test.dot";
        // Printer.writeStringToFile(mcp.dot(domainAll), filePath);

        Assert.assertEquals(0, (int) (domainAll.and(domainAll2)).satCount());
        Assert.assertTrue(domainAll.impWith(domain1MCP1).satCount() != 0);
        Assert.assertEquals(0, (int) (domainAllInter).satCount());
    }

    @Test
    public void testMCPFactoryRegexpSAT() {
        MCPFactory mcp = new MCPFactory(MCPType.SAT);

        mcp.addLabel("Action", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*a"));
        mcp.addLabel("Action", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*c"));
        mcp.addLabel("Action", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*d"));
        mcp.addLabel("Action", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*e"));


        mcp.addLabel("Principal", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(a)*"));
        mcp.addLabel("Principal", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "((a)*a)"));
        mcp.addLabel("Principal", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "((a)*a)|(a)*"));
        mcp.addLabel("Principal", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(a)*aa"));
        mcp.addLabel("Principal", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(a)*aab"));

        mcp.addLabel("Resource", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*"));
        mcp.addLabel("Resource", LabelType.REGEXP, LabelFactory.createVar(LabelType.REGEXP, "(ab)*c"));

        mcp.updates();

        Assert.assertEquals(8, (int) mcp.getNumBits());
        Assert.assertEquals(5, mcp.getECs("Action").size());
        Assert.assertEquals(5, mcp.getECs("Principal").size());
        Assert.assertEquals(3, mcp.getECs("Resource").size());

        MCPBitVector domain1MCP1 = mcp.getLabel("Action", LabelFactory.createVar(LabelType.REGEXP, "(ab)*a"));
        MCPBitVector domain1MCP2 = mcp.getLabel("Action", LabelFactory.createVar(LabelType.REGEXP, "(ab)*c"));
        MCPBitVector domain1MCPAll = domain1MCP1.or(domain1MCP2);
        MCPBitVector domain1MCPAll2 = mcp.getLabel("Action", LabelFactory.createVar(LabelType.REGEXP, "(ab)*d"));

        MCPBitVector domain1Inter = domain1MCPAll.and(domain1MCPAll2);
        Assert.assertTrue(domain1Inter.isZero());

        MCPBitVector domain2MCP1 = mcp.getLabel("Principal", LabelFactory.createVar(LabelType.REGEXP, "(a)*"));
        MCPBitVector domain2MCP2 = mcp.getLabel("Principal", LabelFactory.createVar(LabelType.REGEXP, "((a)*a)"));
        MCPBitVector domain2MCP3 = mcp.getLabel("Principal", LabelFactory.createVar(LabelType.REGEXP, "(a)*aab"));
        MCPBitVector domain2MCPAll = domain2MCP1.or(domain2MCP2).or(domain2MCP3);

        Assert.assertEquals(3, mcp.getLabelECs("Principal", LabelFactory.createVar(LabelType.REGEXP, "(a)*")).size());
        Assert.assertEquals(2, mcp.getLabelECs("Principal", LabelFactory.createVar(LabelType.REGEXP, "((a)*a)")).size());
        Assert.assertEquals(1, mcp.getLabelECs("Principal", LabelFactory.createVar(LabelType.REGEXP, "(a)*aab")).size());

        MCPBitVector domain3MCPAll = mcp.getLabel("Resource", LabelFactory.createVar(LabelType.REGEXP, "(ab)*"));
        MCPBitVector domainAll = domain1MCPAll.and(domain2MCPAll).and(domain3MCPAll);
        MCPBitVector domainAll2 = domain1MCPAll2.and(domain2MCPAll).and(domain3MCPAll);

        MCPBitVector domainAllInter = domainAll.and(domainAll2);

//        String filePath = "/home/simple/Desktop/FindingsMiner/data/test.dot";
//        Printer.writeStringToFile(cube.dot((domainAll.and(domainAll2))), filePath);

        Assert.assertEquals(0, (int) (domainAll.and(domainAll2)).satCount());
        Assert.assertTrue(domainAll.impWith(domain1MCP1).satCount() != 0);
        Assert.assertEquals(0, (int) (domainAllInter).satCount());
    }

    @Test
    public void testMCPFactoryRegexp2SAT() {
        MCPFactory mcp = new MCPFactory(MCPType.SAT);

        mcp.addVar("Action", LabelType.REGEXP, "(ab)*a");
        mcp.addVar("Action", LabelType.REGEXP, "(ab)*c");
        mcp.addVar("Action", LabelType.REGEXP, "(ab)*d");
        mcp.addVar("Action", LabelType.REGEXP, "(ab)*e");

        mcp.addVar("Principal", LabelType.REGEXP, "(a)*");
        mcp.addVar("Principal", LabelType.REGEXP, "((a)*a)");
        mcp.addVar("Principal", LabelType.REGEXP, "((a)*a)|(a)*");
        mcp.addVar("Principal", LabelType.REGEXP, "(a)*aa");
        mcp.addVar("Principal", LabelType.REGEXP, "(a)*aab");

        mcp.addVar("Resource", LabelType.REGEXP, "(ab)*");
        mcp.addVar("Resource", LabelType.REGEXP, "(ab)*c");

        mcp.updates();

        Assert.assertEquals(8, (int) mcp.getNumBits());
        Assert.assertEquals(5, mcp.getECs("Action").size());
        Assert.assertEquals(5, mcp.getECs("Principal").size());
        Assert.assertEquals(3, mcp.getECs("Resource").size());

        MCPBitVector domain1MCP1 = mcp.getVar("Action", "(ab)*a");
        MCPBitVector domain1MCP2 = mcp.getVar("Action", "(ab)*c");
        MCPBitVector domain1MCPAll = domain1MCP1.or(domain1MCP2);
        MCPBitVector domain1MCPAll2 = mcp.getVar("Action","(ab)*d");

        MCPBitVector domain1Inter = domain1MCPAll.and(domain1MCPAll2);
        Assert.assertTrue(domain1Inter.isZero());

        MCPBitVector domain2MCP1 = mcp.getVar("Principal", "(a)*");
        MCPBitVector domain2MCP2 = mcp.getVar("Principal", "((a)*a)");
        MCPBitVector domain2MCP3 = mcp.getVar("Principal", "(a)*aab");
        MCPBitVector domain2MCPAll = domain2MCP1.or(domain2MCP2).or(domain2MCP3);

        Assert.assertEquals(3, mcp.getVarECs("Principal", "(a)*").size());
        Assert.assertEquals(2, mcp.getVarECs("Principal", "((a)*a)").size());
        Assert.assertEquals(1, mcp.getVarECs("Principal", "(a)*aab").size());

        MCPBitVector domain3MCPAll = mcp.getVar("Resource", "(ab)*");
        MCPBitVector domainAll = domain1MCPAll.and(domain2MCPAll).and(domain3MCPAll);
        MCPBitVector domainAll2 = domain1MCPAll2.and(domain2MCPAll).and(domain3MCPAll);

        MCPBitVector domainAllInter = domainAll.and(domainAll2);

        // String filePath = "/home/simple/Desktop/accessrefinery/data/test.dot";
        // Printer.writeStringToFile(mcp.dot(domainAll), filePath);

        Assert.assertEquals(0, (int) (domainAll.and(domainAll2)).satCount());
        Assert.assertTrue(domainAll.impWith(domain1MCP1).satCount() != 0);
        Assert.assertEquals(0, (int) (domainAllInter).satCount());
    }
}
