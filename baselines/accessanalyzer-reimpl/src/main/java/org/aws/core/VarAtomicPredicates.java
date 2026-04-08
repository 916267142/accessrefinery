package org.aws.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.aws.smt.Request;
import org.aws.variables.dynamics.DynamicVar;
import org.aws.variables.statics.StaticVar;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VarAtomicPredicates {
    private final Set<StaticVar> _staticVars;

    private int _numAtomicPredicates;

    private Map<StaticVar, Set<Integer>> _varToPredicates;

    private Map<Integer, DynamicVar> _predicatesToVar;

    public VarAtomicPredicates(Set<StaticVar> staticVars, StaticVar trueStaticVar, Request request) {
        _staticVars = ImmutableSet.<StaticVar>builder()
                .addAll(staticVars)
                .add(trueStaticVar)
                .build();
        initAtomicPredicates(request);
    }

    private void initAtomicPredicates(Request request) {
        SetMultimap<DynamicVar, StaticVar> mmap = HashMultimap.create();
        for (StaticVar textElement : _staticVars) {
            DynamicVar restDynamicVar = textElement.convert(request);
            if (restDynamicVar.isEmpty()) {
                throw new RuntimeException("Regex " + textElement + " does not match any strings");
            }
            SetMultimap<DynamicVar, StaticVar> newMMap = HashMultimap.create(mmap);
            for (DynamicVar dynamicVar : mmap.keySet()) {
                if (dynamicVar.equals(restDynamicVar)) {
                    newMMap.put(dynamicVar, textElement);
                    break;
                }
                DynamicVar inter = dynamicVar.inter(restDynamicVar);

                if (inter.isEmpty()) continue;

                Set<StaticVar> staticVars = newMMap.removeAll(dynamicVar);
                DynamicVar diff = dynamicVar.minus(restDynamicVar);
                newMMap.putAll(inter, staticVars);
                if (!diff.isEmpty()) {
                    newMMap.putAll(diff, staticVars);
                }
                newMMap.put(inter, textElement);
                restDynamicVar = restDynamicVar.minus(dynamicVar);
            }

            if (!restDynamicVar.isEmpty()) {
                newMMap.put(restDynamicVar, textElement);
            }
            mmap = newMMap;
        }
        _predicatesToVar = new HashMap<>();
        SetMultimap<Integer, StaticVar> iToR = HashMultimap.create();
        int i = 0;
        for (DynamicVar a : mmap.keySet()) {
            _predicatesToVar.put(i, a);
            iToR.putAll(i, mmap.get(a));
            i++;
        }
        _numAtomicPredicates = i;
        _varToPredicates = Multimaps.asMap(Multimaps.invertFrom(iToR, HashMultimap.create()));
    }

    public int getNumAtomicPredicates() {
        return _numAtomicPredicates;
    }

    @Nonnull
    public Map<StaticVar, Set<Integer>> getAtomicPredicates() {
        return _varToPredicates;
    }
}
