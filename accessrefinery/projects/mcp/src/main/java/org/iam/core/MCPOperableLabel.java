package org.iam.core;

import org.iam.variables.dynamics.OperableLabel;

/**
 * MCPOperableLabel wraps an MCPBitVector and provides set operations (union, intersection, difference)
 * for use in dynamic symbolic computations.
 * <p>
 * This class implements the OperableLabel interface, enabling symbolic manipulation of sets of constraints
 * using BDD or SAT representations.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class MCPOperableLabel extends OperableLabel {
    /**
     * The underlying symbolic value (BDD or SAT) for this label.
     */
    private final MCPBitVector value;

    /**
     * Copy constructor.
     *
     * @param other the MCPOperableLabel to copy
     */
    public MCPOperableLabel(MCPOperableLabel other) {
        this.value = other.value;
    }

    /**
     * Returns a copy of this MCPOperableLabel.
     *
     * @return a new MCPOperableLabel with the same value
     */
    public MCPOperableLabel copy() {
        return new MCPOperableLabel(this.value);
    }

    /**
     * Constructs a new MCPOperableLabel with the given symbolic value.
     *
     * @param value the MCPBitVector value
     */
    public MCPOperableLabel(MCPBitVector value) {
        this.value = value;
    }

    /**
     * Returns the factory (BDDFactory or FormulaFactory) associated with this label.
     *
     * @return the factory object
     */
    public Object getFactory() {
        return value.getFactory();
    }

    /**
     * Sets the factory (BDDFactory or FormulaFactory) for this label.
     *
     * @param factory the factory object
     */
    public void setFactory(Object factory) {
        this.value.setFactory(factory);
    }

    /**
     * Returns the union of this label and another.
     *
     * @param other the other OperableLabel
     * @return a new MCPOperableLabel representing the union
     */
    @Override
    public OperableLabel union(OperableLabel other) {
        MCPOperableLabel otherBddVar = (MCPOperableLabel) other;
        return new MCPOperableLabel(this.value.or(otherBddVar.value));
    }

    /**
     * Returns the intersection of this label and another.
     *
     * @param other the other OperableLabel
     * @return a new MCPOperableLabel representing the intersection
     */
    @Override
    public OperableLabel inter(OperableLabel other) {
        MCPOperableLabel otherBddVar = (MCPOperableLabel) other;
        return new MCPOperableLabel(this.value.and(otherBddVar.value));
    }

    /**
     * Returns the difference of this label and another (this \ other).
     *
     * @param other the other OperableLabel
     * @return a new MCPOperableLabel representing the difference
     */
    @Override
    public OperableLabel minus(OperableLabel other) {
        MCPOperableLabel otherBddVar = (MCPOperableLabel) other;
        return new MCPOperableLabel(this.value.and(otherBddVar.value.not()));
    }

    /**
     * Returns the underlying symbolic value.
     *
     * @return the MCPBitVector value
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * Checks equality with another object.
     *
     * @param o the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCPOperableLabel that = (MCPOperableLabel) o;
        return value.equals(that.value);
    }

    /**
     * Returns the hash code for this label.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Returns a string representation of this label.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return "MCPOperableLabel{" +
                "value=" + value +
                '}';
    }

    /**
     * Checks if this label represents the empty set.
     *
     * @return true if empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return value.isZero();
    }
}
