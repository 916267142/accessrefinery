#!/bin/bash

mkdir compare_result

# compare Web Access Analyzer with AccessRefinery
sh tools/compare.sh archive_result/accessanalyzer_web/Correctness/ \
    archive_result/accessrefinery_bdd_miner_10rs/Correctness/ \
    compare_result/Correctness_AccessRefinery_with_WebAccessAnalyzer.log

sh tools/compare.sh archive_result/accessanalyzer_web/Scalability_05Keys/ \
    archive_result/accessrefinery_bdd_miner_10rs/Scalability_05Keys/ \
    compare_result/Scalability_05Keys_AccessRefinery_with_WebAccessAnalyzer.log

sh tools/compare.sh archive_result/accessanalyzer_web/Scalability_06Keys/ \
    archive_result/accessrefinery_bdd_miner_10rs/Scalability_06Keys/ \
    compare_result/Scalability_06Keys_AccessRefinery_with_WebAccessAnalyzer.log

# compare Web AccessRefinery Irefinery BDD with AccessRefinery Irefinery SAT
sh tools/compare.sh archive_result/accessrefinery_bdd_miner_10rs/Correctness/  \
    archive_result/accessrefinery_sat_miner_10rs/Correctness/ \
    compare_result/Correctness_MCI_AccessRefinery_BDD_with_AccessRefinery_SAT.log

sh tools/compare.sh archive_result/accessrefinery_bdd_miner_10rs/Scalability_05Keys/  \
    archive_result/accessrefinery_sat_miner_10rs/Scalability_05Keys/ \
    compare_result/Scalability_05Keys_MCI_AccessRefinery_BDD_with_AccessRefinery_SAT.log

sh tools/compare.sh archive_result/accessrefinery_bdd_miner_10rs/Scalability_06Keys/  \
    archive_result/accessrefinery_sat_miner_10rs/Scalability_06Keys/ \
    compare_result/Scalability_06Keys_MCI_AccessRefinery_BDD_with_AccessRefinery_SAT.log

sh tools/compare.sh archive_result/accessrefinery_bdd_miner_10rs/RW/  \
    archive_result/accessrefinery_sat_miner_10rs/RW/ \
    compare_result/Scalability_RW_MCI_AccessRefinery_BDD_with_AccessRefinery_SAT.log

# compare Web AccessRefinery IReducer BDD with AccessRefinery IReducer SAT
sh tools/compare.sh archive_result/accessrefinery_bdd_reducer_10rs/Correctness/  \
    archive_result/accessrefinery_sat_reducer_3rs/Correctness/ \
    compare_result/Correctness_RRI_AccessRefinery_BDD_with_AccessRefinery_SAT.log

sh tools/compare.sh archive_result/accessrefinery_bdd_reducer_10rs/Scalability_05Keys/  \
    archive_result/accessrefinery_sat_reducer_3rs/Scalability_05Keys/ \
    compare_result/Scalability_05Keys_RRI_AccessRefinery_BDD_with_AccessRefinery_SAT.log

sh tools/compare.sh archive_result/accessrefinery_bdd_reducer_10rs/Scalability_06Keys/  \
    archive_result/accessrefinery_sat_reducer_3rs/Scalability_06Keys/ \
    compare_result/Scalability_06Keys_RRI_AccessRefinery_BDD_with_AccessRefinery_SAT.log

sh tools/compare.sh archive_result/accessrefinery_bdd_reducer_10rs/RW/  \
    archive_result/accessrefinery_sat_reducer_3rs/RW/ \
    compare_result/Scalability_RW_RRI_AccessRefinery_BDD_with_AccessRefinery_SAT.log
