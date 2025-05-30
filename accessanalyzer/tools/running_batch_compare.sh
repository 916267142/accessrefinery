#!/bin/babash

mkdir compare_result

# compare AccessAnalyzerCVC5Unreduced with AccessRefinery
bash tools/compare.bash archived_result/accessanalyzer_cvc5_unreduced/Correctness/ \
    archived_result/accessrefinery_bdd_miner_10rs/Correctness/ \
    compare_result/Correctness_AccessRefinery_with_AccessAnalyzerCVC5Unreduced.log

bash tools/compare.bash archived_result/accessanalyzer_cvc5_unreduced/Scalability_05Keys/ \
    archived_result/accessrefinery_bdd_miner_10rs/Scalability_05Keys/ \
    compare_result/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerCVC5Unreduced.log

bash tools/compare.bash archived_result/accessanalyzer_cvc5_unreduced/Scalability_06Keys/ \
    archived_result/accessrefinery_bdd_miner_10rs/Scalability_06Keys/ \
    compare_result/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerCVC5Unreduced.log

# compare AccessAnalyzerZ3Unreduced with AccessRefinery
bash tools/compare.bash archived_result/accessanalyzer_z3_unreduced/Correctness/ \
    archived_result/accessrefinery_bdd_miner_10rs/Correctness/ \
    compare_result/Correctness_AccessRefinery_with_AccessAnalyzerZ3Unreduced.log

bash tools/compare.bash archived_result/accessanalyzer_z3_unreduced/Scalability_05Keys/ \
    archived_result/accessrefinery_bdd_miner_10rs/Scalability_05Keys/ \
    compare_result/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerZ3Unreduced.log

bash tools/compare.bash archived_result/accessanalyzer_z3_unreduced/Scalability_06Keys/ \
    archived_result/accessrefinery_bdd_miner_10rs/Scalability_06Keys/ \
    compare_result/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerZ3Unreduced.log

# compare AccessAnalyzerCVC5Reduced with AccessRefinery
bash tools/compare.bash archived_result/accessanalyzer_cvc5_reduced/Correctness/ \
    archived_result/accessrefinery_bdd_reducer_10rs/Correctness/ \
    compare_result/Correctness_AccessRefinery_with_AccessAnalyzerCVC5Reduced.log

bash tools/compare.bash archived_result/accessanalyzer_cvc5_reduced/Scalability_05Keys/ \
    archived_result/accessrefinery_bdd_reducer_10rs/Scalability_05Keys/ \
    compare_result/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerCVC5Reduced.log

bash tools/compare.bash archived_result/accessanalyzer_cvc5_reduced/Scalability_06Keys/ \
    archived_result/accessrefinery_bdd_reducer_10rs/Scalability_06Keys/ \
    compare_result/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerCVC5Reduced.log

# compare AccessAnalyzerZ3Reduced with AccessRefinery
bash tools/compare.bash archived_result/accessanalyzer_z3_reduced/Correctness/ \
    archived_result/accessrefinery_bdd_reducer_10rs/Correctness/ \
    compare_result/Correctness_AccessRefinery_with_AccessAnalyzerZ3Reduced.log

bash tools/compare.bash archived_result/accessanalyzer_z3_reduced/Scalability_05Keys/ \
    archived_result/accessrefinery_bdd_reducer_10rs/Scalability_05Keys/ \
    compare_result/Scalability_05Keys_AccessRefinery_with_AccessAnalyzerZ3Reduced.log

bash tools/compare.bash archived_result/accessanalyzer_z3_reduced/Scalability_06Keys/ \
    archived_result/accessrefinery_bdd_reducer_10rs/Scalability_06Keys/ \
    compare_result/Scalability_06Keys_AccessRefinery_with_AccessAnalyzerZ3Reduced.log
