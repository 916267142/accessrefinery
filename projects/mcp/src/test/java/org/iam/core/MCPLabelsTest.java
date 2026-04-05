package org.iam.core;

import org.iam.variables.statics.Label;
import org.iam.variables.statics.LabelFactory;
import org.iam.variables.statics.LabelType;
import org.junit.Assert;
import org.junit.Test;

public class MCPLabelsTest {
    @Test
    public void testMCPLabelsContain() {
        MCPLabels mcpLabels = new MCPLabels();

        Label var1 = LabelFactory.createVar(LabelType.REGEXP, "(ab)*a");
        Label var2 = LabelFactory.createVar(LabelType.REGEXP, "(ab)*c");
        Label var3 = LabelFactory.createVar(LabelType.REGEXP, "ab(ab)*a");

        mcpLabels.addLabel("Action", LabelType.REGEXP, var1);
        mcpLabels.addLabel("Action", LabelType.REGEXP, var2);
        mcpLabels.addLabel("Action", LabelType.REGEXP, var3);

        mcpLabels.computeLabels();

        Assert.assertFalse(mcpLabels.isContain("Action", var1, var2));
        Assert.assertFalse(mcpLabels.isContain("Action", var3, var1));
        Assert.assertTrue(mcpLabels.isContain("Action", var1, var3));
    }
}
