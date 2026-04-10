#!/bin/bash
# Generate plot data for Experiment-Correctness-Synthetic.dat

SUMMARY_FILE="results/accessrefinery_bdd_miner_10rs/Correctness/summary.txt"
OUTPUT_FILE="paper_figures/data/Experiment-Correctness-Synthetic.dat"

# Ensure output directory exists
mkdir -p "$(dirname "$OUTPUT_FILE")"

# Skip the header (NR>1)
# $2 is the NumberMCI column
awk 'NR>1 {print NR-1 "\t" $2}' "$SUMMARY_FILE" > "$OUTPUT_FILE"

echo "Extracted $OUTPUT_FILE from $SUMMARY_FILE"
