package org.iam.smt;

public interface Validator {
    boolean checkImplication(Object objectP, Object objectQ);
    boolean checkIntersection(Object objectP, Object objectQ);
}
