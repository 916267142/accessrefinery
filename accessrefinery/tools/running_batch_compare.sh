#!/bin/bash

mkdir compare_result

# compare Web Access Analyzer with AccessMiner
sh tools/compare.sh archive_result/accessanalyzer_web/Correctness/ \
    archive_result/accessminer_bdd_mci_10rs/Correctness/ \
    compare_result/Correctness_AccessMiner_with_WebAccessAnalyzer.log

sh tools/compare.sh archive_result/accessanalyzer_web/Scalability_05Keys/ \
    archive_result/accessminer_bdd_mci_10rs/Scalability_05Keys/ \
    compare_result/Scalability_05Keys_AccessMiner_with_WebAccessAnalyzer.log

sh tools/compare.sh archive_result/accessanalyzer_web/Scalability_06Keys/ \
    archive_result/accessminer_bdd_mci_10rs/Scalability_06Keys/ \
    compare_result/Scalability_06Keys_AccessMiner_with_WebAccessAnalyzer.log

# compare Web AccessMiner IMiner BDD with AccessMiner IMiner SAT
sh tools/compare.sh archive_result/accessminer_bdd_mci_10rs/Correctness/  \
    archive_result/accessminer_sat_mci_10rs/Correctness/ \
    compare_result/Correctness_MCI_AccessMiner_BDD_with_AccessMiner_SAT.log

sh tools/compare.sh archive_result/accessminer_bdd_mci_10rs/Scalability_05Keys/  \
    archive_result/accessminer_sat_mci_10rs/Scalability_05Keys/ \
    compare_result/Scalability_05Keys_MCI_AccessMiner_BDD_with_AccessMiner_SAT.log

sh tools/compare.sh archive_result/accessminer_bdd_mci_10rs/Scalability_06Keys/  \
    archive_result/accessminer_sat_mci_10rs/Scalability_06Keys/ \
    compare_result/Scalability_06Keys_MCI_AccessMiner_BDD_with_AccessMiner_SAT.log

sh tools/compare.sh archive_result/accessminer_bdd_mci_10rs/RW/  \
    archive_result/accessminer_sat_mci_10rs/RW/ \
    compare_result/Scalability_RW_MCI_AccessMiner_BDD_with_AccessMiner_SAT.log

# compare Web AccessMiner IReducer BDD with AccessMiner IReducer SAT
sh tools/compare.sh archive_result/accessminer_bdd_rri_10rs/Correctness/  \
    archive_result/accessminer_sat_rri_3rs/Correctness/ \
    compare_result/Correctness_RRI_AccessMiner_BDD_with_AccessMiner_SAT.log

sh tools/compare.sh archive_result/accessminer_bdd_rri_10rs/Scalability_05Keys/  \
    archive_result/accessminer_sat_rri_3rs/Scalability_05Keys/ \
    compare_result/Scalability_05Keys_RRI_AccessMiner_BDD_with_AccessMiner_SAT.log

sh tools/compare.sh archive_result/accessminer_bdd_rri_10rs/Scalability_06Keys/  \
    archive_result/accessminer_sat_rri_3rs/Scalability_06Keys/ \
    compare_result/Scalability_06Keys_RRI_AccessMiner_BDD_with_AccessMiner_SAT.log

sh tools/compare.sh archive_result/accessminer_bdd_rri_10rs/RW/  \
    archive_result/accessminer_sat_rri_3rs/RW/ \
    compare_result/Scalability_RW_RRI_AccessMiner_BDD_with_AccessMiner_SAT.log
