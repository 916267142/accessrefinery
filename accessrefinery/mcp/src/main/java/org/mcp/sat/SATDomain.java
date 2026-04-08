package org.mcp.sat;

import com.google.common.math.IntMath;

import javax.annotation.Nonnull;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import java.math.RoundingMode;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * SATDomain wraps a SATInteger around a finite collection of values and provides an API
 * for working directly with those values in SAT-based symbolic computations.
 * <p>
 * Supports encoding, decoding, and value lookup for SAT-based domains.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public final class SATDomain<T> {

  /**
   * Formula factory for creating SAT formulas.
   */
  private final @Nonnull FormulaFactory _factory;

  /**
   * List of all values in this domain.
   */
  private final @Nonnull List<T> _values;

  /**
   * The underlying SAT integer representation.
   */
  private @Nonnull MutableSATInteger _integer;

  /**
   * Constructs a SATDomain from a formula factory, value list, and bit index offset.
   *
   * @param factory the formula factory
   * @param values  the list of values in the domain
   * @param index   the starting bit index
   */
  public SATDomain(FormulaFactory factory, List<T> values, int index) {
    int bits = numBits(values.size());
    _factory = factory;
    _values = values;
    _integer = MutableSATInteger.makeFromIndex(_factory, bits, index, false);
  }

  /**
   * Copy constructor.
   *
   * @param other the SATDomain to copy
   */
  public SATDomain(SATDomain<T> other) {
    _factory = other._factory;
    _values = other._values;
    _integer = new MutableSATInteger(other._integer);
  }

  /**
   * Returns the number of bits used to represent a domain of the given size.
   *
   * @param size the number of elements in the domain
   * @return the number of bits required
   */
  public static int numBits(int size) {
    if (size == 0) {
      return 0;
    }
    return IntMath.log2(size, RoundingMode.CEILING);
  }

  /**
   * Returns a formula representing the assignment of the given value.
   *
   * @param value the value to encode
   * @return the formula representing the value
   */
  public Formula value(T value) {
    int idx = _values.indexOf(value);
    checkArgument(idx != -1, "%s is not in the domain %s", value, _values);
    return _integer.value(idx);
  }

  /**
   * Decodes a SAT assignment to the corresponding value in the domain.
   *
   * @param satAssignment the SAT assignment formula
   * @return the value corresponding to the assignment
   */
  public T satAssignmentToValue(Formula satAssignment) {
    int idx = _integer.getValueSatisfyingToInt(satAssignment);
    checkArgument(
        idx < _values.size(),
        "The given assignment is not valid in this domain. Was it restricted to valid values?",
        idx);
    return _values.get(idx);
  }

  /**
   * Returns the underlying MutableSATInteger.
   *
   * @return the MutableSATInteger
   */
  public MutableSATInteger getInteger() {
    return _integer;
  }

  /**
   * Sets the underlying MutableSATInteger.
   *
   * @param i the MutableSATInteger to set
   */
  public void setInteger(MutableSATInteger i) {
    _integer = i;
  }

  /**
   * Checks equality with another object.
   *
   * @param o the object to compare
   * @return true if equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SATDomain<?>)) {
      return false;
    }
    SATDomain<?> other = (SATDomain<?>) o;
    // Values are ignored here, because we should only be checking equality for the same input
    // domain and bit assignment.
    return _integer.equals(other._integer);
  }

  /**
   * Returns the hash code for this SATDomain.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return _integer.hashCode();
  }
}
