#!/bin/bash

mkdir -p results/accessanalyzer_miner_compare_results

# compare AccessAnalyzerCVC5Miner with AccessAnalyzerCLI
bash tools/accessanalyzer-reimpl/compare.sh results/accessanalyzer_cvc5_miner_1rs/Correctness \
    results/accessanalyzer_cli/Correctness \
    results/accessanalyzer_miner_compare_results/Correctness_AccessRefinery_with_AccessAnalyzerCVC5Miner.log

bash tools/accessanalyzer-reimpl/compare.sh results/accessanalyzer_cvc5_miner_1rs/Scalability_05Keys/ \
    results/accessanalyzer_cli/Scalability_05Keys/ \
    results/accessanalyzer_miner_compare_results/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerCVC5Miner.log

bash tools/accessanalyzer-reimpl/compare.sh results/accessanalyzer_cvc5_miner_1rs/Scalability_06Keys/ \
    results/accessanalyzer_cli/Scalability_06Keys/ \
    results/accessanalyzer_miner_compare_results/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerCVC5Miner.log

# compare AccessAnalyzerZ3Miner with AccessAnalyzerCLI
bash tools/accessanalyzer-reimpl/compare.sh results/accessanalyzer_z3_miner_1rs/Correctness/ \
    results/accessanalyzer_cli/Correctness/ \
    results/accessanalyzer_miner_compare_results/Correctness_AccessRefinery_with_AccessAnalyzerZ3Miner.log

bash tools/accessanalyzer-reimpl/compare.sh results/accessanalyzer_z3_miner_1rs/Scalability_05Keys/ \
    results/accessanalyzer_cli/Scalability_05Keys/ \
    results/accessanalyzer_miner_compare_results/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerZ3Miner.log

bash tools/accessanalyzer-reimpl/compare.sh results/accessanalyzer_z3_miner_1rs/Scalability_06Keys/ \
    results/accessanalyzer_cli/Scalability_06Keys/ \
    results/accessanalyzer_miner_compare_results/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerZ3Miner.log
