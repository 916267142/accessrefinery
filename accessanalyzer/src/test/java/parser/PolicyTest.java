package parser;

import org.iam.grammer.Policy;
import org.iam.utils.PolicyParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

public class PolicyTest {
    @Test
    public void testJsonExistence() throws IOException {
        String jsonContent = new String(PolicyParser.getTestFile("parser/test.json").readAllBytes());
        Assertions.assertFalse(jsonContent.isEmpty());
    }

    @Test
    public void testPolicyParser() {
        Policy policy = PolicyParser.parseInput(PolicyParser.getTestFile("parser/test.json"));
        System.out.println(policy);
        Assertions.assertNotNull(policy);
    }

}
