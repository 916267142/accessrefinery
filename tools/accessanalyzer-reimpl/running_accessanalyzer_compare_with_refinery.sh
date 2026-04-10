#!/bin/bash

mkdir -p results/accessanalyzer_miner_compare_results_with_refinery

# compare AccessAnalyzerCVC5Miner with AccessAnalyzerCLI
bash tools/accessanalyzer-reimpl/compare.sh archive_results/accessanalyzer_cvc5_miner_1rs/Correctness \
    archive_results/accessrefinery_bdd_miner_10rs/Correctness \
    results/accessanalyzer_miner_compare_results_with_refinery/Correctness_AccessRefinery_with_AccessAnalyzerCVC5Miner.log

bash tools/accessanalyzer-reimpl/compare.sh archive_results/accessanalyzer_cvc5_miner_1rs/Scalability_05Keys/ \
    archive_results/accessrefinery_bdd_miner_10rs/Scalability_05Keys/ \
    results/accessanalyzer_miner_compare_results_with_refinery/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerCVC5Miner.log

bash tools/accessanalyzer-reimpl/compare.sh archive_results/accessanalyzer_cvc5_miner_1rs/Scalability_06Keys/ \
    archive_results/accessrefinery_bdd_miner_10rs/Scalability_06Keys/ \
    results/accessanalyzer_miner_compare_results_with_refinery/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerCVC5Miner.log

# compare AccessAnalyzerZ3Miner with AccessAnalyzerCLI
bash tools/accessanalyzer-reimpl/compare.sh archive_results/accessanalyzer_z3_miner_1rs/Correctness/ \
    archive_results/accessrefinery_bdd_miner_10rs/Correctness/ \
    results/accessanalyzer_miner_compare_results_with_refinery/Correctness_AccessRefinery_with_AccessAnalyzerZ3Miner.log

bash tools/accessanalyzer-reimpl/compare.sh archive_results/accessanalyzer_z3_miner_1rs/Scalability_05Keys/ \
    archive_results/accessrefinery_bdd_miner_10rs/Scalability_05Keys/ \
    results/accessanalyzer_miner_compare_results_with_refinery/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerZ3Miner.log

bash tools/accessanalyzer-reimpl/compare.sh archive_results/accessanalyzer_z3_miner_1rs/Scalability_06Keys/ \
    archive_results/accessrefinery_bdd_miner_10rs/Scalability_06Keys/ \
    results/accessanalyzer_miner_compare_results_with_refinery/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerZ3Miner.log
