#!/bin/bash
mkdir -p paper_figures/data/
process_dir() {
    local dir_name=$1
    local out_file=$2
    local f2="results/accessanalyzer_z3_miner_1rs/${dir_name}/summary.csv"
    local f3="archive_results/accessanalyzer_cvc5_miner_1rs/${dir_name}/summary.csv"
    local f4="results/accessrefinery_sat_miner_10rs/${dir_name}/summary.txt"
    local f5="results/accessrefinery_bdd_miner_10rs/${dir_name}/summary.txt"
    local val2=()
    local val3=()
    local val4=()
    local val5=()
    # Read f2 (CSV, 5th col = Total Time (s), needs * 1000)
    if [ -f "$f2" ]; then
        mapfile -t val2 < <(awk -F',' 'NR>1 {printf "%.1f\n", $5 * 1000}' "$f2")
    fi
    # Read f3 (CSV, 5th col = Total Time (s), needs * 1000)
    if [ -f "$f3" ]; then
        mapfile -t val3 < <(awk -F',' 'NR>1 {printf "%.1f\n", $5 * 1000}' "$f3")
    fi
    # Read f4 (TXT, whitespace delimited, 5th col = TotalTimeAverage)
    if [ -f "$f4" ]; then
        mapfile -t val4 < <(awk 'NR>1 {printf "%.2f\n", $5}' "$f4")
    fi
    # Read f5 (TXT, whitespace delimited, 5th col = TotalTimeAverage)
    if [ -f "$f5" ]; then
        mapfile -t val5 < <(awk 'NR>1 {printf "%.2f\n", $5}' "$f5")
    fi
    > "$out_file"
    for i in {0..14}; do
        local v2=${val2[$i]:-3600000.0}
        local v3=${val3[$i]:-3600000.0}
        local v4=${val4[$i]:-3600000.00}
        local v5=${val5[$i]:-3600000.00}
        echo "$((i+1)) $v2 $v3 $v4 $v5" >> "$out_file"
    done
}
process_dir "Scalability_05Keys" "paper_figures/data/Experiment-Scalability-MCI-K2.dat"
process_dir "Scalability_06Keys" "paper_figures/data/Experiment-Scalability-MCI-K3.dat"
echo "Done extracting scalability MCI data."
