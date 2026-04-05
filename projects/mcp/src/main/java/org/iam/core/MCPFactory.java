package org.iam.core;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

import org.iam.sat.SATDomain;
import org.iam.variables.statics.Label;
import org.iam.variables.statics.LabelType;
import org.batfish.BDDDomain;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The MCPFactory class extends MCPLabels and manages symbolic representations of sets of constraints.
 * <p>
 * Supports both Binary Decision Diagrams (BDD) and SAT formula backends.
 * Provides methods for creating, updating, and querying BDD/SAT domains, as well as converting between
 * representations and exporting to DOT format for visualization.
 * </p>
 *
 * @since 2025-02-28
 */
public class MCPFactory extends MCPLabels {
    public enum MCPType {
        SAT, BDD
    }

    /**
     * Backend type for this factory (BDD or SAT).
     */
    private final MCPType _type;

    /**
     * BDD domains for each domain name.
     */
    private final TreeMap<String, BDDDomain<Integer>> _bddDomains;

    /**
     * SAT domains for each domain name.
     */
    private final TreeMap<String, SATDomain<Integer>> _satDomains;

    /**
     * BDDFactory instance for BDD operations.
     */
    private final BDDFactory _bddFactory;

    /**
     * FormulaFactory instance for SAT operations.
     */
    private final FormulaFactory _satFactory;

    /**
     * Bit index to name mapping, for debugging and visualization.
     */
    private final Map<Integer, String> _bitNames;

    /**
     * Total number of bits used in the BDD or SAT representation.
     */
    private Integer _numBits;

    /**
     * Constructs a new MCPFactory instance with BDD as the default backend.
     * Initializes the BDDFactory and internal data structures.
     */
    public MCPFactory() {
        this._bddFactory = JFactory.init(100000, 10000);
        this._satFactory = new FormulaFactory();
        this._bddFactory.setCacheRatio(64);
        this._bddDomains = new TreeMap<>();
        this._satDomains = new TreeMap<>();
        this._bitNames = new HashMap<>();
        this._type = MCPType.BDD;
    }

    /**
     * Constructs a new MCPFactory instance with the specified backend type.
     *
     * @param type the backend type to use (SAT or BDD)
     */
    public MCPFactory(MCPType type) {
        this._bddFactory = JFactory.init(100000, 10000);
        this._satFactory = new FormulaFactory();
        this._bddFactory.setCacheRatio(64);
        this._bddDomains = new TreeMap<>();
        this._satDomains = new TreeMap<>();
        this._bitNames = new HashMap<>();
        this._type = type;
    }

    /**
     * Returns the SAT FormulaFactory if the backend type is SAT.
     *
     * @return the FormulaFactory for SAT
     * @throws UnsupportedOperationException if the backend is not SAT
     */
    public FormulaFactory getSatFactory() {
        if(_type == MCPType.SAT) {
            return _satFactory;
        } else {
            throw new UnsupportedOperationException("This factory is not a SAT factory.");
        }
    }

    /**
     * Constructs a new MCPFactory instance by copying the state from another MCPFactory.
     *
     * @param other the MCPFactory instance to copy from
     */
    public MCPFactory(MCPFactory other) {
        _bddFactory = other._bddFactory;
        _satFactory = other._satFactory;
        _bitNames = other._bitNames;
        _numBits = other._numBits;
        _type = other._type;

        _bddDomains = new TreeMap<>();
        _satDomains = new TreeMap<>();
        other._bddDomains.forEach((k, v) ->
                _bddDomains.put(k, new BDDDomain<>(v))
        );
        other._satDomains.forEach((k, v) ->
            _satDomains.put(k, new SATDomain<>(v)));
    }

    /**
     * Returns the total number of bits used in the BDD or SAT representation.
     *
     * @return the number of bits
     */
    public Integer getNumBits() {
        return _numBits;
    }

    /**
     * Returns a bit vector representing logical one (true) for the current backend type.
     *
     * @return MCPBitVector representing logical one
     */
    public MCPBitVector getOne() {
        if(_type == MCPType.BDD) {
            return new MCPBitVector(_bddFactory.one(), _type);
        } else {
            return new MCPBitVector(_satFactory.verum(), _type).withFactory(_satFactory);
        }
    }

    /**
     * Returns a bit vector representing logical true for all domains.
     *
     * @return MCPBitVector representing logical true for all domains
     */
    public MCPBitVector getTrue() {
        if (_type == MCPType.BDD) {
            BDD bdd = _bddDomains.keySet().stream()
            .map(currDomain -> (BDD)this.getLabel(currDomain, _domainToTrueVariable.get(currDomain)).getValue())
            .reduce(_bddFactory.one(), BDD::and);
            return new MCPBitVector(bdd, _type);
        } else {
            Formula sat = _satDomains.keySet().stream()
            .map(currDomain -> (Formula)this.getLabel(currDomain, _domainToTrueVariable.get(currDomain)).getValue())
            .reduce(_satFactory.verum(), _satFactory::and);
            return new MCPBitVector(sat, _type).withFactory(_satFactory);
        }
    }

    /**
     * Checks if the given value is a tautology (always true).
     *
     * @param value the value to check (should be an MCPBitVector)
     * @return true if the value is a tautology, false otherwise
     */
    public boolean isTautology(Object value) {
        if(_type == MCPType.BDD) {
            return ((BDD)((MCPBitVector)value).getValue()).isOne();
        } else {
            return ((Formula)((MCPBitVector)value).getValue()).isTautology();
        }
    }

    /**
     * Checks if the given value is a contradiction (always false).
     *
     * @param value the value to check (should be an MCPBitVector)
     * @return true if the value is a contradiction, false otherwise
     */
    public boolean isContradiction(Object value) {
        if(_type == MCPType.BDD) {
            return ((BDD)((MCPBitVector)value).getValue()).isZero();
        } else {
            return ((Formula)((MCPBitVector)value).getValue()).isContradiction();
        }
    }

    /**
     * Checks if the given value is satisfiable.
     *
     * @param value the value to check (should be an MCPBitVector)
     * @return true if the value is satisfiable, false otherwise
     */
    public boolean isSatisfying(Object value) {
        if(_type == MCPType.BDD) {
            return ((BDD)((MCPBitVector)value).getValue()).satCount() != 0;
        } else {
            return ((Formula)((MCPBitVector)value).getValue()).isSatisfiable();
        }
    }

    /**
     * Returns a bit vector representing logical false for the current backend type.
     *
     * @return MCPBitVector representing logical false
     */
    public MCPBitVector getFalse() {
        if(_type == MCPType.BDD) {
            return new MCPBitVector(_bddFactory.zero(), _type);
        } else {
            return new MCPBitVector(_satFactory.falsum(), _type).withFactory(_satFactory);
        }
    }

    /**
     * Updates the internal state of the MCPFactory.
     * Recomputes labels, sets the number of BDD variables, and initializes each domain.
     */
    public void updates() {
        this.computeLabels();

        // Step1. create the number of bdd variables.
        _numBits = this._domainToECs == null? 0 : this._domainToECs.values()
                .stream()
                .mapToInt(v -> BDDDomain.numBits(v.size()))
                .sum();
        _bddFactory.setVarNum(_numBits);


        // Step2. initial each domain.
        int idx = 0;
        for (Map.Entry<String, LabelType> entry : this._domainNames.entrySet()) {
            String domainName = entry.getKey();
            int apNumber = _domainToStandardECs.get(domainName).getNumECs();

            int bitsLen;
            if(_type == MCPType.BDD) {
                BDDDomain<Integer> bddDomain = new BDDDomain<>(_bddFactory,
                    IntStream.range(0, apNumber).boxed().collect(Collectors.toList()), idx);
                _bddDomains.put(domainName, bddDomain);
                bitsLen = bddDomain.getInteger().size();
            } else {
                SATDomain<Integer> satDomain = new SATDomain<>(_satFactory,
                    IntStream.range(0, apNumber).boxed().collect(Collectors.toList()), idx);
                _satDomains.put(domainName, satDomain);
                bitsLen = satDomain.getInteger().size(); 
            }

            addBitNames(domainName, bitsLen, idx, false);
            idx += bitsLen;
        }
    }

    /**
     * Returns all possible satisfying assignments from a given BDD.
     *
     * @param bdd the input BDD
     * @return a list of maps, each mapping domain names to integer values for one assignment
     */
    public List<HashMap<String, Integer>> getBDDResults(BDD bdd) {
        List<HashMap<String, Integer>> results = new ArrayList<>();
        while (!bdd.isZero()) {
            BDD oneResult = bdd.satOne();
            results.add(this.getBDDResult(oneResult));
            bdd = bdd.diff(oneResult);
        }
        return results;
    }

    /**
     * Returns a single satisfying assignment from a BDD as a map of domain names to integer values.
     *
     * @param bdd the input BDD
     * @return a map from domain names to integer values for one assignment
     */
    public HashMap<String, Integer> getBDDResult(BDD bdd) {
        HashMap<String, Integer> domainValues = new HashMap<>();
        for (String domainName : _domainNames.keySet()) {
            BDDDomain<Integer> bddDomain = _bddDomains.get(domainName);
            Integer value = bddDomain.satAssignmentToValue(bdd);
            domainValues.put(domainName, value);
        }
        return domainValues;
    }

    /**
     * Returns a bit vector representing the given label in the specified domain.
     *
     * @param domainName the domain name
     * @param value the label value
     * @return MCPBitVector representing the label
     */
    public MCPBitVector getLabel(String domainName, Label value) {
        if(_type == MCPType.BDD) {
            BDD bdd = setToBDD(getLabelECs(domainName, value), _bddDomains.get(domainName));
            return new MCPBitVector(bdd, _type);
        } else {
            Formula sat = setToSAT(getLabelECs(domainName, value), _satDomains.get(domainName));
            return new MCPBitVector(sat, _type).withFactory(_satFactory);
        }
    }

    /**
     * Returns a bit vector for the given label in one domain, and true in all other domains.
     *
     * @param domainName the domain name
     * @param value the label value
     * @return MCPBitVector representing the label with other domains filled as true
     */
    public MCPBitVector getLabelFillOtherDomain(String domainName, Label value) {
        if(_type == MCPType.BDD) {
            BDD curr = (BDD) getLabel(domainName, value).getValue();
            BDD result = _bddDomains.keySet().stream()
                            .filter(currDomain -> !currDomain.equals(domainName))
                            .map(currDomain -> (BDD)this.getLabel(currDomain, _domainToTrueVariable.get(currDomain)).getValue())
                            .reduce(curr, BDD::and);
            return new MCPBitVector(result, _type);
        } else {
            Formula curr = (Formula) getLabel(domainName, value).getValue();
            Formula result = _satDomains.keySet().stream()
                            .filter(currDomain -> !currDomain.equals(domainName))
                            .map(currDomain -> (Formula)this.getLabel(currDomain, _domainToTrueVariable.get(currDomain)).getValue())
                            .reduce(curr, _satFactory::and);
            return new MCPBitVector(result, _type).withFactory(_satFactory);
        }
    }

    /**
     * Returns a bit vector representing the given value in the specified domain.
     *
     * @param domainName the domain name
     * @param value the value
     * @return MCPBitVector representing the value
     */
    public MCPBitVector getVar(String domainName, Object value) {
        assert this._domainToObjectToLabel.get(domainName).containsKey(value);
        return getLabel(domainName, this._domainToObjectToLabel.get(domainName).get(value));
    }

    /**
     * Returns a bit vector for the given value in one domain, and true in all other domains.
     *
     * @param domainName the domain name
     * @param value the value
     * @return MCPBitVector representing the value with other domains filled as true
     */
    public MCPBitVector getVarFillOtherDomain(String domainName, Object value) {
        return getLabelFillOtherDomain(domainName, this._domainToObjectToLabel.get(domainName).get(value));
    }

    /**
     * Converts a BDD to the Graphviz DOT format for visualization.
     *
     * @param bdd the input BDD
     * @return a string representing the BDD in DOT format
     */
    public String dot(BDD bdd) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        sb.append("0 [shape=box, label=\"0\", style=filled, shape=box, height=0.3, width=0.3];\n");
        sb.append("1 [shape=box, label=\"1\", style=filled, shape=box, height=0.3, width=0.3];\n");
        dotRec(sb, bdd, new HashSet<>());
        sb.append("}");
        return sb.toString();
    }

    /**
     * Converts a set of elements to a BDD representation using the given BDD domain.
     *
     * @param set the set of elements
     * @param bddDomain the BDD domain used for conversion
     * @param <T> the type of elements in the set
     * @return the BDD representing the set
     */
    private <T> BDD setToBDD(Set<T> set, BDDDomain<T> bddDomain) {
        if (set.isEmpty()) {
            return _bddFactory.one();
        } else {
            return this.anyElementOf(set, bddDomain);
        }
    }

    /**
     * Converts a set of elements to a SAT formula using the given SAT domain.
     *
     * @param set the set of elements
     * @param satDomain the SAT domain used for conversion
     * @param <T> the type of elements in the set
     * @return the SAT formula representing the set
     */
    private <T> Formula setToSAT(Set<T> set, SATDomain<T> satDomain) {
        if (set.isEmpty()) {
            return _satFactory.verum();
        } else {
            return this.anyElementOfSAT(set, satDomain);
        }
    }

    /**
     * Returns a SAT formula representing the disjunction of all elements in the set.
     *
     * @param elements the set of elements
     * @param satDomain the SAT domain
     * @param <T> the type of elements in the set
     * @return the SAT formula representing the disjunction
     */
    private <T> Formula anyElementOfSAT(Set<T> elements, SATDomain<T> satDomain) {
        return elements.stream().map(satDomain::value).reduce(_satFactory.falsum(), _satFactory::or);
    }

    /**
     * Returns a BDD representing the disjunction of all elements in the set for the given domain.
     *
     * @param elements the set of allowed elements
     * @param bddDomain the BDD domain to constrain
     * @param <T> the type of elements in the set
     * @return the BDD representing this constraint
     */
    private <T> BDD anyElementOf(Set<T> elements, BDDDomain<T> bddDomain) {
        return _bddFactory.orAll(elements.stream().map(bddDomain::value).collect(Collectors.toList()));
    }

    /**
     * Builds a map from BDD variable index to a meaningful name for debugging.
     *
     * @param s the base name for the bit names
     * @param length the number of bits
     * @param index the starting index of the bits
     * @param reverse whether to reverse the naming order
     */
    private void addBitNames(String s, int length, int index, boolean reverse) {
        for (int i = index; i < index + length; i++) {
            if (reverse) {
                _bitNames.put(i, s + (length - 1 - (i - index)));
            } else {
                _bitNames.put(i, s + (i - index + 1));
            }
        }
    }

    /**
     * Creates a unique id for a BDD node when generating a DOT file for Graphviz.
     *
     * @param bdd the input BDD
     * @return a unique integer id for the BDD node
     */
    private Integer dotId(BDD bdd) {
        if (bdd.isZero()) {
            return 0;
        }
        if (bdd.isOne()) {
            return 1;
        }
        return bdd.hashCode() + 2;
    }

    /**
     * Recursively builds each of the intermediate BDD nodes in the Graphviz DOT format.
     *
     * @param sb the StringBuilder used to build the DOT string
     * @param bdd the current BDD node
     * @param visited a set of visited BDD nodes to avoid cycles
     */
    private void dotRec(StringBuilder sb, BDD bdd, Set<BDD> visited) {
        if (bdd.isOne() || bdd.isZero() || visited.contains(bdd)) {
            return;
        }
        int val = dotId(bdd);
        int valLow = dotId(bdd.low());
        int valHigh = dotId(bdd.high());
        String name = _bitNames.get(bdd.var());
        sb.append(val).append(" [label=\"").append(name).append("\"]\n");
        sb.append(val).append(" -> ").append(valLow).append("[style=dotted]\n");
        sb.append(val).append(" -> ").append(valHigh).append("[style=filled]\n");
        visited.add(bdd);
        dotRec(sb, bdd.low(), visited);
        dotRec(sb, bdd.high(), visited);
    }
}