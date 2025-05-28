package org.iam.smt.Z3Solver;

import com.microsoft.z3.*;
import org.iam.grammer.Condition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Z3Encoder {
    /**
     * Convert an Ip into corresponding BoolExpr.
     *
     * @param ctx The Context of z3.
     * @param cidr The string representing the Ip.
     * @param ip The expr of the request.
     * @return The result BoolExpr constraint of Ip.
     * @throws IllegalArgumentException if prefix is invalid.
     */
    private static BoolExpr cidrEncode(Context ctx,
                               String cidr,
                               Expr<BitVecSort> ip
    ) {
        String[] parts = cidr.split("/");
        long baseIp = ipToLong(parts[0]);
        int prefix = 32;
        if (parts.length > 1) {
            prefix = Integer.parseInt(parts[1]);
        }

        if (prefix < 0 || prefix > 32) {
            throw new IllegalArgumentException("Invalid prefix: " + prefix);
        }

        BitVecExpr cidrIp = ctx.mkBV(baseIp, 32);

        long maskValue = prefix == 0 ? 0 : (0xFFFFFFFFL << (32 - prefix));
        BitVecExpr mask = ctx.mkBV(maskValue, 32);

        return ctx.mkEq(
                ctx.mkBVAND(ip, mask),
                ctx.mkBVAND(cidrIp, mask)
        );
    }

    /**
     * Converts an IPv4 address string to a 32-bit unsigned integer (stored as Java long).
     *
     * @param ipAddress Ipv4 address in dotted-decimal format (e.g. "192.168.1.1").
     * @return 32-bit unsigned integer represented as long.
     * @throws IllegalArgumentException if input format is invalid.
     */
    private static long ipToLong(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4) {
            System.out.println(ipAddress);
            throw new IllegalArgumentException("Invalid IPv4 address format");
        }

        long result = 0;
        for (int i = 0; i < 4; ++i) {
            int octet = Integer.parseInt(octets[i]);
            if (octet < 0 || octet > 255) {
                throw new IllegalArgumentException("Octet out of range: " + octet);
            }

            result |= (long) octet << (24 - (8 * i));
        }
        return result;
    }

    /**
     * Convert a String into corresponding BoolExpr.
     *
     * @param ctx   The Context of z3.
     * @param value The original String containing wildcards * and ?.
     * @param expr  The Expr of the request.
     * @return The result BoolExpr constraint.
     */
    public static BoolExpr commonEncode(Context ctx,
                                        String value,
                                        Expr<SeqSort<CharSort>> expr
    ) {
        int astNum = 0, queNum = 0;
        int len = value.length();
        for (int i = 0; i < len; ++i) {
            if (value.charAt(i) == '?') {
                ++queNum;
            } else if (value.charAt(i) == '*') {
                ++astNum;
            }
        }

        if (queNum == 0 && astNum == 0) {
            return ctx.mkEq(ctx.mkString(value), expr);
        } else {
            ReExpr<SeqSort<CharSort>> result = null;
            int lastLocation = 0;
            for (int i = 0; i < len; ++i) {
                if (value.charAt(i) == '?') {
                    if (lastLocation != i) {
                        result = (result == null ? ctx.mkToRe(ctx.mkString(value.substring(lastLocation, i)))
                                : ctx.mkConcat(result, ctx.mkToRe(ctx.mkString(value.substring(lastLocation, i)))));
                    }
                    result = (result == null ? ctx.mkAllcharRe(ctx.mkReSort(ctx.mkStringSort()))
                            : ctx.mkConcat(result, ctx.mkAllcharRe(ctx.mkReSort(ctx.mkStringSort()))));
                    lastLocation = i + 1;
                } else if (value.charAt(i) == '*') {
                    if (lastLocation != i) {
                        result = (result == null ? ctx.mkToRe(ctx.mkString(value.substring(lastLocation, i)))
                                : ctx.mkConcat(result, ctx.mkToRe(ctx.mkString(value.substring(lastLocation, i)))));
                    }
                    result = (result == null ? ctx.mkStar(ctx.mkAllcharRe(ctx.mkReSort(ctx.mkStringSort())))
                            : ctx.mkConcat(result, ctx.mkStar(ctx.mkAllcharRe(ctx.mkReSort(ctx.mkStringSort())))));
                    lastLocation = i + 1;
                }
            }
            if (lastLocation != len) {
                result = (result == null ? ctx.mkToRe(ctx.mkString(value.substring(lastLocation, len)))
                        : ctx.mkConcat(result, ctx.mkToRe(ctx.mkString(value.substring(lastLocation, len)))));
            }
            return ctx.mkInRe(expr, result);
        }
    }

    /**
     * Encode the Condition
     *
     * @param ctx       The Context of z3.
     * @param condition The Condition waiting to be converted.
     * @param keyToExpr The Expr of the request.
     * @return The result BoolExpr constraint.
     */
    public static BoolExpr conditionEncode(Context ctx,
                                           Condition condition,
                                           Map<String, Expr<?>> keyToExpr
    ) {
        Condition.VarOperator operator = condition.getOperator();
        Map<String, Set<String>> keyToValues = condition.getKeyToValues();

        // This part still needs to add more conditional operators.
        return switch (operator) {
            case STRING_EQUALS, STRING_EQUALS_IF_EXISTS, FOR_ANY_VALUE_STRING_EQUALS, FOR_ALL_VALUES_STRING_EQUALS
                    -> encodeStringOp(ctx, keyToValues, keyToExpr, EncodeStringOp.EQUALS);
            case STRING_MATCH, STRING_MATCH_IF_EXISTS, FOR_ANY_VALUE_STRING_MATCH, FOR_ALL_VALUES_STRING_MATCH, ARN_LIKE, STRING_LIKE
                    -> encodeStringOp(ctx, keyToValues, keyToExpr, EncodeStringOp.MATCH);
            case STRING_NOT_EQUALS, STRING_NOT_EQUALS_IF_EXISTS, FOR_ANY_VALUE_STRING_NOT_EQUALS, FOR_ALL_VALUES_STRING_NOT_EQUALS
                    -> encodeStringOp(ctx, keyToValues, keyToExpr, EncodeStringOp.NOT_EQUALS);
            case STRING_NOT_MATCH, STRING_NOT_MATCH_IF_EXISTS, FOR_ANY_VALUE_STRING_NOT_MATCH, FOR_ALL_VALUES_STRING_NOT_MATCH, ARN_NOT_LIKE, STRING_NOT_LIKE
                    -> encodeStringOp(ctx, keyToValues, keyToExpr, EncodeStringOp.NOT_MATCH);
            case STRING_EQUALS_IGNORE_CASE, STRING_EQUALS_IGNORE_CASE_IF_EXISTS, FOR_ANY_VALUE_STRING_EQUALS_IGNORE_CASE, FOR_ALL_VALUES_STRING_EQUALS_IGNORE_CASE
                    -> encodeStringOp(ctx, keyToValues, keyToExpr, EncodeStringOp.EQUALS_IGNORE_CASE);
            case STRING_NOT_EQUALS_IGNORE_CASE, STRING_NOT_EQUALS_IGNORE_CASE_IF_EXISTS, FOR_ALL_VALUES_STRING_NOT_EQUALS_IGNORE_CASE, FOR_ANY_VALUE_STRING_NOT_EQUALS_IGNORE_CASE
                    -> encodeStringOp(ctx, keyToValues, keyToExpr, EncodeStringOp.NOT_EQUALS_IGNORE_CASE);

            case IP_ADDRESS, IP_ADDRESS_IF_EXISTS -> encodeBitVecOp(ctx,keyToValues, keyToExpr, EncodeBitVecOp.IP);
            case NOT_IP_ADDRESS, NOT_IP_ADDRESS_IF_EXISTS -> encodeBitVecOp(ctx,keyToValues, keyToExpr, EncodeBitVecOp.NOT_IP);
        };
    }

    /**
     * Auxiliary enumeration for string operation types.
     */
    private enum EncodeStringOp {
        EQUALS, NOT_EQUALS, EQUALS_IGNORE_CASE, NOT_EQUALS_IGNORE_CASE, MATCH, NOT_MATCH
    }

    private static BoolExpr encodeStringOp(
            Context ctx,
            Map<String, Set<String>> keyToValues,
            Map<String, Expr<?>> keyToExpr,
            EncodeStringOp op
    ) {
        Set<String> relevantKeys = keyToValues.keySet();

        Sort expectedCharSort = ctx.mkCharSort();
        Sort expectedSeqSort = ctx.mkSeqSort(expectedCharSort);
        SortValidationCriteria seqCriteria = new SortValidationCriteria(
                expectedSeqSort,
                CharSort.class
        );
        Map<String, Expr<SeqSort<CharSort>>> stringExprs = extractAndValidates(
                ctx,
                keyToExpr,
                relevantKeys,
                seqCriteria
        );

        return switch (op) {
            case EQUALS -> stringEqualsEncode(ctx, keyToValues, stringExprs);
            case NOT_EQUALS -> stringNotEqualsEncode(ctx, keyToValues, stringExprs);
            case MATCH -> stringMatchEncode(ctx, keyToValues, stringExprs);
            case NOT_MATCH -> stringNotMatchEncode(ctx, keyToValues, stringExprs);
            case EQUALS_IGNORE_CASE -> stringEqualsIgnoreCaseEncode(ctx, keyToValues, stringExprs);
            case NOT_EQUALS_IGNORE_CASE -> stringNotEqualsIgnoreCaseEncode(ctx, keyToValues, stringExprs);
        };
    }

    private enum EncodeBitVecOp {
        IP, NOT_IP
    }

    private static BoolExpr encodeBitVecOp(
            Context ctx,
            Map<String, Set<String>> keyToValues,
            Map<String, Expr<?>> keyToExpr,
            EncodeBitVecOp op
    ) {
        Set<String> relevantKeys = keyToValues.keySet();

        Sort expectedBitVecSort = ctx.mkBitVecSort(32);
        SortValidationCriteria bitVecCriteria = new SortValidationCriteria(expectedBitVecSort);

        Map<String, Expr<BitVecSort>> bitVecExprs = extractAndValidates(
                ctx,
                keyToExpr,
                relevantKeys,
                bitVecCriteria
        );

        return switch (op) {
            case IP -> ipAddressEncode(ctx, keyToValues, bitVecExprs);
            case NOT_IP -> notIpAddressEncode(ctx, keyToValues, bitVecExprs);
        };
    }

    @SuppressWarnings("unchecked")
    private static <R extends Sort> Map<String, Expr<R>> extractAndValidates(
            Context ctx,
            Map<String, Expr<?>> sourceMap,
            Set<String> relevantKeys,
            SortValidationCriteria criteria
    ) {
        Map<String, Expr<R>> result = new HashMap<>();

        for (String key : relevantKeys) {
            Expr<?> expr = sourceMap.get(key);
            if (expr == null) {
                throw new IllegalArgumentException("Missing expression for key: " + key);
            }

            Sort actualSort = expr.getSort();
            Sort expectedSort = criteria.getExpectedSort();

            if (!actualSort.equals(expectedSort)) {
                throw new IllegalArgumentException(
                        "Invalid sort for key '" + key + "': Expected " + expectedSort +
                                ", got " + actualSort
                );
            }

            if (criteria.getElementSortType() != null) {
                if (expectedSort instanceof SeqSort) {
                    Sort expectedElementSort = ctx.mkCharSort();
                    Sort expectedSeqSort = ctx.mkSeqSort(expectedElementSort);
                    if (!actualSort.equals(expectedSeqSort)) {
                        throw new IllegalArgumentException("Element type mismatch for key: " + key);
                    }
                } else {
                    throw new UnsupportedOperationException("Unsupported composite sort type");
                }
            }

            result.put(key, (Expr<R>) expr);
        }
        return result;
    }

    /**
     * A tool for validating the sort.
     */
    public static class SortValidationCriteria {
        private final Sort expectedSort;
        private final Class<? extends Sort> elementSortType;

        public SortValidationCriteria(Sort expectedSort) {
            this.expectedSort = expectedSort;
            this.elementSortType = null;
        }

        public SortValidationCriteria(Sort expectedSort, Class<? extends Sort> elementSortType) {
            this.expectedSort = expectedSort;
            this.elementSortType = elementSortType;
        }

        public Sort getExpectedSort() { return expectedSort; }
        public Class<? extends Sort> getElementSortType() { return elementSortType; }
    }

    /**
     * This annotation applies to all the following methods
     * Encode a condition operation.
     *
     * @param ctx         The Context of z3.
     * @param keyToValues The key-value pairs of the StringEquals condition.
     * @param keyToExpr   The key-Expr pairs of the request.
     * @return The result BoolExpr constraint.
     */
    private static BoolExpr stringEqualsEncode(Context ctx,
                                              Map<String, Set<String>> keyToValues,
                                              Map<String, Expr<SeqSort<CharSort>>> keyToExpr
    ) {
        BoolExpr result = null;
        for (Map.Entry<String, Set<String>> entry : keyToValues.entrySet()) {
            BoolExpr temporaryConstraint = null;
            for (String value : entry.getValue()) {
                temporaryConstraint = temporaryConstraint == null ?
                        ctx.mkEq(keyToExpr.get(entry.getKey()), ctx.mkString(value)) :
                        ctx.mkOr(temporaryConstraint, ctx.mkEq(keyToExpr.get(entry.getKey()), ctx.mkString(value)));
            }
            if (temporaryConstraint == null) {
                continue;
            }
            if (result == null) {
                result = temporaryConstraint;
            } else {
                result = ctx.mkAnd(result, temporaryConstraint);
            }
        }
        if (result == null) {
            result = ctx.mkTrue();
        }
        return result;
    }

    private static BoolExpr stringNotEqualsEncode(Context ctx,
                                                 Map<String, Set<String>> keyToValue,
                                                 Map<String, Expr<SeqSort<CharSort>>> keyToExpr
    ) {
        return ctx.mkNot(stringEqualsEncode(ctx, keyToValue, keyToExpr));
    }

    private static BoolExpr stringEqualsIgnoreCaseEncode(Context ctx,
                                                        Map<String, Set<String>> keyToValue,
                                                        Map<String, Expr<SeqSort<CharSort>>> keyToExpr
    ) {
        BoolExpr result = null;
        for (Map.Entry<String, Set<String>> entry : keyToValue.entrySet()) {
            BoolExpr temporaryConstraint = null;
            for (String value : entry.getValue()) {
                int i = 0;
                ReExpr<SeqSort<CharSort>> temp = null;
                while (i < value.length()) {
                    char c = value.charAt(i);
                    ReExpr<SeqSort<CharSort>> charExpr = c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'
                            ? ctx.mkUnion(ctx.mkToRe(ctx.mkString(String.valueOf(c).toLowerCase())), ctx.mkToRe(ctx.mkString(String.valueOf(c).toUpperCase())))
                            : ctx.mkToRe(ctx.mkString(String.valueOf(c)));
                    temp = temp == null
                            ? charExpr
                            : ctx.mkConcat(temp, charExpr);
                    ++i;
                }
                temporaryConstraint = temporaryConstraint == null ?
                        ctx.mkInRe(keyToExpr.get(entry.getKey()), temp) :
                        ctx.mkOr(temporaryConstraint, ctx.mkInRe(keyToExpr.get(entry.getKey()), temp));
            }
            if (temporaryConstraint == null) {
                continue;
            }
            if (result == null) {
                result = temporaryConstraint;
            } else {
                result = ctx.mkAnd(result, temporaryConstraint);
            }
        }
        if (result == null) {
            result = ctx.mkTrue();
        }
        return result;
    }

    private static BoolExpr stringNotEqualsIgnoreCaseEncode(Context ctx,
                                                            Map<String, Set<String>> keyToValue,
                                                            Map<String, Expr<SeqSort<CharSort>>> keyToExpr
    ) {
        return ctx.mkNot(stringEqualsIgnoreCaseEncode(ctx, keyToValue, keyToExpr));
    }

    private static BoolExpr stringMatchEncode(Context ctx,
                                             Map<String, Set<String>> keyToValue,
                                             Map<String, Expr<SeqSort<CharSort>>> keyToExpr
    ) {
        BoolExpr result = null;
        for (Map.Entry<String, Set<String>> entry : keyToValue.entrySet()) {
            BoolExpr temporaryConstraint = null;
            for (String value : entry.getValue()) {
                temporaryConstraint = temporaryConstraint == null ?
                        commonEncode(ctx, value, keyToExpr.get(entry.getKey())) :
                        ctx.mkOr(temporaryConstraint, commonEncode(ctx, value, keyToExpr.get(entry.getKey())));
            }
            if (temporaryConstraint == null) {
                continue;
            }
            if (result == null) {
                result = temporaryConstraint;
            } else {
                result = ctx.mkAnd(result, temporaryConstraint);
            }
        }
        if (result == null) {
            result = ctx.mkTrue();
        }
        return result;
    }

    private static BoolExpr stringNotMatchEncode(Context ctx,
                                                Map<String, Set<String>> keyToValue,
                                                Map<String, Expr<SeqSort<CharSort>>> keyToExpr
    ) {
        return ctx.mkNot(stringMatchEncode(ctx, keyToValue, keyToExpr));
    }

    private static BoolExpr ipAddressEncode(Context ctx,
                                            Map<String, Set<String>> keyToValue,
                                            Map<String, Expr<BitVecSort>> keyToExpr
    ) {
        BoolExpr result = null;
        for (Map.Entry<String, Set<String>> entry : keyToValue.entrySet()) {
            BoolExpr temporaryConstraint = null;
            for (String value : entry.getValue()) {
                temporaryConstraint = temporaryConstraint == null ?
                        cidrEncode(ctx, value, keyToExpr.get(entry.getKey())) :
                        ctx.mkOr(temporaryConstraint, cidrEncode(ctx, value, keyToExpr.get(entry.getKey())));
            }
            if (temporaryConstraint == null) {
                continue;
            }
            if (result == null) {
                result = temporaryConstraint;
            } else {
                result = ctx.mkAnd(result, temporaryConstraint);
            }
        }
        if (result == null) {
            result = ctx.mkTrue();
        }
        return result;
    }

    public static BoolExpr notIpAddressEncode(Context ctx,
                                              Map<String, Set<String>> keyToValue,
                                              Map<String, Expr<BitVecSort>> keyToExpr
    ) {
        return ctx.mkNot(ipAddressEncode(ctx, keyToValue, keyToExpr));
    }
}