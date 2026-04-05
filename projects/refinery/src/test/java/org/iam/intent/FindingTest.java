package org.iam.intent;

import org.iam.core.MCPFactory.MCPType;
import org.iam.model.DomainLabelTrees;
import org.iam.model.LabelTree;
import org.iam.utils.Parameter;
import org.iam.core.MCPLabels;
import org.iam.core.MCPBitVector;
import org.iam.core.MCPFactory;
import org.iam.variables.statics.LabelType;
import org.junit.Assert;
import org.junit.Test;


import java.util.Set;

public class FindingTest {
    @Test
    public void testFindingRefine() {
        MCPLabels mcp = new MCPLabels();
        Set.of(".*", "a*", "aa*", "aaa*", "aaaa*", "b*", "bb*", "a*|b*").forEach(value->
            mcp.addVar("Principal", LabelType.REGEXP, value)
        );
        Set.of(".*", "a*", "aa*", "aaa*", "aaaa*", "b*", "bb*", "a*|b*").forEach(value->
            mcp.addVar("Action", LabelType.REGEXP, value)
        );
        mcp.computeLabels();

        DomainLabelTrees domainLabelTrees = new DomainLabelTrees();
        domainLabelTrees.setDomainLabelTrees("Action", new LabelTree<>(mcp.getDomainChildrenNodes("Action")));
        domainLabelTrees.setDomainLabelTrees("Principal", new LabelTree<>(mcp.getDomainChildrenNodes("Principal")));

        MCPIntent rootFinding = new MCPIntent();
        MCPIntent.setDomainLabelTrees(domainLabelTrees);

        rootFinding.setDomainValue("Action", "a*|b*");
        rootFinding.setDomainValue("Principal", "a*");
        Assert.assertEquals(3, rootFinding.refines().size());

        rootFinding.setDomainValue("Action", ".*");
        rootFinding.setDomainValue("Principal", ".*");
        Assert.assertEquals(2, rootFinding.refines().size());
    }

    @Test
    public void testGetMCPNode() {
        MCPFactory mcpFactory = new MCPFactory(MCPType.SAT);
        Set.of(".*", "a*", "aa*", "aaa*", "aaaa*", "b*", "bb*", "a*|b*").forEach(value->
                mcpFactory.addVar("Principal", LabelType.REGEXP, value)
        );
        Set.of(".*", "a*", "aa*", "aaa*", "aaaa*", "b*", "bb*", "a*|b*").forEach(value->
                mcpFactory.addVar("Action", LabelType.REGEXP, value)
        );

        mcpFactory.updates();

        DomainLabelTrees domainLabelTrees = new DomainLabelTrees();
        domainLabelTrees.setDomainLabelTrees("Action", new LabelTree<>(mcpFactory.getDomainChildrenNodes("Action")));
        domainLabelTrees.setDomainLabelTrees("Principal", new LabelTree<>(mcpFactory.getDomainChildrenNodes("Principal")));

        MCPIntent rootFinding = new MCPIntent();
        MCPIntent.setDomainLabelTrees(domainLabelTrees);
        MCPIntent.setMCPLabelsFactory(mcpFactory);

        rootFinding.setDomainValue("Action", "a*|b*");
        rootFinding.setDomainValue("Principal", "a*");

        MCPBitVector rootFindingMCP = rootFinding.getMCPNode();
        MCPBitVector restMCP = rootFinding
                .refines()
                .stream()
                .map(MCPIntent::getMCPNode)
                .reduce(mcpFactory.getFalse(), MCPBitVector::or);

//        String filePath = "/home/simple/Desktop/accessrefinery/data/test.dot";
//        Printer.writeStringToFile(mcpFactory.dot(rootFindingMCP), filePath);
        Assert.assertTrue(mcpFactory.isContradiction(rootFindingMCP.diff(restMCP)));
    }

    @Test
    public void testAPIFunction() {
        Parameter.isSplitLabel = true;
        MCPFactory mcpFactory = new MCPFactory(MCPType.SAT);
        Set.of(".*", "a*", "aa*", "aaa*", "aaaa*", "b*", "bb*", "a*|b*").forEach(value->
                mcpFactory.addVar("Principal", LabelType.REGEXP, value)
        );
        Set.of(".*", "a*", "aa*", "aaa*", "aaaa*", "b*", "bb*", "a*|b*").forEach(value->
                mcpFactory.addVar("Action", LabelType.REGEXP, value)
        );
        mcpFactory.updates();

        MCPIntent rootFinding = MCPIntent.getRootFinding(mcpFactory);
        MCPIntent.setDomainLabelTrees(new DomainLabelTrees(mcpFactory));

        MCPBitVector rootFindingMCP = rootFinding.getMCPNode();
        MCPBitVector restMCP = rootFinding
                .refines()
                .stream()
                .map(MCPIntent::getMCPNode)
                .reduce(mcpFactory.getFalse(), MCPBitVector::or);
        Assert.assertTrue(!mcpFactory.isContradiction(rootFindingMCP.diff(restMCP)));
        Assert.assertTrue(mcpFactory.isSatisfying(rootFindingMCP.diff(restMCP)));
    }

}
