#!/bin/bash
# Required logs
Z3_CSV="results/accessanalyzer_z3_miner_1rs/Scalability_05Keys/summary.csv"
CVC5_CSV="results/accessanalyzer_cvc5_miner_1rs/Scalability_05Keys/summary.csv"
MCP_TXT="results/accessrefinery_bdd_miner_10rs/Scalability_05Keys/summary.txt"
echo "| # of allow statements | # of rounds | The time of single-round Boolean solving - Z3 | The time of single-round Boolean solving - CVC5 | The time of single-round Boolean solving - MCP | The time of MCF preprocessing |"
echo "|-----------------------|-------------|-----------------------------------------------|-------------------------------------------------|------------------------------------------------|-------------------------------|"
for i in 3 6 9 12 15; do
    # MCP_TXT has header: NumberAllow NumberMCI NumberRRI TotalTimeAverage MCILabelsTimeAverage MCIOperationsTimeAverage
    # Let's extract values
    num_rounds=$(awk -v k="$i" '$1 == k {print $2}' "$MCP_TXT")
    # average single round for Z3 ms and CVC5 ms
    # Z3 Average time per round (s) column is 4th. Time is in seconds.
    z3_time=$(awk -F',' -v k="$i" 'NR>1 && $1==k {printf "%.1f", $4 * 1000}' "$Z3_CSV")
    if [ -z "$z3_time" ]; then z3_time="N/A"; fi
    cvc5_time=$(awk -F',' -v k="$i" 'NR>1 && $1==k {printf "%.1f", $4 * 1000}' "$CVC5_CSV")
    if [ -z "$cvc5_time" ]; then cvc5_time="N/A"; fi
    # MCP single-round Boolean solving (us)
    mcp_time=$(awk -v k="$i" '$1 == k {printf "%.1f", ($7 / $4) * 1000}' "$MCP_TXT")
    if [ -z "$mcp_time" ]; then mcp_time="N/A"; fi
    # MCF preprocessing time
    mcp_prep_time=$(awk -v k="$i" '$1 == k {printf "%.1f", $6}' "$MCP_TXT")
    if [ -z "$mcp_prep_time" ]; then mcp_prep_time="N/A"; fi
    echo "| $i | $num_rounds | ${z3_time}ms | ${cvc5_time}ms | ${mcp_time}μs | ${mcp_prep_time}ms |"
done
