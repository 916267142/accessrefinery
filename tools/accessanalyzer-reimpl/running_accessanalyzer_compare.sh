#!/bin/bash

mkdir -p results/accessanalyzer_compare_result

# compare AccessAnalyzerCVC5Miner with AccessRefinery
bash tools/compare.sh archive_results/accessanalyzer_cvc5_miner_1rs/Correctness \
    archive_results/accessrefinery_bdd_miner_10rs/Correctness \
    results/accessanalyzer_compare_result/Correctness_AccessRefinery_with_AccessAnalyzerCVC5Miner.log

bash tools/compare.sh archive_results/accessanalyzer_cvc5_miner_1rs/Scalability_05Keys/ \
    archive_results/accessrefinery_bdd_miner_10rs/Scalability_05Keys/ \
    results/accessanalyzer_compare_result/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerCVC5Miner.log

bash tools/compare.sh archive_results/accessanalyzer_cvc5_miner_1rs/Scalability_06Keys/ \
    archive_results/accessrefinery_bdd_miner_10rs/Scalability_06Keys/ \
    results/accessanalyzer_compare_result/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerCVC5Miner.log

# compare AccessAnalyzerZ3Miner with AccessRefinery
bash tools/compare.sh archive_results/accessanalyzer_z3_miner_1rs/Correctness/ \
    archive_results/accessrefinery_bdd_miner_10rs/Correctness/ \
    results/accessanalyzer_compare_result/Correctness_AccessRefinery_with_AccessAnalyzerZ3Miner.log

bash tools/compare.sh archive_results/accessanalyzer_z3_miner_1rs/Scalability_05Keys/ \
    archive_results/accessrefinery_bdd_miner_10rs/Scalability_05Keys/ \
    results/accessanalyzer_compare_result/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerZ3Miner.log

bash tools/compare.sh archive_results/accessanalyzer_z3_miner_1rs/Scalability_06Keys/ \
    archive_results/accessrefinery_bdd_miner_10rs/Scalability_06Keys/ \
    results/accessanalyzer_compare_result/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerZ3Miner.log

# compare AccessAnalyzerCVC5Reducer with AccessRefinery
bash tools/compare.sh archive_results/accessanalyzer_cvc5_reducer_1rs/Correctness/ \
    archive_results/accessrefinery_bdd_reducer_10rs/Correctness/ \
    results/accessanalyzer_compare_result/Correctness_AccessRefinery_with_AccessAnalyzerCVC5Reducer.log

bash tools/compare.sh archive_results/accessanalyzer_cvc5_reducer_1rs/Scalability_05Keys/ \
    archive_results/accessrefinery_bdd_reducer_10rs/Scalability_05Keys/ \
    results/accessanalyzer_compare_result/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerCVC5Reducer.log

bash tools/compare.sh archive_results/accessanalyzer_cvc5_reducer_1rs/Scalability_06Keys/ \
    archive_results/accessrefinery_bdd_reducer_10rs/Scalability_06Keys/ \
    results/accessanalyzer_compare_result/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerCVC5Reducer.log

# compare AccessAnalyzerZ3Reducer with AccessRefinery
bash tools/compare.sh archive_results/accessanalyzer_z3_reducer_1rs/Correctness/ \
    archive_results/accessrefinery_bdd_reducer_10rs/Correctness/ \
    results/accessanalyzer_compare_result/Correctness_AccessRefinery_with_AccessAnalyzerZ3Reducer.log

bash tools/compare.sh archive_results/accessanalyzer_z3_reducer_1rs/Scalability_05Keys/ \
    archive_results/accessrefinery_bdd_reducer_10rs/Scalability_05Keys/ \
    results/accessanalyzer_compare_result/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerZ3Reducer.log

bash tools/compare.sh archive_results/accessanalyzer_z3_reducer_1rs/Scalability_06Keys/ \
    archive_results/accessrefinery_bdd_reducer_10rs/Scalability_06Keys/ \
    results/accessanalyzer_compare_result/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerZ3Reducer.log
