package org.mcp.core;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.mcp.core.MCPFactory.MCPType;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

/**
 * Represents a symbolic bit vector for sets of constraints, supporting both BDD and SAT backends.
 * <p>
 * Provides logical operations (and, or, not, implication, etc.) and conversion between BDD and SAT representations.
 * Each instance is associated with a backend type and the corresponding factory.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class MCPBitVector {
    private BDD bddValue;
    private Formula satValue;
    private MCPType type;
    private BDDFactory bddFactory;
    private FormulaFactory satFactory;

    /**
     * Constructs a new MCPBitVector with the given value and backend type.
     *
     * @param value the underlying value (BDD or Formula)
     * @param type  the backend type (BDD or SAT)
     */
    public MCPBitVector(Object value, MCPType type) {
        if (type == MCPType.BDD) {
            this.bddValue = (BDD) value;
        } else {
            this.satValue = (Formula)value;
            // AdvancedSimplifier simplifier = new AdvancedSimplifier();
            // Formula temp = simplifier.apply(satValue, false);
            // if (temp != null) {
            //     satValue = temp;
            // }
        }
        this.type = type;
    }

    /**
     * Sets the factory (BDDFactory or FormulaFactory) for this bit vector.
     *
     * @param factory the factory object
     */
    public void setFactory(Object factory) {
        if (type == MCPType.BDD) {
            this.bddFactory = (BDDFactory) factory;
        } else {
            this.satFactory = (FormulaFactory) factory;
        }
    }

    /**
     * Returns the factory (BDDFactory or FormulaFactory) associated with this bit vector.
     *
     * @return the factory object
     */
    public Object getFactory() {
        return type == MCPType.BDD ? bddFactory : satFactory;
    }

    private void checkFactory() {
        if (type == MCPType.SAT && satFactory == null) {
            throw new IllegalStateException("Factory not set for MCPBitVector instance");
        }
    }

    private void checkSameType(MCPBitVector other) {
        if (type != other.type) {
            throw new IllegalArgumentException("Cannot operate on MCPs of different types");
        }
    }

    /**
     * Performs a logical OR with another MCPBitVector.
     *
     * @param other the other bit vector
     * @return a new MCPBitVector representing the OR result
     */
    public MCPBitVector or(MCPBitVector other) {
        checkSameType(other);
        checkFactory();
        
        if (type == MCPType.BDD) {
            return new MCPBitVector(this.bddValue.or(other.bddValue), type).withFactory(bddFactory);
        } else {
            Formula result = satFactory.or(this.satValue, other.satValue);
            return new MCPBitVector(result, type).withFactory(satFactory);
        }
    }

    /**
     * Performs a logical AND with another MCPBitVector.
     *
     * @param other the other bit vector
     * @return a new MCPBitVector representing the AND result
     */
    public MCPBitVector and(MCPBitVector other) {
        checkSameType(other);
        checkFactory();
        
        if (type == MCPType.BDD) {
            return new MCPBitVector(this.bddValue.and(other.bddValue), type).withFactory(bddFactory);
        } else {
            Formula result = satFactory.and(this.satValue, other.satValue);
            return new MCPBitVector(result, type).withFactory(satFactory);
        }
    }

    /**
     * Performs an in-place logical AND with another MCPBitVector (modifies this instance for SAT).
     *
     * @param other the other bit vector
     * @return a new MCPBitVector representing the AND result
     */
    public MCPBitVector andWith(MCPBitVector other) {
        checkSameType(other);
        checkFactory();
        
        if (type == MCPType.BDD) {
            return new MCPBitVector(this.bddValue.andWith(other.bddValue), type).withFactory(bddFactory);
        } else {
            Formula result = satFactory.and(this.satValue, other.satValue);
            this.satValue = result;
            return new MCPBitVector(result, type).withFactory(satFactory);
        }
    }

    /**
     * Computes the difference (this AND NOT other) with another MCPBitVector.
     *
     * @param other the other bit vector
     * @return a new MCPBitVector representing the difference
     */
    public MCPBitVector diff(MCPBitVector other) {
        checkSameType(other);
        checkFactory();
        
        if (type == MCPType.BDD) {
            return new MCPBitVector(this.bddValue.and(other.bddValue.not()), type).withFactory(bddFactory);
        } else {
            Formula result = satFactory.and(this.satValue, satFactory.not(other.satValue));
            return new MCPBitVector(result, type).withFactory(satFactory);
        }
    }

    /**
     * Computes the in-place difference (this AND NOT other) with another MCPBitVector (modifies this instance for SAT).
     *
     * @param other the other bit vector
     * @return a new MCPBitVector representing the difference
     */
    public MCPBitVector diffWith(MCPBitVector other) {
        checkSameType(other);
        checkFactory();
        
        if (type == MCPType.BDD) {
            return new MCPBitVector(this.bddValue.andWith(other.bddValue.not()), type).withFactory(bddFactory);
        } else {
            Formula result = satFactory.and(this.satValue, satFactory.not(other.satValue));
            this.satValue = result;
            return new MCPBitVector(result, type).withFactory(satFactory);
        }
    }

    /**
     * Returns a new MCPBitVector with the same value and the given factory set.
     *
     * @param factory the factory to set
     * @return a new MCPBitVector with the factory set
     */
    public MCPBitVector withFactory(Object factory) {
        MCPBitVector newMCP = new MCPBitVector(getValue(), type);
        newMCP.setFactory(factory);
        return newMCP;
    }

    /**
     * Returns the underlying value (BDD or Formula) of this bit vector.
     *
     * @return the underlying value
     */
    public Object getValue() {
        return type == MCPType.BDD ? bddValue : satValue;
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
        MCPBitVector that = (MCPBitVector) o;
        if (type != that.type) return false;
    
        if (type == MCPType.BDD) {
            return bddValue.equals(that.bddValue);
        } else {
            // Formula equivalence = satFactory.equivalence(this.satValue, that.satValue);
            // return equivalence.isTautology();
            return satValue.equals(that.satValue);
        }
    }

    /**
     * Returns the hash code for this bit vector.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        if (type == MCPType.BDD) {
            return bddValue.hashCode();
        } else {
            return satValue.hashCode();
            // AdvancedSimplifier simplifier = new AdvancedSimplifier();
            // Formula temp = simplifier.apply(satValue.cnf(), false);
            // if(temp == null) {
            //     return 0;
            // } else {
            //     return temp.hashCode();
            // }
        }
    }

    /**
     * Returns a string representation of this bit vector.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return "MCPBitVector{" +
                "type=" + type +
                ", value=" + (type == MCPType.BDD ? bddValue : satValue) +
                '}';
    }

    /**
     * Checks if this bit vector represents logical false (contradiction).
     *
     * @return true if contradiction, false otherwise
     */
    public boolean isZero() {
        if (type == MCPType.BDD) {
            return bddValue.isZero();
        } else {
            // checkFactory();
            // SATSolver miniSat = MiniSat.miniSat(satFactory);
            // miniSat.add(satValue);
            // Tristate result = miniSat.sat();
            return satValue.isContradiction();
        }
    }

    /**
     * Checks if this bit vector represents logical true (tautology).
     *
     * @return true if tautology, false otherwise
     */
    public boolean isOne() {
        if (type == MCPType.BDD) {
            return bddValue.isOne();
        } else {
            // checkFactory();
            // SATSolver miniSat = MiniSat.miniSat(satFactory);
            // miniSat.add(satValue);
            // Tristate result = miniSat.sat();
            return satValue.isTautology();
        }
    }

    /**
     * Returns the number of satisfying assignments for this bit vector.
     * For SAT, returns 1 if satisfiable, 0 otherwise.
     *
     * @return the number of satisfying assignments
     */
    public double satCount() {
        if (type == MCPType.BDD) {
            return bddValue.satCount();
        } else {
            if(isZero()) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    /**
     * Performs logical implication (this => other).
     *
     * @param other the other bit vector
     * @return a new MCPBitVector representing the implication
     */
    public MCPBitVector imp(MCPBitVector other) {
        checkSameType(other);
        checkFactory();
        
        if (type == MCPType.BDD) {
            return new MCPBitVector(this.bddValue.imp(other.bddValue), type).withFactory(bddFactory);
        } else {
            Formula result = satFactory.implication(this.satValue, other.satValue);
            return new MCPBitVector(result, type).withFactory(satFactory);
        }
    }

    /**
     * Performs in-place logical implication (this => other) (modifies this instance for SAT).
     *
     * @param other the other bit vector
     * @return a new MCPBitVector representing the implication
     */
    public MCPBitVector impWith(MCPBitVector other) {
        checkSameType(other);
        checkFactory();
        
        if (type == MCPType.BDD) {
            return new MCPBitVector(this.bddValue.impWith(other.bddValue), type).withFactory(bddFactory);
        } else {
            Formula result = satFactory.implication(this.satValue, other.satValue);
            this.satValue = result;
            return new MCPBitVector(result, type).withFactory(satFactory);
        }
    }

    /**
     * Performs logical NOT on this bit vector.
     *
     * @return a new MCPBitVector representing the negation
     */
    public MCPBitVector not() {
        checkFactory();
        
        if (type == MCPType.BDD) {
            return new MCPBitVector(this.bddValue.not(), type).withFactory(bddFactory);
        } else {
            Formula result = satFactory.not(this.satValue);
            return new MCPBitVector(result, type).withFactory(satFactory);
        }
    }

    /**
     * Performs logical bi-implication (equivalence) with another MCPBitVector.
     *
     * @param other the other bit vector
     * @return a new MCPBitVector representing the equivalence
     */
    public MCPBitVector biimp(MCPBitVector other) {
        checkSameType(other);
        checkFactory();
        
        if (type == MCPType.BDD) {
            return new MCPBitVector(this.bddValue.biimp(other.bddValue), type).withFactory(bddFactory);
        } else {
            Formula result = satFactory.equivalence(this.satValue, other.satValue);
            return new MCPBitVector(result, type).withFactory(satFactory);
        }
    }

    /**
     * Returns a copy of this bit vector (for BDD, returns a new reference).
     *
     * @return a new MCPBitVector copy
     */
    public MCPBitVector id() {
        checkFactory();
        
        if (type == MCPType.BDD) {
            return new MCPBitVector(this.bddValue.id(), type).withFactory(bddFactory);
        } else {
            return new MCPBitVector(this.satValue, type).withFactory(satFactory);
        }
    }

    /**
     * Returns the backend type of this bit vector.
     *
     * @return the MCPType (BDD or SAT)
     */
    public MCPType getType() {
        return type;
    }

    /**
     * Checks if this bit vector uses the BDD backend.
     *
     * @return true if BDD, false otherwise
     */
    public boolean isBDD() {
        return type == MCPType.BDD;
    }

    /**
     * Checks if this bit vector uses the SAT backend.
     *
     * @return true if SAT, false otherwise
     */
    public boolean isSAT() {
        return type == MCPType.SAT;
    }

    /**
     * Copy constructor. Creates a deep copy of the given MCPBitVector.
     *
     * @param other the MCPBitVector to copy
     */
    public MCPBitVector(MCPBitVector other) {
        this.type = other.type;
        if (other.type == MCPType.BDD) {
            this.bddValue = other.bddValue.id();  
            this.bddFactory = other.bddFactory;   
        } else {
            this.satValue = other.satValue;       
            this.satFactory = other.satFactory;
        }
    }

    /**
     * Returns a deep copy of this MCPBitVector.
     *
     * @return a new MCPBitVector copy
     */
    public MCPBitVector copy() {
        MCPBitVector newMCP;
        if (type == MCPType.BDD) {
            newMCP = new MCPBitVector(this.bddValue.id(), type);
            newMCP.setFactory(this.bddFactory);
        } else {
            newMCP = new MCPBitVector(this.satValue, type);
            newMCP.setFactory(this.satFactory);
        }
        return newMCP;
    }
}