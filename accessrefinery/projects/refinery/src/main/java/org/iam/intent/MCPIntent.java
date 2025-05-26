package org.iam.intent;

import com.google.common.collect.ImmutableSet;

import org.iam.core.MCPBitVector;
import org.iam.core.MCPFactory;
import org.iam.variables.statics.LabelType;
import org.batfish.datamodel.Prefix;
import org.iam.model.DomainLabelTrees;
import org.iam.policy.model.MCPCondition;
import org.iam.utils.Parameter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * MCPIntent represents a finding with domain values and symbolic encoding.
 * <p>
 * Encapsulates domain values, supports refinement, and provides symbolic encoding
 * using MCPBitVector. Used for intent mining, reduction, and merging.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class MCPIntent {
    /**
     * Domain label trees for all MCPIntent instances.
     */
    private static DomainLabelTrees _domainLabelTrees;
    /**
     * MCPFactory for symbolic encoding.
     */
    private static MCPFactory _mcpFactory;
    /**
     * Domain values for this intent.
     */
    private final HashMap<String, Object> _domainValues;
    /**
     * Symbolic encoding node for this intent.
     */
    private MCPBitVector _mcpNode;

    /**
     * Default constructor. Initializes domain label trees and domain values.
     */
    public MCPIntent() {
        _domainLabelTrees = new DomainLabelTrees();
        _domainValues = new HashMap<>();
    }

    /**
     * Copy constructor. Copies domain values from another MCPIntent.
     *
     * @param other the MCPIntent to copy
     * @throws IllegalArgumentException if the other object is null
     */
    public MCPIntent(MCPIntent other) {
        if (other == null) {
            throw new IllegalArgumentException("Other object cannot be null");
        }
        this._domainValues = new HashMap<>(other._domainValues);
        this._mcpNode = null;
    }

    /**
     * Sets the domain label trees for all MCPIntent instances.
     *
     * @param domainLabelTrees the DomainLabelTrees instance
     */
    static public void setDomainLabelTrees(DomainLabelTrees domainLabelTrees) {
        _domainLabelTrees = domainLabelTrees;
    }

    /**
     * Sets the MCPFactory for all MCPIntent instances.
     *
     * @param cube the MCPFactory instance
     */
    static public void setMCPLabelsFactory(MCPFactory cube) {
        _mcpFactory = cube;
    }

    /**
     * Creates and returns the root intent, initializing domain values for all domains.
     *
     * @param cubeFactory the MCPFactory instance
     * @return the root MCPIntent
     */
    static public MCPIntent getRootFinding(MCPFactory cubeFactory) {
        MCPIntent finding = new MCPIntent();
        MCPIntent._mcpFactory = cubeFactory;
        if(Parameter.isSplitLabel) {
            _mcpFactory.getDomainNames().forEach((key, type) -> {
                LabelType labelType = _mcpFactory.getDomainNames().get(key);
                switch (labelType) {
                    case PREFIX:
                        finding.setDomainValue(key, "1.0.0.0/0");
                        break;
                    default:
                        finding.setDomainValue(key, ".*");
                        break;
                }
            });
        } else {
            _mcpFactory.getDomainNames().forEach((key, type) -> {
                LabelType labelType = _mcpFactory.getDomainNames().get(key);
                switch (labelType) {
                    case PREFIX_SET:
                        finding.setDomainValue(key, ImmutableSet.of("1.0.0.0/0"));
                        break;
                    default:
                        finding.setDomainValue(key, ImmutableSet.of(".*"));
                        break;
                }
            });
        }
        return finding;
    }

    /**
     * Refines the current intent into a set of more specific intents.
     *
     * @return a set of refined MCPIntent objects
     */
    @SuppressWarnings("unchecked")
    public HashSet<MCPIntent> refines() {
        HashSet<MCPIntent> refines = new HashSet<>();
        _domainValues.forEach((key, value) -> {
            LabelType labelType = _mcpFactory.getDomainNames().get(key);
            switch (labelType) {
                case PREFIX:
                    Prefix prefix = Prefix.parse(MCPCondition.processPrefix((String)value));
                    MCPIntent._domainLabelTrees.getMaximumLabels(key, prefix).forEach(label -> {
                        MCPIntent finding = new MCPIntent(this);
                        finding.setDomainValue(key, label.toString());
                        refines.add(finding);
                    });
                    break;
                case PREFIX_SET:
                    List<Prefix> prefixes = ((ImmutableSet<String>) value).stream()
                            .map(MCPCondition::processPrefix)
                            .map(Prefix::parse)
                            .toList();
                    MCPIntent._domainLabelTrees.getMaximumLabels(key, ImmutableSet.copyOf(prefixes)).forEach(label -> {
                        MCPIntent finding = new MCPIntent(this);
                        finding.setDomainValue(key, ((ImmutableSet<Prefix>) label).stream().map(v->v.toString()).collect(ImmutableSet.toImmutableSet()));
                        refines.add(finding);
                    });
                    break;
                default:
                    MCPIntent._domainLabelTrees.getMaximumLabels(key, value).forEach(label -> {
                        MCPIntent finding = new MCPIntent(this);
                        finding.setDomainValue(key, label);
                        refines.add(finding);
                    });
                    break;
            };});
        return refines;
    }

    /**
     * Gets the symbolic encoding node for this intent.
     * If not yet calculated, computes it as the conjunction of all domain values.
     *
     * @return the MCPBitVector node
     */
    @SuppressWarnings("unchecked")
    public MCPBitVector getMCPNode() {
        if(_mcpNode != null) {
            return _mcpNode;
        }
        _mcpNode = _domainValues.entrySet().stream()
                .map(entry -> {
                    LabelType labelType = _mcpFactory.getDomainNames().get(entry.getKey());
                    switch (labelType) {
                        case PREFIX:
                            return _mcpFactory.getVar(entry.getKey(), Prefix.parse(MCPCondition.processPrefix((String)entry.getValue())));
                        case PREFIX_SET:
                            List<Prefix> prefixes = ((ImmutableSet<String>) entry.getValue()).stream()
                            .map(MCPCondition::processPrefix)
                            .map(Prefix::parse)
                            .toList();                            
                            return _mcpFactory.getVar(entry.getKey(), ImmutableSet.copyOf(prefixes));
                        default:
                            return _mcpFactory.getVar(entry.getKey(), entry.getValue());
                    }
                })
                .reduce(_mcpFactory.getTrue(), MCPBitVector::and);
        return _mcpNode;
    }

    /**
     * Sets the value for a specific domain.
     *
     * @param domain the domain name
     * @param value  the value to set
     */
    public void setDomainValue(String domain, Object value) {
        this._domainValues.put(domain, value);
    }

    /**
     * Returns the domain values for this intent.
     *
     * @return the domain values map
     */
    public HashMap<String, Object> getDomainValues() {
        return _domainValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; 
        if (o == null || getClass() != o.getClass()) return false; 

        MCPIntent myClass = (MCPIntent) o;

        return deepEquals(this._domainValues, myClass._domainValues);
    }

    @Override
    public int hashCode() {
        return deepHashCode(this._domainValues);
    }

    private boolean deepEquals(Map<String, Object> map1, Map<String, Object> map2) {
        if (map1 == map2) return true; 
        if (map1 == null || map2 == null || map1.size() != map2.size()) return false;

        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            String key = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = map2.get(key);

            if (value2 == null || !deepEqualsObjects(value1, value2)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean deepEqualsObjects(Object obj1, Object obj2) {
        if (obj1 == obj2) return true; 
        if (obj1 == null || obj2 == null) return false;

        if (obj1 instanceof Map && obj2 instanceof Map) {
            return deepEquals((Map<String, Object>) obj1, (Map<String, Object>) obj2);
        }

        if (obj1 instanceof Object[] && obj2 instanceof Object[]) {
            return deepEqualsArrays((Object[]) obj1, (Object[]) obj2);
        }

        return obj1.equals(obj2);
    }

    private boolean deepEqualsArrays(Object[] arr1, Object[] arr2) {
        if (arr1 == arr2) return true; 
        if (arr1 == null || arr2 == null || arr1.length != arr2.length) return false;

        for (int i = 0; i < arr1.length; i++) {
            if (!deepEqualsObjects(arr1[i], arr2[i])) {
                return false;
            }
        }
        return true;
    }

    private int deepHashCode(Map<String, Object> map) {
        if (map == null) return 0;

        int result = 1;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            result = 31 * result + (key == null ? 0 : key.hashCode());
            result = 31 * result + deepHashCodeObject(value);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private int deepHashCodeObject(Object obj) {
        if (obj == null) return 0;

        if (obj instanceof Map) {
            return deepHashCode((Map<String, Object>) obj);
        }

        if (obj instanceof Object[]) {
            return deepHashCodeArray((Object[]) obj);
        }

        return obj.hashCode();
    }

    private int deepHashCodeArray(Object[] arr) {
        if (arr == null) return 0;

        int result = 1;
        for (Object element : arr) {
            result = 31 * result + deepHashCodeObject(element);
        }
        return result;
    }

    /**
     * Returns a string representation of this intent.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return "Finding{" +
                "_domainValues=" + _domainValues +
                '}';
    }
}