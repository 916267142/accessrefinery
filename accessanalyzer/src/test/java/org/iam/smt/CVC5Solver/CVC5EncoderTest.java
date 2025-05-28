package org.iam.smt.CVC5Solver;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import org.iam.grammer.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class CVC5EncoderTest {

    private HashMap<String, Set<String>> keyToValue1;
    private HashMap<String, Term> keyToExpr1;
    private HashMap<String, Set<String>> keyToValue2;
    private HashMap<String, Term> keyToExpr2;
    private TermManager termManager;

    @BeforeEach
    public void setUp() throws CVC5ApiException {
        termManager = new TermManager();
        keyToValue1 = new HashMap<>();
        keyToValue1.put("test1", new HashSet<>(Collections.singleton("a*b*b*c")));
        keyToValue2 = new HashMap<>();
        keyToValue2.put("test2", new HashSet<>(Collections.singleton("10.10.10.16/22")));
        keyToExpr1 = new HashMap<>();
        keyToExpr1.put("test1", termManager.mkConst(termManager.getStringSort(), "test1"));
        keyToExpr2 = new HashMap<>();
        keyToExpr2.put("test2", termManager.mkConst(termManager.mkBitVectorSort(32), "test2"));
    }

    @Test
    void stringEqualsEncodeTest() throws CVC5ApiException {
        Condition condition = new Condition(Condition.VarOperator.STRING_EQUALS, keyToValue1);
        Term result = CVC5Encoder.conditionEncode(termManager, condition, keyToExpr1);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void stringNotEqualsEncodeTest() throws CVC5ApiException {
        Condition condition = new Condition(Condition.VarOperator.STRING_NOT_EQUALS, keyToValue1);
        Term result = CVC5Encoder.conditionEncode(termManager, condition, keyToExpr1);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void stringMatchEncodeTest() throws CVC5ApiException {
        Condition condition = new Condition(Condition.VarOperator.STRING_MATCH, keyToValue1);
        Term result = CVC5Encoder.conditionEncode(termManager, condition, keyToExpr1);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void stringNotMatchEncodeTest() throws CVC5ApiException {
        Condition condition = new Condition(Condition.VarOperator.STRING_NOT_MATCH, keyToValue1);
        Term result = CVC5Encoder.conditionEncode(termManager, condition, keyToExpr1);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void ipAddressEncodeTest() throws CVC5ApiException {
        Condition condition = new Condition(Condition.VarOperator.IP_ADDRESS, keyToValue2);
        Term result = CVC5Encoder.conditionEncode(termManager, condition, keyToExpr2);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }
}
