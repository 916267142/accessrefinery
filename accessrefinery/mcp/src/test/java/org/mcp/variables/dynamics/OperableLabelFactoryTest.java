package org.mcp.variables.dynamics;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeRangeSet;

import org.junit.Test;
import org.mcp.variables.dynamics.OperableLabel;
import org.mcp.variables.dynamics.OperableLabelFactory;
import org.mcp.variables.dynamics.OperableLabelType;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.HashSet;

public class OperableLabelFactoryTest {
    @Test
    public void testBddOperableLabel() {
        BDDFactory factory = JFactory.init(1000, 1000);
        factory.setCacheRatio(64);
        factory.setVarNum(2);
        BDD bdd1 = factory.ithVar(0);
        BDD bdd2 = factory.ithVar(1);

        OperableLabel bddVar1 = OperableLabelFactory.createVar(OperableLabelType.BDD, bdd1);
        OperableLabel bddVar2 = OperableLabelFactory.createVar(OperableLabelType.BDD, bdd2);

        OperableLabel bddUnion = bddVar1.union(bddVar2);
        BDD expectedUnion = bdd1.or(bdd2);
        assertEquals(expectedUnion, bddUnion.getValue());

        OperableLabel bddInter = bddVar1.inter(bddVar2);
        BDD expectedInter = bdd1.and(bdd2);
        assertEquals(expectedInter, bddInter.getValue());

        OperableLabel bddMinus = bddVar1.minus(bddVar2);
        BDD expectedMinus = bdd1.and(bdd2.not());
        assertEquals(expectedMinus, bddMinus.getValue());
    }

    @Test
    public void testAutomatonOperableLabel() {
        Automaton automaton1 = new RegExp("a*").toAutomaton();
        Automaton automaton2 = new RegExp("b*").toAutomaton();

        OperableLabel automatonVar1 = OperableLabelFactory.createVar(OperableLabelType.AUTOMATON, automaton1);
        OperableLabel automatonVar2 = OperableLabelFactory.createVar(OperableLabelType.AUTOMATON, automaton2);

        OperableLabel automatonUnion = automatonVar1.union(automatonVar2);
        Automaton expectedUnion = automaton1.union(automaton2);
        assertEquals(expectedUnion, automatonUnion.getValue());

        OperableLabel automatonInter = automatonVar1.inter(automatonVar2);
        Automaton expectedInter = automaton1.intersection(automaton2);
        assertEquals(expectedInter, automatonInter.getValue());

        OperableLabel automatonMinus = automatonVar1.minus(automatonVar2);
        Automaton expectedMinus = automaton1.minus(automaton2);
        assertEquals(expectedMinus, automatonMinus.getValue());
    }

    @Test
    public void testRangeSetOperableLabel() {
        RangeSet<Integer> rangeSet1 = TreeRangeSet.create();
        rangeSet1.add(Range.closed(1, 5));
        RangeSet<Integer> rangeSet2 = TreeRangeSet.create();
        rangeSet2.add(Range.closed(3, 7));

        OperableLabel rangeSetVar1 = OperableLabelFactory.createVar(OperableLabelType.RANGE_SET, rangeSet1);
        OperableLabel rangeSetVar2 = OperableLabelFactory.createVar(OperableLabelType.RANGE_SET, rangeSet2);

        OperableLabel rangeSetUnion = rangeSetVar1.union(rangeSetVar2);
        RangeSet<Integer> expectedUnion = TreeRangeSet.create(rangeSet1);
        expectedUnion.addAll(rangeSet2);
        assertEquals(expectedUnion, rangeSetUnion.getValue());

        OperableLabel rangeSetInter = rangeSetVar1.inter(rangeSetVar2);
        RangeSet<Integer> expectedInter = TreeRangeSet.create();
        for (Range<Integer> range : rangeSet1.asRanges()) {
            expectedInter.addAll(rangeSet2.subRangeSet(range));
        }
        assertEquals(expectedInter, rangeSetInter.getValue());

        OperableLabel rangeSetMinus = rangeSetVar1.minus(rangeSetVar2);
        RangeSet<Integer> expectedMinus = TreeRangeSet.create(rangeSet1);
        expectedMinus.removeAll(rangeSet2);
        assertEquals(expectedMinus, rangeSetMinus.getValue());
    }

    @Test
    public void testMutableSetOperableLabel() {
        Set<Integer> set1 = new HashSet<>();
        set1.add(1);
        set1.add(2);
        set1.add(3);
        Set<Integer> set2 = new HashSet<>();
        set2.add(3);
        set2.add(4);
        set2.add(5);

        Sets.SetView<Integer> setView1 = Sets.union(set1, Sets.newHashSet());
        Sets.SetView<Integer> setView2 = Sets.union(set2, Sets.newHashSet());

        OperableLabel mutableSetVar1 = OperableLabelFactory.createVar(OperableLabelType.INTEGER_SET, setView1);
        OperableLabel mutableSetVar2 = OperableLabelFactory.createVar(OperableLabelType.INTEGER_SET, setView2);

        OperableLabel mutableSetUnion = mutableSetVar1.union(mutableSetVar2);
        Sets.SetView<Integer> expectedUnion = Sets.union(setView1, setView2);
        assertEquals(expectedUnion, mutableSetUnion.getValue());

        OperableLabel mutableSetInter = mutableSetVar1.inter(mutableSetVar2);
        Sets.SetView<Integer> expectedInter = Sets.intersection(setView1, setView2);
        assertEquals(expectedInter, mutableSetInter.getValue());

        OperableLabel mutableSetMinus = mutableSetVar1.minus(mutableSetVar2);
        Sets.SetView<Integer> expectedMinus = Sets.difference(setView1, setView2);
        assertEquals(expectedMinus, mutableSetMinus.getValue());
    }
}