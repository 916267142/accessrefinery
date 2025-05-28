package org.iam.smt.Z3Solver;


import com.microsoft.z3.*;
import org.iam.core.Node;
import org.iam.grammer.Finding;
import org.iam.grammer.Policy;
import org.iam.grammer.Principal;
import org.iam.smt.SMTConstraintFactory;
import org.iam.smt.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Z3Validator implements Validator {

    // debug
    public static long implicationTimeCounter = 0;
    public static long intersectionTimeCounter = 0;

    private final Context ctx;
    private final Solver solver;

    public Z3Validator() {
        this.ctx = new Context();
        this.solver = ctx.mkSolver();
    }

    public boolean checkIntersectionOfReduceFinding(Policy policy, Finding finding,
                                                    Node<Principal> principalNode,
                                                    Node<String> actionNode,
                                                    Node<String> resourceNode,
                                                    Map<String, Node<String>> conditionNode) {
        long startTime = System.nanoTime();

        solver.reset();

        Z3Request z3Request = new Z3Request(ctx);

        HashMap<String, SMTConstraintFactory.VarType> mergedMap = new HashMap<>();

        Map<String, SMTConstraintFactory.VarType> mapP = SMTConstraintFactory.initializeKeyToType(policy);
        Map<String, SMTConstraintFactory.VarType> mapQ = SMTConstraintFactory.initializeKeyToType(finding);

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

        z3Request.addKey(mergedMap);

        BoolExpr SMTP = (BoolExpr) SMTConstraintFactory.convertToSMT(z3Request, policy);
        BoolExpr SMTQ = (BoolExpr) SMTConstraintFactory.convertToReduceFinding(z3Request, finding,
                principalNode, actionNode, resourceNode, conditionNode);

        BoolExpr conjunction = ctx.mkAnd(SMTP, SMTQ);
        solver.add(conjunction);
        Status result = solver.check();

        intersectionTimeCounter += System.nanoTime() - startTime;

        return result == Status.SATISFIABLE;
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

        // debug
        long startTime = System.nanoTime();

        solver.reset();
        Z3Request z3Request = new Z3Request(ctx);

        HashMap<String, SMTConstraintFactory.VarType> mergedMap = new HashMap<>();

        Map<String, SMTConstraintFactory.VarType> mapP = SMTConstraintFactory.initializeKeyToType(objectP);
        Map<String, SMTConstraintFactory.VarType> mapQ = SMTConstraintFactory.initializeKeyToType(objectQ);

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

        z3Request.addKey(mergedMap);

        BoolExpr SMTP = (BoolExpr) SMTConstraintFactory.convertToSMT(z3Request, objectP);
        BoolExpr SMTQ = (BoolExpr) SMTConstraintFactory.convertToSMT(z3Request, objectQ);

        solver.add(ctx.mkNot(ctx.mkImplies(SMTP, SMTQ)));
        Status result = solver.check();

        double implicationTime = System.nanoTime() - startTime;
        implicationTimeCounter += implicationTime;

        return result == Status.UNSATISFIABLE;
    }

    /**
     * Checks whether the logical conjunction of `objectP` and `objectQ` is satisfiable,
     * where `objectP` and `objectQ` are instances of either `Findings` or `Policy`.
     *
     * @param objectP The first condition. Must be an instance of `Findings` or `Policy`.
     * @param objectQ The second condition. Must be an instance of `Findings` or `Policy`.
     * @return `true` if the conjunction of `objectP` and `objectQ` is satisfiable, otherwise `false`.
     */
    public boolean checkIntersection(Object objectP, Object objectQ) {

        // debug
        long startTime = System.nanoTime();

        solver.reset();
        Z3Request z3Request = new Z3Request(ctx);

        HashMap<String, SMTConstraintFactory.VarType> mergedMap = new HashMap<>();

        Map<String, SMTConstraintFactory.VarType> mapP = SMTConstraintFactory.initializeKeyToType(objectP);
        Map<String, SMTConstraintFactory.VarType> mapQ = SMTConstraintFactory.initializeKeyToType(objectQ);

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
        z3Request.addKey(mergedMap);

        BoolExpr SMTP = (BoolExpr) SMTConstraintFactory.convertToSMT(z3Request, objectP);
        BoolExpr SMTQ = (BoolExpr) SMTConstraintFactory.convertToSMT(z3Request, objectQ);
        BoolExpr conjunction = ctx.mkAnd(SMTP, SMTQ);

        solver.add(conjunction);
        Status result = solver.check();

        intersectionTimeCounter += System.nanoTime() - startTime;

        return Objects.requireNonNull(result) == Status.SATISFIABLE;
    }
}
