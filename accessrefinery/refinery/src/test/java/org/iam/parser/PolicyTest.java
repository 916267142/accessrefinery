package org.iam.parser;

import org.iam.policy.grammer.Policy;
import org.iam.utils.PolicyParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class PolicyTest {
    @Test
    public void testJsonExistence() throws IOException {
        String jsonContent = new String(PolicyParser.getTestFile("org/iam/parser/test.json").readAllBytes());
        Assert.assertFalse(jsonContent.isEmpty());
    }

    @Test
    public void testPolicyParser() {
        Policy policy = PolicyParser.parseInput(PolicyParser.getTestFile("org/iam/parser/test.json"));
//        System.out.println(policy);
        Assert.assertNotNull(policy);
    }

}
