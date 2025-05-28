package org.iam.smt.CVC5Solver;

import io.github.cvc5.*;
import org.iam.grammer.Condition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CVC5Encoder {
    /**
     * Create a CIDR constraint for IP address validation (e.g., "192.168.1.0/24" format).
     *
     * @param termManager The cvc5 TermManager instance used for term creation.
     * @param cidr CIDR-formatted IP range string in "IPv4/prefix" or "IPv4" format (default prefix = 32).
     * @param ip Target IP term to validate, must be a 32-bit bitvector (BitVector(32))
     * @return Boolean constraint term indicating whether the IP falls within the CIDR range
     * @throws IllegalArgumentException If prefix is outside valid range [0, 32]
     * @throws CVC5ApiException For API-related errors including type mismatches or invalid bitwise operations.
     */
    private static Term cidrEncode(
            TermManager termManager,
            String cidr,
            Term ip
    ) throws CVC5ApiException {
        String[] parts = cidr.split("/");
        long baseIp = ipToLong(parts[0]);
        int prefix = 32;
        if (parts.length > 1) {
            prefix = Integer.parseInt(parts[1]);
        }

        if (prefix < 0 || prefix > 32) {
            throw new IllegalArgumentException("Invalid prefix: " + prefix);
        }

        Term cidrIp = termManager.mkBitVector(32, baseIp);

        long maskValue = prefix == 0 ? 0 : (0xFFFFFFFFL << (32 - prefix));
        Term mask = termManager.mkBitVector(32, maskValue);

        Term maskedIp = termManager.mkTerm(Kind.BITVECTOR_AND, ip, mask);
        Term maskedCidr = termManager.mkTerm(Kind.BITVECTOR_AND, cidrIp, mask);
        return termManager.mkTerm(Kind.EQUAL, maskedIp, maskedCidr);
    }

    /**
     * Converts an IPv4 address string to a 32-bit unsigned integer (stored as Java long).
     *
     * @param ipAddress Ipv4 address in dotted-decimal format (e.g. "192.168.1.1").
     * @return 32-bit unsigned integer represented as long.
     * @throws IllegalArgumentException If input format is invalid.
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
     * Convert a String into SMT constraint.
     *
     * @param termManager The cvc5 termManager instance used for term creation via TermManager.
     * @param value The original String containing wildcards '*' and '?'.
     * @param term Target term to validate.
     * @return Boolean Term representing the constraint.
     * @throws CVC5ApiException For API-related errors including type mismatches or invalid bitwise operations.
     */
    public static Term commonEncode(
            TermManager termManager,
            String value,
            Term term
    ) throws CVC5ApiException {
        int astNum = 0, queNum = 0;
        int len = value.length();

        for (int i = 0; i < len; ++i) {
            char c = value.charAt(i);
            if (c == '?') {
                ++queNum;
            } else if (c == '*') {
                ++astNum;
            }
        }

        if (astNum == 0 && queNum == 0) {
            return termManager.mkTerm(Kind.EQUAL, term, termManager.mkString(value));
        } else {
            Term regex = null;
            int lastPos = 0;

            for (int i = 0; i < len; ++i) {
                char c = value.charAt(i);
                if (c == '?' || c == '*') {
                    if (i > lastPos) {
                        String literal = value.substring(lastPos, i);
                        Term litRegex = termManager.mkTerm(Kind.STRING_TO_REGEXP, termManager.mkString(literal));
                        regex = (regex == null) ? litRegex
                                : termManager.mkTerm(Kind.REGEXP_CONCAT, regex, litRegex);
                    }

                    Term wildcard = (c == '?')
                            ? termManager.mkTerm(Kind.REGEXP_ALLCHAR)
                            : termManager.mkTerm(Kind.REGEXP_STAR, termManager.mkTerm(Kind.REGEXP_ALLCHAR));
                    regex = (regex == null) ? wildcard
                            : termManager.mkTerm(Kind.REGEXP_CONCAT, regex, wildcard);
                    lastPos = i + 1;
                }
            }
            if (lastPos < len) {
                String remaining = value.substring(lastPos);
                Term remainRegex = termManager.mkTerm(Kind.STRING_TO_REGEXP, termManager.mkString(remaining));
                regex = (regex == null)
                        ? remainRegex
                        : termManager.mkTerm(Kind.REGEXP_CONCAT, regex, remainRegex);
            }
            return termManager.mkTerm(Kind.STRING_IN_REGEXP, term, regex);
        }
    }

    /**
     * Encode the Condition.
     *
     * @param termManager The cvc5 termManager instance used for term creation via TermManager.
     * @param condition The Condition waiting to be converted.
     * @param keyToTerm The Term of the request.
     * @return Boolean Term representing the corresponding SMT constraint.
     * @throws CVC5ApiException For API-related errors including type mismatches or invalid bitwise operations.
     */
    public static Term conditionEncode(TermManager termManager,
                                       Condition condition,
                                       Map<String, Term> keyToTerm
    ) throws CVC5ApiException {
        Condition.VarOperator operator = condition.getOperator();
        Map<String, Set<String>> keyToValues = condition.getKeyToValues();
        // This part still needs to add more conditional operators.
        return switch (operator) {
            case STRING_EQUALS, STRING_EQUALS_IF_EXISTS, FOR_ANY_VALUE_STRING_EQUALS, FOR_ALL_VALUES_STRING_EQUALS
                    -> encodeStringOp(termManager, keyToValues, keyToTerm, EncodeStringOp.EQUALS);
            case STRING_MATCH, STRING_MATCH_IF_EXISTS, FOR_ANY_VALUE_STRING_MATCH, FOR_ALL_VALUES_STRING_MATCH, ARN_LIKE, STRING_LIKE
                    -> encodeStringOp(termManager, keyToValues, keyToTerm, EncodeStringOp.MATCH);
            case STRING_NOT_EQUALS, STRING_NOT_EQUALS_IF_EXISTS, FOR_ANY_VALUE_STRING_NOT_EQUALS, FOR_ALL_VALUES_STRING_NOT_EQUALS
                    -> encodeStringOp(termManager, keyToValues, keyToTerm, EncodeStringOp.NOT_EQUALS);
            case STRING_NOT_MATCH, STRING_NOT_MATCH_IF_EXISTS, FOR_ANY_VALUE_STRING_NOT_MATCH, FOR_ALL_VALUES_STRING_NOT_MATCH, ARN_NOT_LIKE, STRING_NOT_LIKE
                    -> encodeStringOp(termManager, keyToValues, keyToTerm, EncodeStringOp.NOT_MATCH);
            case STRING_EQUALS_IGNORE_CASE, STRING_EQUALS_IGNORE_CASE_IF_EXISTS, FOR_ANY_VALUE_STRING_EQUALS_IGNORE_CASE, FOR_ALL_VALUES_STRING_EQUALS_IGNORE_CASE
                    -> encodeStringOp(termManager, keyToValues, keyToTerm, EncodeStringOp.EQUALS_IGNORE_CASE);
            case STRING_NOT_EQUALS_IGNORE_CASE, STRING_NOT_EQUALS_IGNORE_CASE_IF_EXISTS, FOR_ANY_VALUE_STRING_NOT_EQUALS_IGNORE_CASE, FOR_ALL_VALUES_STRING_NOT_EQUALS_IGNORE_CASE
                    -> encodeStringOp(termManager, keyToValues, keyToTerm, EncodeStringOp.NOT_EQUALS_IGNORE_CASE);

            case IP_ADDRESS, IP_ADDRESS_IF_EXISTS -> encodeBitVecOp(termManager,keyToValues, keyToTerm, EncodeBitVecOp.IP);
            case NOT_IP_ADDRESS, NOT_IP_ADDRESS_IF_EXISTS -> encodeBitVecOp(termManager,keyToValues, keyToTerm, EncodeBitVecOp.NOT_IP);
        };
    }

    private enum EncodeStringOp {
        EQUALS, NOT_EQUALS, EQUALS_IGNORE_CASE, NOT_EQUALS_IGNORE_CASE, MATCH, NOT_MATCH
    }

    private static Term encodeStringOp(
            TermManager termManager,
            Map<String, Set<String>> keyToValues,
            Map<String, Term> keyToTerm,
            EncodeStringOp op
    ) throws CVC5ApiException {
        Set<String> relevantKeys = keyToValues.keySet();

        Sort stringSort = termManager.getStringSort();
        Map<String, Term> stringTerms = extractAndValidateSorts(keyToTerm, relevantKeys, stringSort);

        return switch (op) {
            case EQUALS -> stringEqualsEncode(termManager, keyToValues, stringTerms);
            case NOT_EQUALS -> stringNotEqualsEncode(termManager, keyToValues, stringTerms);
            case MATCH -> stringMatchEncode(termManager, keyToValues, stringTerms);
            case NOT_MATCH -> stringNotMatchEncode(termManager, keyToValues, stringTerms);
            case EQUALS_IGNORE_CASE -> stringEqualsIgnoreCaseEncode(termManager, keyToValues, stringTerms);
            case NOT_EQUALS_IGNORE_CASE -> stringNotEqualsIgnoreCaseEncode(termManager, keyToValues, stringTerms);
        };
    }

    private enum EncodeBitVecOp {
        IP, NOT_IP
    }

    private  static Term encodeBitVecOp(
            TermManager termManager,
            Map<String, Set<String>> keyToValues,
            Map<String, Term> keyToTerm,
            EncodeBitVecOp op
    ) throws CVC5ApiException {
        Set<String> relevantKeys = keyToValues.keySet();

        Sort bitVecSort = termManager.mkBitVectorSort(32);
        Map<String, Term> bitVecTerms = extractAndValidateSorts(keyToTerm, relevantKeys, bitVecSort);

        return switch (op) {
            case IP -> ipAddressEncode(termManager, keyToValues, bitVecTerms);
            case NOT_IP -> notIpAddressEncode(termManager, keyToValues, bitVecTerms);
        };
    }

    private static Map<String, Term> extractAndValidateSorts(Map<String, Term> sourceMap, Set<String> relevantKeys, Sort expectedSort) {
        Map<String, Term> result = new HashMap<>();
        for (String key : relevantKeys) {
            Term term = sourceMap.get(key);
            if (term == null) {
                throw new IllegalArgumentException("Missing expression for key: " + key);
            }

            Sort actualSort = term.getSort();
            if (!actualSort.equals(expectedSort)) {
                throw new IllegalArgumentException("Sort mismatch for key: " + key);
            }

            result.put(key, term);
        }
        return result;
    }

    private static Term stringEqualsEncode(
            TermManager termManager,
            Map<String, Set<String>> keyToValues,
            Map<String, Term> keyToTerm
     ) {
        Term result = null;
        for (Map.Entry<String, Set<String>> entry : keyToValues.entrySet()) {
            String key = entry.getKey();
            Term term = keyToTerm.get(key);
            Term temp = null;
            for (String value : entry.getValue()) {
                Term eq = termManager.mkTerm(Kind.EQUAL, term, termManager.mkString(value));
                temp = temp == null
                        ? eq
                        : termManager.mkTerm(Kind.OR, temp, eq);
            }
            if (temp == null) {
                continue;
            }
            if (result == null) {
                result = temp;
            } else {
                result = termManager.mkTerm(Kind.AND, result, temp);
            }
            if (result == null) {
                result = termManager.mkTrue();
            }
        }
        return result;
    }

    private static Term stringNotEqualsEncode(
            TermManager termManager,
            Map<String, Set<String>> keyToValues,
            Map<String, Term> keyToTerm
    ) {
        return termManager.mkTerm(Kind.NOT, stringEqualsEncode(termManager, keyToValues, keyToTerm));
    }

    private static Term stringEqualsIgnoreCaseEncode(
            TermManager termManager,
            Map<String, Set<String>> keyToValues,
            Map<String, Term> keyToTerm
    ) {
        Term result = null;
        for (Map.Entry<String, Set<String>> entry : keyToValues.entrySet()) {
            String key = entry.getKey();
            Term term = termManager.mkTerm(Kind.STRING_TO_LOWER, keyToTerm.get(key));
            Term temp = null;
            for (String value : entry.getValue()) {
                Term eq = termManager.mkTerm(Kind.EQUAL, term, termManager.mkString(value.toLowerCase()));
                temp = temp == null
                        ? eq
                        : termManager.mkTerm(Kind.OR, temp, eq);
            }
            if (temp == null) {
                continue;
            }
            if (result == null) {
                result = temp;
            } else {
                result = termManager.mkTerm(Kind.AND, result, temp);
            }
        }
        if (result == null) {
            result = termManager.mkTrue();
        }
        return result;
    }

    private static Term stringNotEqualsIgnoreCaseEncode(
            TermManager termManager,
            Map<String, Set<String>> keyToValues,
            Map<String, Term> keyToTerm
    ) {
        return termManager.mkTerm(Kind.NOT, stringEqualsIgnoreCaseEncode(termManager, keyToValues, keyToTerm));
    }

    private static Term stringMatchEncode(
            TermManager termManager,
            Map<String, Set<String>> keyToValues,
            Map<String, Term> keyToTerm
    ) throws CVC5ApiException {
        Term result = null;
        for (Map.Entry<String, Set<String>> entry : keyToValues.entrySet()) {
            String key = entry.getKey();
            Term term = keyToTerm.get(key);
            Term temp = null;
            for (String value : entry.getValue()) {
                Term match = commonEncode(termManager, value, term);
                temp = temp == null
                        ? match
                        : termManager.mkTerm(Kind.OR, temp, match);
            }
            if (temp == null) {
                continue;
            }
            if (result == null) {
                result = temp;
            } else {
                result = termManager.mkTerm(Kind.AND, result, temp);
            }
        }
        if (result == null) {
            result = termManager.mkTrue();
        }
        return result;
    }

    private static Term stringNotMatchEncode(
            TermManager termManager,
            Map<String, Set<String>> keyToValues,
            Map<String, Term> keyToTerm
    ) throws CVC5ApiException{
        return termManager.mkTerm(Kind.NOT, stringMatchEncode(termManager, keyToValues, keyToTerm));
    }

    private static Term ipAddressEncode(
            TermManager termManager,
            Map<String, Set<String>> keyToValue,
            Map<String, Term> keyToTerm
    ) throws CVC5ApiException {
        Term result = null;
        for (Map.Entry<String, Set<String>> entry : keyToValue.entrySet()) {
            String key = entry.getKey();
            Term term = keyToTerm.get(key);
            Term temp = null;
            for (String value : entry.getValue()) {
                temp = temp == null
                        ? cidrEncode(termManager, value, term)
                        : termManager.mkTerm(Kind.OR, temp, cidrEncode(termManager, value, term));
            }
            if (temp == null) {
                continue;
            }
            if (result == null) {
                result = temp;
            } else {
                result = termManager.mkTerm(Kind.AND, result, temp);
            }
        }
        if (result == null) {
            result = termManager.mkTrue();
        }
        return result;
    }

    private static Term notIpAddressEncode(
            TermManager termManager,
            Map<String, Set<String>> keyToValue,
            Map<String, Term> keyToTerm
    ) throws CVC5ApiException {
        return termManager.mkTerm(Kind.NOT, ipAddressEncode(termManager, keyToValue, keyToTerm));
    }
}
