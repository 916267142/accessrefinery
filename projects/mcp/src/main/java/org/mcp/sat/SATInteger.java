package org.mcp.sat;

import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Variable;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Abstract base class for SAT-based integer encodings.
 * <p>
 * Provides methods for encoding, decoding, and querying integer values using SAT formulas.
 * Supports conversion between SAT assignments and integer values, and value constraints.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public abstract class SATInteger implements Serializable {
    /**
     * Formula factory for creating SAT formulas.
     */
    protected final FormulaFactory _factory;

    /**
     * Bit vector representing the integer (each bit is a SAT variable).
     */
    protected final Variable[] _bitvec;

    /**
     * Maximum value representable by this SATInteger.
     */
    protected final long _maxVal;

    /**
     * Constructs a SATInteger from a formula factory and bit vector.
     *
     * @param factory the formula factory
     * @param bitvec  the bit vector (array of SAT variables)
     */
    protected SATInteger(FormulaFactory factory, Variable[] bitvec) {
        checkArgument(bitvec.length < 64, "Only lengths up to 63 are supported");
        _factory = factory;
        _bitvec = bitvec;
        _maxVal = bitvec.length == 0 ? 0L : 0xFFFF_FFFF_FFFF_FFFFL >>> (64 - bitvec.length);
    }

    /**
     * Returns the number of bits in this SATInteger.
     *
     * @return the number of bits
     */
    public int size() {
        return _bitvec.length;
    }

    /**
     * Converts a SAT assignment to a long value.
     *
     * @param assignment the SAT assignment
     * @return the corresponding long value
     */
    public abstract long satAssignmentToLong(Assignment assignment);

    /**
     * Converts a SAT assignment to an int value.
     * Only supports up to 31 bits.
     *
     * @param formula the SAT formula assignment
     * @return the corresponding int value
     */
    public int getValueSatisfyingToInt(Formula formula) {
        checkArgument(
                _bitvec.length <= 31,
                "Only SATInteger of 31 or fewer bits can be converted to int");

        return (int) satAssignmentToLong(SATUtils.getAssignment(_factory, formula, _bitvec));
    }

    /**
     * Returns the value satisfying the given formula, or 0 if unsatisfiable.
     *
     * @param formula the SAT formula assignment
     * @return the value as a Long, or 0L if unsatisfiable
     */
    public Long getValueSatisfying(Formula formula) {
        if (!SATUtils.isSatisfying(_factory, formula)) {
            return 0L;
        }
        long val = satAssignmentToLong(SATUtils.getAssignment(_factory, formula, _bitvec));
        return val;
    }

    /**
     * Returns a list of values satisfying the input formula, up to a maximum number.
     *
     * @param formula the SAT formula constraint
     * @param max     the maximum number of values desired
     * @return the list of satisfying values
     */
    public List<Long> getValuesSatisfying(Formula formula, int max) {
        ImmutableList.Builder<Long> values = new ImmutableList.Builder<>();

        checkArgument(max > 0, "max must be > 0");

        int num = 0;
        Formula pred = formula;
        while (num < max) {
            if (!SATUtils.isSatisfying(_factory, pred)) {
                break;
            }
            long val = satAssignmentToLong(SATUtils.getAssignment(_factory, pred, _bitvec));
            values.add(val);
            pred = _factory.and(pred, _factory.not(value(val)));
            num++;
        }
        return values.build();
    }

    /**
     * Returns a formula representing the exact value.
     *
     * @param val the value to encode
     * @return the formula representing the value
     */
    public final Formula value(long val) {
        checkArgument(val >= 0, "value is negative");
        checkArgument(val <= _maxVal, "value %s is out of range [0, %s]", val, _maxVal);
        return firstBitsEqual(val, _bitvec.length);
    }

    /**
     * Returns the formula factory.
     *
     * @return the formula factory
     */
    public FormulaFactory getFactory() {
        return _factory;
    }

    /**
     * Returns the bit vector.
     *
     * @return the bit vector (array of SAT variables)
     */
    public Variable[] getBitvec() {
        return _bitvec;
    }

    /**
     * Returns a formula representing the first {@code length} bits equal to the given value.
     *
     * @param val    the value to compare
     * @param length the number of bits to check
     * @return the formula representing the equality
     */
    protected abstract Formula firstBitsEqual(long val, int length);
}