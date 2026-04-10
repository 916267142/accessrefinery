#!/bin/sh

RESULT_ROOT="results"
OUTPUT_DIR="${RESULT_ROOT}/accessrefinery_compare_results"
ARCHIVE_ROOT="archive_results"
COMPARE_SCRIPT="tools/compare.sh"

mkdir -p "${OUTPUT_DIR}"

DATASETS="Correctness Scalability_05Keys Scalability_06Keys"

# compare Web Access Analyzer with AccessRefinery
for DATASET in ${DATASETS}; do
    sh "${COMPARE_SCRIPT}" "${ARCHIVE_ROOT}/accessanalyzer_cli/${DATASET}/" \
        "${ARCHIVE_ROOT}/accessrefinery_bdd_miner_10rs/${DATASET}/" \
        "${OUTPUT_DIR}/${DATASET}_AccessRefinery_with_CliAccessAnalyzer.log"
done

# compare AccessRefinery MCI BDD with AccessRefinery MCI SAT
for DATASET in ${DATASETS}; do
    sh "${COMPARE_SCRIPT}" "${ARCHIVE_ROOT}/accessrefinery_bdd_miner_10rs/${DATASET}/" \
        "${ARCHIVE_ROOT}/accessrefinery_sat_miner_10rs/${DATASET}/" \
        "${OUTPUT_DIR}/${DATASET}_MCI_AccessRefinery_BDD_with_AccessRefinery_SAT.log"
done

# compare AccessRefinery RRI BDD with AccessRefinery RRI SAT
for DATASET in ${DATASETS}; do
    sh "${COMPARE_SCRIPT}" "${ARCHIVE_ROOT}/accessrefinery_bdd_reducer_10rs/${DATASET}/" \
        "${ARCHIVE_ROOT}/accessrefinery_sat_reducer_3rs/${DATASET}/" \
        "${OUTPUT_DIR}/${DATASET}_RRI_AccessRefinery_BDD_with_AccessRefinery_SAT.log"
done
