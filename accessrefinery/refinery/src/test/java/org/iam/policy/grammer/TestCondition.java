package org.iam.policy.grammer;

import org.batfish.datamodel.Prefix;
import org.junit.Test;

public class TestCondition {
    public static String processPrefix(String prefix) {
        return !prefix.contains("/") ? prefix + "/32" : prefix;
    }

    @Test
    public void testPrefix() {
        Prefix s1 = Prefix.parse(processPrefix("10.20.10.10"));
        System.out.println(s1);

        Prefix s2 = Prefix.parse("10.10.10.10/0");
        System.out.println(s2);

        System.out.println("Hello World");
    }
    
}
