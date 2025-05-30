package org.iam.core;

import org.iam.config.Parameter;
import org.iam.grammer.Finding;
import org.iam.grammer.Policy;
import org.iam.core.enumerated.EnumeratedMiner;
import org.iam.core.stratified.StratifiedMiner;
import org.iam.utils.TimeMeasure;

import java.util.Set;

public class MinerFactory {
    public Set<Finding> mineIntent(Policy policy, TimeMeasure timeMeasure) {
        return switch (Parameter.getActiveLogic()) {
            case STRATIFIED -> {
                StratifiedMiner miner = new StratifiedMiner();
                yield miner.mineIntent(policy, timeMeasure);
            }
            case ENUMERATED -> {
                EnumeratedMiner miner = new EnumeratedMiner();
                yield miner.mineIntent(policy, timeMeasure);
            }
        };
    }

    public Set<Finding> reduceIntent(Policy policy, Set<Finding> findings) {
        return switch (Parameter.getActiveLogic()) {
            case STRATIFIED -> {
                StratifiedMiner miner = new StratifiedMiner();
                yield miner.reduceIntent(policy, findings);
            }
            case ENUMERATED -> {
                EnumeratedMiner miner = new EnumeratedMiner();
                yield miner.reduceIntent(policy, findings);
            }
        };
    }
}
