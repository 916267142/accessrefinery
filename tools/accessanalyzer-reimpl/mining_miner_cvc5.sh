#!/bin/bash
SOLVER="CVC5"
SOLVER_LOWER="cvc5"
REDUCE_TYPE="miner"
TARGET_DIR="accessanalyzer_${SOLVER_LOWER}_${REDUCE_TYPE}_1rs"
TIMEOUT=3600
echo "Scanning for datasets..."
datasets=(
    "data/Correctness"
    "data/Scalability_05Keys"
    "data/Scalability_06Keys"
)
for dataset_dir in "${datasets[@]}"; do
    dataset_name=$(basename "$dataset_dir")
    files=($(find "$dataset_dir" -name "*.json" | sort))
    if [ ${#files[@]} -eq 0 ]; then continue; fi
    timeout_flag=0
    for file in "${files[@]}"; do
        filename=$(basename "$file")
        if [ $timeout_flag -eq 1 ]; then continue; fi
        timeout $TIMEOUT java -jar target/accessanalyzer-1.0.jar -s "$SOLVER" -f "$file"
        if [ $? -eq 124 ]; then timeout_flag=1; fi
    done
done
if [ -d "result" ]; then
    rm -rf "$TARGET_DIR"
    mv result "$TARGET_DIR"
fi
for dataset_dir in "${datasets[@]}"; do
    dataset_name=$(basename "$dataset_dir")
    dataset_target_dir="$TARGET_DIR/$dataset_name"
    if [ ! -d "$dataset_target_dir" ]; then continue; fi
    find "$dataset_target_dir" -mindepth 1 -maxdepth 1 -type d -name "*.json" | sort | while IFS= read -r json_dir; do
        file_name=$(basename "$json_dir" .json)
        findings_file=$(find "$json_dir" -name "${file_name}_${SOLVER_LOWER}_findings.json" 2>/dev/null | head -n 1)
        if [ -z "$findings_file" ]; then findings_file=$(find "$json_dir" -name "${file_name}_${SOLVER}_findings.json" 2>/dev/null | head -n 1); fi
        time_file=$(find "$json_dir" -name "${file_name}_${SOLVER_LOWER}_time.csv" 2>/dev/null | head -n 1)
        if [ -z "$time_file" ]; then time_file=$(find "$json_dir" -name "${file_name}_${SOLVER}_time.csv" 2>/dev/null | head -n 1); fi
        if [ -n "$findings_file" ]; then mv "$findings_file" "$dataset_target_dir/${file_name}_result.json"; fi
        if [ -n "$time_file" ]; then mv "$time_file" "$dataset_target_dir/${file_name}_time.csv"; fi
        rm -rf "$json_dir"
    done
    summary_file="$dataset_target_dir/summary.csv"
    echo "Number of Statements,Number of Findings,Rounds,Average Time per Round (s),Total Time (s)" > "$summary_file"
    find "$dataset_target_dir" -maxdepth 1 -type f -name "*_result.json" | sort | while IFS= read -r result_file; do
        file_name=$(basename "$result_file" | sed 's/_result\.json$//')
        policy_file="data/$dataset_name/$file_name.json"
        statement_count="N/A"
        if [ -f "$policy_file" ]; then statement_count=$(jq '.Statement | length' "$policy_file"); fi
        intent_count="N/A"
        if [ -f "$result_file" ]; then intent_count=$(jq '.Findings | length' "$result_file"); fi
        time_file="$dataset_target_dir/${file_name}_time.csv"
        rounds="N/A"; avg_time="N/A"; total_time="N/A"
        if [ -f "$time_file" ]; then
            rounds=$(($(wc -l < "$time_file") - 1))
            last_line=$(tail -1 "$time_file")
            IFS=',' read -r _ cumulative_time total_time <<< "$last_line"
            if [ "$rounds" -gt 0 ]; then
                avg_time=$(echo "scale=6; $cumulative_time / $rounds" | bc | awk '{printf "%.4f\n", $0}')
                if [[ "$avg_time" == .* ]]; then avg_time="0$avg_time"; fi
            fi
        fi
        echo "$statement_count,$intent_count,$rounds,$avg_time,$total_time" >> "$summary_file"
    done
done

mkdir -p results
mv accessanalyzer_cvc5_miner_1rs results/
