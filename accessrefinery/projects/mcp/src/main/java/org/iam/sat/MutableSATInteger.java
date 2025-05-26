package org.iam.sat;

import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Variable;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.iam.sat.SATUtils.bitvector;

/**
 * MutableSATInteger represents a mutable bit-vector for SAT-based integer encoding.
 * <p>
 * Provides methods for constructing, copying, and manipulating SAT integer variables,
 * as well as converting SAT assignments to integer values.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public final class MutableSATInteger extends SATInteger {

    /**
     * List of variables set to true in the current context.
     */
    private transient List<Variable> _trues;

    /**
     * List of variables set to false in the current context.
     */
    private transient List<Variable> _falses;

    /**
     * Creates a MutableSATInteger from a bitvector index.
     *
     * @param factory the formula factory
     * @param length  the number of bits
     * @param start   the starting index
     * @param reverse whether to reverse the bit order
     * @return a new MutableSATInteger
     */
    public static MutableSATInteger makeFromIndex(
        FormulaFactory factory, int length, int start, boolean reverse) {
        return new MutableSATInteger(factory, bitvector(factory, length, start, reverse));
    }

    /**
     * Constructs a MutableSATInteger from a bit vector.
     *
     * @param factory the formula factory
     * @param bitvec  the bit vector
     */
    public MutableSATInteger(FormulaFactory factory, Variable[] bitvec) {
        super(factory, bitvec);
        initTransientFields();
    }

    /**
     * Constructs a MutableSATInteger with the given length, initializing all bits as free variables.
     *
     * @param factory the formula factory
     * @param length  the number of bits
     */
    public MutableSATInteger(FormulaFactory factory, int length) {
        this(factory, new Variable[length]);
        // Initialize all bits to "don't care" (free variables)
        for (int i = 0; i < length; i++) {
            _bitvec[i] = factory.variable("bit_" + i);
        }
    }

    /**
     * Copy constructor.
     *
     * @param other the MutableSATInteger to copy
     */
    public MutableSATInteger(MutableSATInteger other) {
        this(other._factory, other._bitvec.length);
    }

    /**
     * Initializes the transient fields for tracking true/false variables.
     */
    private void initTransientFields() {
        _trues = new ArrayList<>(_bitvec.length);
        _falses = new ArrayList<>(_bitvec.length);
    }

    /**
     * Converts a SAT assignment to a long value.
     *
     * @param assignment the SAT assignment
     * @return the corresponding long value
     */
    @Override
    public long satAssignmentToLong(Assignment assignment) {
        long value = 0;

        for (int i = 0; i < _bitvec.length; i++) {
            Formula bitFormula = _bitvec[i];
            if (bitFormula instanceof Variable) {
                Variable var = (Variable) bitFormula;
                if (assignment.positiveVariables().contains(var)) {
                    value |= 1L << (_bitvec.length - i - 1);
                }
            } else if (bitFormula == _factory.verum()) {
                value |= 1L << (_bitvec.length - i - 1);
            }
        }
        
        return value;
    }

    /**
     * Returns a formula representing the first {@code length} bits equal to the given value.
     *
     * @param value  the value to compare
     * @param length the number of bits to check
     * @return the formula representing the equality
     */
    @Override
    protected Formula firstBitsEqual(long value, int length) {
        checkArgument(length <= _bitvec.length, "Not enough bits");
        checkState(_trues.isEmpty(), "Unexpected array state");
        checkState(_falses.isEmpty(), "Unexpected array state");

        long val = value >> (_bitvec.length - length);
        for (int i = length - 1; i >= 0; i--) {
            boolean bitValue = (val & 1) == 1;
            if (bitValue) {
                _trues.add((Variable) _bitvec[i]);
            } else {
                _falses.add((Variable) _bitvec[i]);
            }
            val >>= 1;
        }

        Formula result;
        if (!_trues.isEmpty() && !_falses.isEmpty()) {
            result = _factory.and(
                    _factory.and(_trues),
                    _factory.not(_factory.or(_falses))
            );
        } else if (!_trues.isEmpty()) {
            result = _factory.and(_trues);
        } else if (!_falses.isEmpty()) {
            result = _factory.not(_factory.or(_falses));
        } else {
            result = _factory.verum();
        }

        _trues.clear();
        _falses.clear();
        return result;
    }

    /**
     * Checks equality with another object.
     *
     * @param o the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MutableSATInteger)) {
            return false;
        }
        MutableSATInteger other = (MutableSATInteger) o;
        return Arrays.equals(_bitvec, other._bitvec);
    }

    /**
     * Returns the hash code for this MutableSATInteger.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(_bitvec);
    }
}