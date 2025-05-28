package org.iam.smt.Z3Solver;

import com.microsoft.z3.*;
import org.iam.grammer.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

class Z3EncoderTest {

    private HashMap<String, Set<String>> keyToValue1;
    private HashMap<String, Expr<?>> keyToExpr1;
    private HashMap<String, Set<String>> keyToValue2;
    private HashMap<String, Expr<?>> keyToExpr2;
    private Context ctx;

    @BeforeEach
    public void setUp() {
        HashMap<String, String> cfg = new HashMap<>();
        cfg.put("model", "true");
        ctx = new Context(cfg);
        keyToValue1 = new HashMap<>();
        keyToValue1.put("test1", new HashSet<>(Collections.singleton("a*b*b*c")));
        keyToValue2 = new HashMap<>();
        keyToValue2.put("test2", new HashSet<>(Collections.singleton("10.10.10.16/22")));
        keyToExpr1 = new HashMap<>();
        keyToExpr1.put("test1", ctx.mkConst("test1", ctx.mkStringSort()));
        keyToExpr2 = new HashMap<>();
        keyToExpr2.put("test2", ctx.mkConst("test2", ctx.mkBitVecSort(32)));
    }

    @Test
    void stringEqualsEncodeTest() {
        Condition condition = new Condition(Condition.VarOperator.STRING_EQUALS, keyToValue1);
        BoolExpr result = Z3Encoder.conditionEncode(ctx, condition, keyToExpr1);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void stringNotEqualsEncodeTest() {
        Condition condition = new Condition(Condition.VarOperator.STRING_NOT_EQUALS, keyToValue1);
        BoolExpr result = Z3Encoder.conditionEncode(ctx, condition, keyToExpr1);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void stringMatchEncodeTest() {
        Condition condition = new Condition(Condition.VarOperator.STRING_MATCH, keyToValue1);
        BoolExpr result = Z3Encoder.conditionEncode(ctx, condition, keyToExpr1);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void stringNotMatchEncodeTest() {
        Condition condition = new Condition(Condition.VarOperator.STRING_NOT_MATCH, keyToValue1);
        BoolExpr result = Z3Encoder.conditionEncode(ctx, condition, keyToExpr1);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void ipAddressEncodeTest() {
        Condition condition = new Condition(Condition.VarOperator.IP_ADDRESS, keyToValue2);
        BoolExpr result = Z3Encoder.conditionEncode(ctx, condition, keyToExpr2);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }
}
