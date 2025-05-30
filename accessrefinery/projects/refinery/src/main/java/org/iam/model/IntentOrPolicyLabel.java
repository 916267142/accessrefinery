package org.iam.model;

import org.iam.intent.MCPIntent;
import org.iam.policy.model.MCPPolicy;
import org.iam.variables.dynamics.OperableLabel;
import org.iam.variables.statics.Label;
import org.iam.core.MCPBitVector;
import org.iam.core.MCPOperableLabel;


/**
 * IntentOrPolicyLabel is a Label that can wrap a finding, a policy, or an MCPBitVector.
 * <p>
 * Supports conversion to OperableLabel and provides type-safe access to the underlying value.
 * Used for symbolic manipulation and EC computation.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class IntentOrPolicyLabel extends Label {
    /**
     * The wrapped finding, if this label represents a finding.
     */
    private MCPIntent finding;
    /**
     * The wrapped policy, if this label represents a policy.
     */
    private MCPPolicy policy;
    /**
     * The wrapped MCPBitVector, if this label represents a symbolic value.
     */
    private MCPBitVector MCPBitVector;
    /**
     * The type of value this label represents.
     */
    private VarType varType;
    /**
     * Cached OperableLabel for efficient conversion.
     */
    private OperableLabel cachedOperableLabel;

    private IntentOrPolicyLabel(Builder builder) {
        this.finding = builder.finding;
        this.policy = builder.policy;
        this.MCPBitVector = builder.MCPBitVector;
        this.varType = builder.varType;
    }

    /**
     * Builder for IntentOrPolicyLabel.
     */
    public static class Builder {
        private MCPIntent finding;
        private MCPPolicy policy;
        private MCPBitVector MCPBitVector;
        private VarType varType;

        public Builder setPolicy(MCPPolicy policy) {
            this.policy = policy;
            this.varType = VarType.POLICY;
            return this;
        }

        public Builder setFinding(MCPIntent finding) {
            this.finding = finding;
            this.varType = VarType.FINDING;
            return this;
        }

        public Builder setMCP(MCPBitVector MCPBitVector) {
            this.MCPBitVector = MCPBitVector;
            this.varType = VarType.MCPBitVector;
            return this;
        }

        public IntentOrPolicyLabel build() {
            return new IntentOrPolicyLabel(this);
        }
    }

    /**
     * Enum for the type of value this label represents.
     */
    public enum VarType {
        FINDING,
        POLICY,
        MCPBitVector
    }

    @Override
    public int hashCode() {
        switch (varType) {
            case FINDING:
                return finding.hashCode();
            case POLICY:
                return policy.hashCode();
            case MCPBitVector:
                return MCPBitVector.hashCode();
            default:
                throw new IllegalStateException("Unexpected value: " + varType);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntentOrPolicyLabel that = (IntentOrPolicyLabel) o;
        if (this.varType != that.varType) return false;
        switch (varType) {
            case FINDING:
                return finding.equals(that.finding);
            case POLICY:
                return policy.equals(that.policy);
            case MCPBitVector:
                return MCPBitVector.equals(that.MCPBitVector);
            default:
                throw new IllegalStateException("Unexpected value: " + varType);
        }
    }

    /**
     * Converts this label to an OperableLabel for symbolic operations.
     *
     * @return the corresponding OperableLabel
     */
    @Override
    public OperableLabel convert() {
        if (cachedOperableLabel != null) {
            return cachedOperableLabel;
        }

        MCPBitVector MCPBitVector = null;
        switch (varType) {
            case FINDING:
                MCPBitVector = finding.getMCPNode();
                break;
            case POLICY:
                MCPBitVector = policy.getMCPNode();
                break;
            case MCPBitVector:
                MCPBitVector = this.MCPBitVector;
                break;
        }


        if (cachedOperableLabel != null) {
            return cachedOperableLabel;
        }
        // cachedOperableLabel = OperableLabelFactory.createVar(OperableLabelType.MCPBitVector, MCPBitVector);
        cachedOperableLabel = new MCPOperableLabel(MCPBitVector);

        if (MCPBitVector == null) {
            throw new NullPointerException("MCPBitVector is null");
        }
        ((MCPOperableLabel) cachedOperableLabel).setFactory(MCPBitVector.getFactory());
        return cachedOperableLabel;
    }

    /**
     * Returns the underlying value (finding, policy, or MCPBitVector).
     *
     * @return the wrapped value
     */
    @Override
    public Object getValue() {
        switch (varType) {
            case FINDING:
                return finding;
            case POLICY:
                return policy;
            case MCPBitVector:
                return MCPBitVector;
            default:
                throw new IllegalStateException("Unexpected value: " + varType);
        }
    }

    @Override
    public String toString() {
        switch (varType) {
            case FINDING:
                return "Type: FINDING, Content: ";
            case POLICY:
                return "Type: POLICY, Content: " + policy.toString();
            case MCPBitVector:
                return "Type: MCPBitVector, Content: " + MCPBitVector.toString();
            default:
                throw new IllegalStateException("Unexpected value: " + varType);
        }
    }

    /**
     * Returns the type of value this label represents.
     *
     * @return the VarType
     */
    public VarType getVarType() {
        return varType;
    }
}