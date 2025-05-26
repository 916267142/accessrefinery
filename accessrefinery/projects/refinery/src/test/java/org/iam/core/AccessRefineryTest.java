package org.iam.core;

import org.iam.intent.MCPIntent;
import org.iam.utils.Parameter;
import org.iam.utils.ResultsAnalyzer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;


public class AccessRefineryTest {
    @Test
    public void testCalculationFindings() throws IOException {
        AccessRefinery miner = new AccessRefinery();
        String rootPath = System.getProperty("user.dir");
        String filePath = "/src/test/resources/org/iam/model/StratifiedPolicy1.json";
        System.out.println(rootPath + filePath);
        Parameter.isReduced = false;
        ResultsAnalyzer resultsAnalyzer = new ResultsAnalyzer();
        HashSet<MCPIntent> findings = miner.running(Paths.get(rootPath + filePath), resultsAnalyzer);
        Assert.assertEquals(3, findings.size());
    }
}
