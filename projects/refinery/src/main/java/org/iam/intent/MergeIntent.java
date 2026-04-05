package org.iam.intent;

import java.util.HashMap;
import java.util.Set;
import com.google.common.collect.ImmutableSet;

/**
 * MergeIntent is used to merge and manipulate sets of domain values for intent mining.
 * <p>
 * Supports merging compatible intents, checking mergeability, and converting to MCPIntent.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class MergeIntent{

    /**
     * Domain values for this intent (domain name to set of values).
     */
    private final HashMap<String, Set<String>> _domainValues;

    /**
     * Copy constructor.
     *
     * @param intent the MergeIntent to copy
     */
    public MergeIntent(MergeIntent intent) {
        _domainValues = new HashMap<>();
        intent.getDomainValues().forEach((domain, values) -> {
            if (values != null) {
                _domainValues.put(domain, values);
            }
        });
    }

    /**
     * Constructs a MergeIntent from an MCPIntent.
     *
     * @param finding the MCPIntent to convert
     */
    public MergeIntent(MCPIntent finding) {
        _domainValues = new HashMap<>();
        finding.getDomainValues().forEach((domain, values) -> {
            if (values != null) {
                _domainValues.put(domain, ImmutableSet.of((String)values));
            }
        });
    }

    /**
     * Returns the domain values for this intent.
     *
     * @return the domain values map
     */
    public HashMap<String, Set<String>> getDomainValues() {
        return _domainValues;
    }
    
    /**
     * Sets the values for a specific domain.
     *
     * @param domain the domain name
     * @param values the set of values to set
     */
    public void setDomainValues(String domain, Set<String> values) {
        if (values == null) {
            _domainValues.remove(domain);
        } else {
            _domainValues.put(domain, values);
        }
    }

    /**
     * Checks if this intent can be merged with another (differs in exactly one domain).
     *
     * @param intent the other MergeIntent
     * @return true if mergeable, false otherwise
     */
    public Boolean isMerged(MergeIntent intent) {
        int different = 0;
        for (String domain : _domainValues.keySet()) {
            if (!intent.getDomainValues().containsKey(domain)) {
                different++;
            } else {
                Set<String> values = _domainValues.get(domain);
                Set<String> values2 = intent.getDomainValues().get(domain);
                if (!values.equals(values2)) {
                    different++;
                }
            }
        }
        return different == 1;
    }

    /**
     * Merges this intent with another, combining values in the differing domain.
     *
     * @param intent the other MergeIntent
     * @return the merged MergeIntent
     */
    public MergeIntent merge(MergeIntent intent) {
        MergeIntent mergedIntent = new MergeIntent(this);

        for (String domain : _domainValues.keySet()) {
            Set<String> values = _domainValues.get(domain);
            Set<String> values2 = intent.getDomainValues().get(domain);
            if(!values.equals(values2)) {
                Set<String> mergedSet = ImmutableSet.<String>builder()
                        .addAll(values)
                        .addAll(values2)
                        .build();
                HashMap<String, Set<String>> mergedValues = new HashMap<>();
                mergedValues.put(domain, mergedSet);
                mergedIntent.setDomainValues(domain, mergedSet);
                break;
            } 
        }
        return mergedIntent;
    }

    /**
     * Converts this MergeIntent to an MCPIntent.
     *
     * @return the corresponding MCPIntent
     */
    public MCPIntent getFinding() {
        MCPIntent finding = new MCPIntent();
        for (String domain : _domainValues.keySet()) {
            Set<String> values = _domainValues.get(domain);
            if (values != null) {
                finding.setDomainValue(domain, values);
            }
        }
        return finding;
    }

    /**
     * Converts this MergeIntent to an MCPIntent for a specific domain.
     *
     * @param domain the domain name
     * @return the corresponding MCPIntent
     */
    public MCPIntent getFinding(String domain) {
        MCPIntent finding = new MCPIntent();
        Set<String> values = _domainValues.get(domain);
        if (values != null) {
            finding.setDomainValue(domain, values);
        }
        return finding;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (String domain : _domainValues.keySet()) {
            Set<String> values = _domainValues.get(domain);
            if (values != null) {
                hash = 31 * hash + values.hashCode();
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MergeIntent other = (MergeIntent) obj;
        return _domainValues.equals(other._domainValues);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String domain : _domainValues.keySet()) {
            sb.append(domain).append(": ");
            Set<String> values = _domainValues.get(domain);
            if (values != null) {
                sb.append(values.toString());
            } else {
                sb.append("null");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
