package org.iam.smt.CVC5Solver;


import io.github.cvc5.*;
import org.iam.core.Node;
import org.iam.grammer.Finding;
import org.iam.grammer.Policy;
import org.iam.grammer.Principal;
import org.iam.smt.SMTConstraintFactory;
import org.iam.smt.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CVC5Validator implements Validator {
    public static long implicationTimeCounter = 0;
    public static long intersectionTimeCounter = 0;

    private final TermManager termManager;
    private final Solver solver;

    public CVC5Validator() {
        this.termManager = new TermManager();
        this.solver = new Solver(termManager);
        solver.setOption("strings-fmf", "true");
    }

    public boolean checkIntersectionOfReduceFinding(Policy policy, Finding finding,
                                                    Node<Principal> principalNode,
                                                    Node<String> actionNode,
                                                    Node<String> resourceNode,
                                                    Map<String, Node<String>> conditionNode) {
        try {
            long startTime = System.nanoTime();

            solver.resetAssertions();
            CVC5Request CVC5Request = new CVC5Request(termManager);

            Map<String, SMTConstraintFactory.VarType> mergedMap = mergeVariableTypes(policy, finding);
            CVC5Request.addKey(mergedMap);

            Term SMTP = (Term) SMTConstraintFactory.convertToSMT(CVC5Request, policy);
            Term SMTQ = (Term) SMTConstraintFactory.convertToReduceFinding(CVC5Request, finding,
                    principalNode, actionNode, resourceNode, conditionNode);
            Term conjunction = termManager.mkTerm(Kind.AND, SMTP, SMTQ);

            solver.assertFormula(conjunction);
            Result result = solver.checkSat();

            intersectionTimeCounter += System.nanoTime() - startTime;

            return result.isSat();
        } catch (CVC5ApiException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Checks whether the implication `objectP -> objectQ` holds, where `objectP` and `objectQ` are instances of
     * either `Findings` or `Policy`.
     *
     * @param objectP The antecedent of the implication. Must be an instance of `Findings` or `Policy`
     * @param objectQ The consequent of the implication. Must be an instance of `Findings` or `Policy`
     * @return 'true' if the implication `object -> objectQ` is valid, otherwise 'false'.
     */
    @Override
    public boolean checkImplication(Object objectP, Object objectQ) {
        try {
            long startTime = System.nanoTime();

            solver.resetAssertions();
            CVC5Request CVC5Request = new CVC5Request(termManager);

            Map<String, SMTConstraintFactory.VarType> mergedMap = mergeVariableTypes(objectP, objectQ);
            CVC5Request.addKey(mergedMap);

            Term SMTP = (Term) SMTConstraintFactory.convertToSMT(CVC5Request, objectP);
            Term SMTQ = (Term) SMTConstraintFactory.convertToSMT(CVC5Request, objectQ);

            solver.assertFormula(termManager.mkTerm(Kind.NOT, termManager.mkTerm(Kind.IMPLIES, SMTP, SMTQ)));
            Result result = solver.checkSat();

            double implicationTime = System.nanoTime() - startTime;
            implicationTimeCounter += implicationTime;

            return result.isUnsat();
        } catch (CVC5ApiException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks whether the logical conjunction of `objectP` and `objectQ` is satisfiable,
     * where `objectP` and `objectQ` are instances of either `Findings` or `Policy`.
     *
     * @param objectP The first condition. Must be an instance of `Findings` or `Policy`.
     * @param objectQ The second condition. Must be an instance of `Findings` or `Policy`.
     * @return `true` if the conjunction of `objectP` and `objectQ` is satisfiable, otherwise `false`.
     */
    @Override
    public boolean checkIntersection(Object objectP, Object objectQ) {
        try {
            long startTime = System.nanoTime();

            solver.resetAssertions();
            CVC5Request CVC5Request = new CVC5Request(termManager);

            Map<String, SMTConstraintFactory.VarType> mergedMap = mergeVariableTypes(objectP, objectQ);
            CVC5Request.addKey(mergedMap);

            Term SMTP = (Term) SMTConstraintFactory.convertToSMT(CVC5Request, objectP);
            Term SMTQ = (Term) SMTConstraintFactory.convertToSMT(CVC5Request, objectQ);
            Term conjunction = termManager.mkTerm(Kind.AND, SMTP, SMTQ);

            solver.assertFormula(conjunction);
            Result result = solver.checkSat();

            intersectionTimeCounter += System.nanoTime() - startTime;

            return result.isSat();
        } catch (CVC5ApiException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Map<String, SMTConstraintFactory.VarType> mergeVariableTypes(Object objectP, Object objectQ) {
        Map<String, SMTConstraintFactory.VarType> mapP = SMTConstraintFactory.initializeKeyToType(objectP);
        Map<String, SMTConstraintFactory.VarType> mapQ = SMTConstraintFactory.initializeKeyToType(objectQ);

        Map<String, SMTConstraintFactory.VarType> mergedMap = new HashMap<>();

        Stream.concat(mapP.entrySet().stream(), mapQ.entrySet().stream())
                .forEach(entry -> {
                    String key = entry.getKey();
                    SMTConstraintFactory.VarType newValue = entry.getValue();
                    mergedMap.merge(key, newValue, (oldValue, value) -> {
                        if (oldValue != newValue) {
                            throw new IllegalArgumentException(
                                    "Conflict detected for key '" + key + "': "
                                            + oldValue + " vs " + newValue
                            );
                        }
                        return oldValue;
                    });
                });
        return mergedMap;
    }
}
