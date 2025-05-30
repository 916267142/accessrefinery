package org.iam.intent;
import org.junit.Assert;
import org.junit.Test;

public class MergeIntentTest {
    @Test
    public void testMergeIntent() {
        MCPIntent finding1 = new MCPIntent();
        finding1.setDomainValue("Action", "a1*");
        finding1.setDomainValue("Principal", "b1*");
        MergeIntent intent1 = new MergeIntent(finding1);

        MCPIntent finding2 = new MCPIntent();
        finding2.setDomainValue("Action", "a2*");
        finding2.setDomainValue("Principal", "b1*");
        MergeIntent intent2 = new MergeIntent(finding2);

        MCPIntent finding3 = new MCPIntent();
        finding3.setDomainValue("Action", "a1*");
        finding3.setDomainValue("Principal", "b2*");
        MergeIntent intent3 = new MergeIntent(finding3);

        MCPIntent finding4 = new MCPIntent();
        finding4.setDomainValue("Action", "a2*");
        finding4.setDomainValue("Principal", "b2*");
        MergeIntent intent4 = new MergeIntent(finding4);

        Assert.assertTrue(intent1.isMerged(intent2));
        Assert.assertTrue(intent3.isMerged(intent4));

        MergeIntent mergedIntent1 = intent1.merge(intent2);
        MergeIntent mergedIntent2 = intent3.merge(intent4);
        MergeIntent mergedIntent3 = mergedIntent1.merge(mergedIntent2);
        Assert.assertEquals(mergedIntent3.getDomainValues().size(), 2);
        System.out.println("Merged Intent: " + mergedIntent3);
    }
}
