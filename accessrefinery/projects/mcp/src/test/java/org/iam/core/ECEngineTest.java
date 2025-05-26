package org.iam.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.iam.variables.statics.IntegerSetLabel;
import org.iam.variables.statics.Label;
import org.iam.variables.statics.LabelFactory;
import org.iam.variables.statics.LabelType;
import org.junit.Assert;
import org.junit.Test;

public class ECEngineTest {
    @Test
    public void testECsForIntegerSet() {
        ImmutableSet<Integer> var1 = ImmutableSet.<Integer>builder()
                .add(1)
                .add(2)
                .build();
        ImmutableSet<Integer> var2 = ImmutableSet.<Integer>builder()
                .add(3)
                .add(4)
                .add(6)
                .build();
        ImmutableSet<Integer> var3 = ImmutableSet.<Integer>builder()
                .add(4)
                .build();

        Sets.SetView<Integer> setView1 = Sets.union(var1, Sets.newHashSet());
        Sets.SetView<Integer> setView2 = Sets.union(var2, Sets.newHashSet());
        Sets.SetView<Integer> setView3 = Sets.union(var3, Sets.newHashSet());

        Label label1 = LabelFactory.createVar(LabelType.INTEGER_SET, setView1);
        Label label2 = LabelFactory.createVar(LabelType.INTEGER_SET, setView2);
        Label label3 = LabelFactory.createVar(LabelType.INTEGER_SET, setView3);
        Label label4 = IntegerSetLabel.getAllVariable(0, 6);

        ImmutableSet<Label> vars = ImmutableSet.<Label>builder()
                .add(label1)
                .add(label2)
                .add(label3)
                .build();

        ECEngine _standardECs;
        _standardECs = new ECEngine(vars, label4);

        int numECs = _standardECs.getNumECs();
        Assert.assertEquals(4, numECs);
    }

    @Test
    public void testECsForRegexps() {
        ImmutableSet<String> var1 = ImmutableSet.<String>builder()
                .add("(ab)*")
                .add("(ab)*c")
                .add("(ab)*c|(ab)*")
                .build();
        ImmutableSet<String> var2 = ImmutableSet.<String>builder()
                .add("(ab)*")
                .build();
        ImmutableSet<String> var3 = ImmutableSet.<String>builder()
                .add("(ab)*d")
                .build();
        ImmutableSet<String> var4 = ImmutableSet.<String>builder()
                .add(".*")
                .build();

        Label label1 = LabelFactory.createVar(LabelType.REGEXP_SET, var1);
        Label label2 = LabelFactory.createVar(LabelType.REGEXP_SET, var2);
        Label label3 = LabelFactory.createVar(LabelType.REGEXP_SET, var3);
        Label label4 = LabelFactory.createVar(LabelType.REGEXP_SET, var4);

        ImmutableSet<Label> vars = ImmutableSet.<Label>builder()
                .add(label1)
                .add(label2)
                .add(label3)
                .build();

        ECEngine _standardECs;
        _standardECs = new ECEngine(vars, label4);

        int numECs = _standardECs.getNumECs();
        Assert.assertEquals(4, numECs);
    }

    @Test
    public void testECsForRegexps2() {
        ImmutableSet<String> var1 = ImmutableSet.<String>builder()
                .add("a.*ba.*b.*")
                .build();
        ImmutableSet<String> var2 = ImmutableSet.<String>builder()
                .add("a.*b.a.*b.*")
                .build();
        ImmutableSet<String> var3 = ImmutableSet.<String>builder()
                .add("a.*b..a.*b.*")
                .build();
        ImmutableSet<String> var4 = ImmutableSet.<String>builder()
                .add("a.*b...a.*b.*")
                .build();
        ImmutableSet<String> var5 = ImmutableSet.<String>builder()
                .add("a.*b....a.*.b.*")
                .build();
        ImmutableSet<String> var6 = ImmutableSet.<String>builder()
                .add("a.*b.....a.*b.*")
                .build();
        ImmutableSet<String> var7 = ImmutableSet.<String>builder()
                .add("a.*b.......*a.*b.*")
                .build();
        ImmutableSet<String> var8 = ImmutableSet.<String>builder()
                .add(".*")
                .build();

        Label label1 = LabelFactory.createVar(LabelType.REGEXP_SET, var1);
        Label label2 = LabelFactory.createVar(LabelType.REGEXP_SET, var2);
        Label label3 = LabelFactory.createVar(LabelType.REGEXP_SET, var3);
        Label label4 = LabelFactory.createVar(LabelType.REGEXP_SET, var4);
        Label label5 = LabelFactory.createVar(LabelType.REGEXP_SET, var5);
        Label label6 = LabelFactory.createVar(LabelType.REGEXP_SET, var6);
        Label label7 = LabelFactory.createVar(LabelType.REGEXP_SET, var7);
        Label label8 = LabelFactory.createVar(LabelType.REGEXP_SET, var8);


        ImmutableSet<Label> vars = ImmutableSet.<Label>builder()
                .add(label1)
                .add(label2)
                .add(label3)
                .add(label4)
                .add(label5)
                .add(label6)
                .add(label7)
                .build();

        ECEngine _standardECs;
        _standardECs = new ECEngine(vars, label8);

        int numECs = _standardECs.getNumECs();
        Assert.assertEquals(128, numECs);
    }
}
