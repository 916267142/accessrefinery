#!/bin/bash
# Generate plot data for Experiment-Effectiveness-Synthetic-K2.dat and K3.dat

# Create output directory if it doesn't exist
mkdir -p paper_figures/data/

# Process K2 data (Scalability_05Keys)
INPUT_K2="results/accessrefinery_bdd_reducer_10rs/Scalability_05Keys/summary.txt"
OUTPUT_K2="paper_figures/data/Experiment-Effectiveness-Synthetic-K2.dat"

if [ -f "$INPUT_K2" ]; then
    # Skip the header (NR>1)
    # $3 is NumberRRI (Column 1 in output)
    # $2 is NumberMCI (Column 2 in output)
    awk 'NR>1 {print $3 "\t" $2}' "$INPUT_K2" > "$OUTPUT_K2"
    echo "Extracted $OUTPUT_K2 from $INPUT_K2"
else
    echo "Warning: File $INPUT_K2 not found"
fi

# Process K3 data (Scalability_06Keys) included for completeness
INPUT_K3="results/accessrefinery_bdd_reducer_10rs/Scalability_06Keys/summary.txt"
OUTPUT_K3="paper_figures/data/Experiment-Effectiveness-Synthetic-K3.dat"

if [ -f "$INPUT_K3" ]; then
    awk 'NR>1 {print $3 "\t" $2}' "$INPUT_K3" > "$OUTPUT_K3"
    echo "Extracted $OUTPUT_K3 from $INPUT_K3"
else
    echo "Warning: File $INPUT_K3 not found"
fi
