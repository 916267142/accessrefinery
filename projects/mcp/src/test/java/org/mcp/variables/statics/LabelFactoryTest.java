package org.mcp.variables.statics;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Range;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import org.batfish.BDDUtils;
import org.batfish.datamodel.Ip;
import org.batfish.datamodel.Prefix;
import org.junit.Test;
import org.mcp.variables.dynamics.*;
import org.mcp.variables.statics.Label;
import org.mcp.variables.statics.LabelFactory;
import org.mcp.variables.statics.LabelType;
import org.mcp.variables.statics.PrefixLabel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

public class LabelFactoryTest {
    @Test
    public void testAutomatonOperation() {
        Automaton automaton0 = new RegExp(".*").toAutomaton();
        Automaton automaton1 = new RegExp("(a)*").toAutomaton();
        Automaton automaton3 = new RegExp("((a)*a)").toAutomaton();
        Automaton automaton4 = new RegExp("(((a)*a)|(a)*)").toAutomaton();
        Automaton automaton5 = automaton1.union(automaton3);
        Automaton automaton6 = automaton4.minus(automaton1).minus(automaton3);

        assertEquals(automaton1, automaton4);
        assertEquals(automaton4, automaton5);
        assertTrue(automaton6.isEmpty());

        assertEquals(automaton0.minus(automaton1), automaton0.minus(automaton4));
        assertTrue(automaton0.minus(automaton1).minus(automaton0.minus(automaton4)).isEmpty());
    }

    @Test
    public void testRegexpLabelOperation() {
        ImmutableSet<String> set = ImmutableSet.<String>builder()
                .add("(ab)*")
                .add("(ab)*c")
                .build();
        Label regexpSetLabel = LabelFactory.createVar(LabelType.REGEXP_SET, set);
        OperableLabel dynamicVar = regexpSetLabel.convert();
        Automaton automaton = (Automaton) dynamicVar.getValue();

        assertTrue(automaton.run("abababc"));
        assertTrue(automaton.run("abc"));
        assertTrue(automaton.run("ababab"));
        assertFalse(automaton.run("abababd"));
        assertFalse(automaton.run("abd"));
    }

    @Test
    public void testAutomatonLabelOperation() {
        Set<String> set1 = ImmutableSet.of("(a)*");
        Set<String> set2 = ImmutableSet.of("((a)*a)");
        Set<String> set3 = ImmutableSet.of("(((a)*a)|(a)*)");

        Label label1 = LabelFactory.createVar(LabelType.REGEXP_SET, set1);
        Label label2 = LabelFactory.createVar(LabelType.REGEXP_SET, set2);
        Label label3 = LabelFactory.createVar(LabelType.REGEXP_SET, set3);

        OperableLabel var1 = label1.convert();
        OperableLabel var2 = label2.convert();
        OperableLabel var3 = label3.convert();

        Automaton combinedAutomaton = (Automaton) var1.union(var2).getValue();
        AutomatonOperableLabel var4 = new AutomatonOperableLabel(combinedAutomaton);

        assertEquals(var3.getValue(), var4.getValue());
    }

    @Test
    public void testIntegerSetLabelOperation() {
        ImmutableSet<Integer> ele1 = ImmutableSet.<Integer>builder()
                .add(1)
                .add(2)
                .add(3)
                .build();
        ImmutableSet<Integer> ele2 = ImmutableSet.<Integer>builder()
                .add(3)
                .add(6)
                .build();

        Sets.SetView<Integer> setView1 = Sets.union(ele1, Sets.newHashSet());
        Sets.SetView<Integer> setView2 = Sets.union(ele2, Sets.newHashSet());

        Label integerSetLabel1 = LabelFactory.createVar(LabelType.INTEGER_SET, setView1);
        Label integerSetLabel2 = LabelFactory.createVar(LabelType.INTEGER_SET, setView2);

        OperableLabel var1 = integerSetLabel1.convert();
        OperableLabel var2 = integerSetLabel2.convert();

        Sets.SetView<?> unionSet = (Sets.SetView<?>) var1.union(var2).getValue();
        Sets.SetView<?> interSet = (Sets.SetView<?>) var1.inter(var2).getValue();
        Sets.SetView<?> minusSet = (Sets.SetView<?>) var1.minus(var2).getValue();

        assertEquals(4, unionSet.size());
        assertEquals(1, interSet.size());
        assertEquals(2, minusSet.size());
    }

    @Test
    public void testRangeLabelOperation() {
        Label label1 = LabelFactory.createVar(LabelType.RANGE, Range.closed(1, 6));
        Label label2 = LabelFactory.createVar(LabelType.RANGE, Range.closed(2, 7));
        Label label3 = LabelFactory.createVar(LabelType.RANGE, Range.closed(9, 10));
        Label label4 = LabelFactory.createVar(LabelType.RANGE, Range.closed(2, 6));

        Set<Range<Integer>> ranges =  new HashSet<>();
        ranges.add(Range.closed(1, 7));
        ranges.add(Range.closed(9, 10));
        Label label5 = LabelFactory.createVar(LabelType.RANGE_SET, ranges);

        OperableLabel var1 = label1.convert();
        OperableLabel var2 = label2.convert();
        OperableLabel var3 = label3.convert();
        OperableLabel var4 = label4.convert();
        OperableLabel var5 = label5.convert();

        assertEquals(var4, var1.inter(var2));
        assertEquals(var5.getValue(), var1.union(var2).union(var3).getValue());
    }

    @Test
    public void testPrefixLabelOperation() {
        BDDFactory factory = BDDUtils.bddFactory(32);
        Prefix prefix = Prefix.parse("1.2.3.64/30");

        Label label = LabelFactory.createVar(LabelType.PREFIX, prefix);
        ((PrefixLabel) label).setBddFactory(factory);
        OperableLabel dynamicVar = label.convert();

        Long[] expected =
                new Long[] {
                        Ip.parse("1.2.3.64").asLong(),
                        Ip.parse("1.2.3.65").asLong(),
                        Ip.parse("1.2.3.66").asLong(),
                        Ip.parse("1.2.3.67").asLong()
                };

        assertThat(((PrefixLabel)label).getBddInteger()
                .getValuesSatisfying((BDD)dynamicVar.getValue(), 100), contains(expected));
    }
}