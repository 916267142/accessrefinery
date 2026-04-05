package org.iam.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.iam.variables.dynamics.OperableLabel;
import org.iam.variables.statics.Label;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A utility class for computing Equivalence Classes (ECs) from a set of labels.
 *
 * <p>
 * ECs are the minimal set of labels such that:
 * <ul>
 *   <li>No EC is logically false.</li>
 *   <li>The disjunction of all ECs is logically true.</li>
 *   <li>Each EC is disjoint from all others.</li>
 *   <li>Each label is equivalent to a disjunction of some subset of the ECs.</li>
 * </ul>
 * This is based on the algorithm described in:
 * <a href="http://www.cs.utexas.edu/users/lam/Vita/Jpapers/Yang_Lam_TON_2015.pdf">
 * "Real-time Verification of Network Properties using ECs" by Yang and Lam</a>.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
@ParametersAreNonnullByDefault
public class ECEngine {

    private final Set<Label> _labels;

    /**
     * The number of computed ECs.
     */
    private int _numECs;

    /**
     * Maps each label to its set of EC indices (integers in the range 0 ... (_numECs - 1)).
     */
    private Map<Label, Set<Integer>> _varToLabels;

    /**
     * Maps each EC index to its semantic representation (as an OperableLabel).
     */
    private Map<Integer, OperableLabel> _labelsToVar;

    /**
     * Constructs ECs for the given set of labels.
     *
     * @param labels    the set of labels to compute ECs for
     * @param trueLabel a label representing logical "true"
     */
    public ECEngine(Set<Label> labels, Label trueLabel) {
        _labels = ImmutableSet.<Label>builder().addAll(labels).add(trueLabel).build();
        initECs();
    }

    private void initECs() {
        SetMultimap<OperableLabel, Label> mmap = HashMultimap.create();
        for (Label textElement : _labels) {
            OperableLabel restOperableLabel = textElement.convert();
            if (restOperableLabel.isEmpty()) {
                throw new RuntimeException("Regex " + textElement + " does not match any strings");
            }
            SetMultimap<OperableLabel, Label> newMMap = HashMultimap.create(mmap);
            for (OperableLabel dynamicVar : mmap.keySet()) {
                /*
                 since all ECs are disjoint from one another,
                 if this is equal to an existing one, we can ignore all the rest
                */
                if (dynamicVar.equals(restOperableLabel)) {
                    newMMap.put(dynamicVar, textElement);
                    break;
                }
                OperableLabel inter = dynamicVar.inter(restOperableLabel);
                /* this is disjoint from 'operableElement', so move on to the next atomic predicate */
                if (inter.isEmpty()) continue;
                /* replace with new ECs */
                Set<Label> labels = newMMap.removeAll(dynamicVar);
                OperableLabel diff = dynamicVar.minus(restOperableLabel);
                newMMap.putAll(inter, labels);
                if (!diff.isEmpty()) {
                    newMMap.putAll(diff, labels);
                }
                newMMap.put(inter, textElement);
                restOperableLabel = restOperableLabel.minus(dynamicVar);
            }
            /* if there's anything left by the end, add it */
            if (!restOperableLabel.isEmpty()) {
                newMMap.put(restOperableLabel, textElement);
            }
            mmap = newMMap;
        }
        /*
         assign a unique integer to each operableElement.
         create a mapping from each integer to its corresponding operableElement
         and a mapping from each textElement to its corresponding set of integers.
        */
        _labelsToVar = new HashMap<>();
        SetMultimap<Integer, Label> iToR = HashMultimap.create();
        int i = 0;
        for (OperableLabel a : mmap.keySet()) {
            _labelsToVar.put(i, a);
            iToR.putAll(i, mmap.get(a));
            i++;
        }
        _numECs = i;
        _varToLabels = Multimaps.asMap(Multimaps.invertFrom(iToR, HashMultimap.create()));
    }

    /**
     * Returns the number of computed ECs.
     *
     * @return the number of ECs
     */
    public int getNumECs() {
        return _numECs;
    }

    /**
     * Returns a mapping from EC indices to their semantic representations.
     *
     * @return a map from EC index to OperableLabel
     */
    @Nonnull
    public Map<Integer, OperableLabel> getECAutomata() {
        return _labelsToVar;
    }

    /**
     * Returns a mapping from each label to the set of EC indices it covers.
     *
     * @return a map from Label to set of EC indices
     */
    @Nonnull
    public Map<Label, Set<Integer>> getECs() {
        return _varToLabels;
    }
}
