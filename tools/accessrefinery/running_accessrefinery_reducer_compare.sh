#!/bin/bash

mkdir -p results/accessrefinery_reducer_compare_results

# compare Web AccessRefinery IReducer BDD with AccessRefinery IReducer SAT
sh tools/accessrefinery/compare.sh results/accessrefinery_bdd_reducer_10rs/Correctness/  \
    results/accessrefinery_sat_reducer_3rs/Correctness/ \
    results/accessrefinery_reducer_compare_results/Correctness_RRI_AccessRefinery_BDD_with_AccessRefinery_SAT.log

sh tools/accessrefinery/compare.sh results/accessrefinery_bdd_reducer_10rs/Scalability_05Keys/  \
    results/accessrefinery_sat_reducer_3rs/Scalability_05Keys/ \
    results/accessrefinery_reducer_compare_results/Scalability_05Keys_RRI_AccessRefinery_BDD_with_AccessRefinery_SAT.log

sh tools/accessrefinery/compare.sh results/accessrefinery_bdd_reducer_10rs/Scalability_06Keys/  \
    results/accessrefinery_sat_reducer_3rs/Scalability_06Keys/ \
    results/accessrefinery_reducer_compare_results/Scalability_06Keys_RRI_AccessRefinery_BDD_with_AccessRefinery_SAT.log
