#!/bin/bash

result_types=("reduced" "unreduced")
solvers=("cvc5" "z3")

for result_type in "${result_types[@]}"; do
    source_dir="${result_type}_result"

    for solver in "${solvers[@]}"; do
        target_dir="accessanalyzer_${solver}_${result_type}"
        mkdir -p "$target_dir"

        echo "Processing $source_dir for solver $solver -> $target_dir"

        # 按字典序处理数据集目录
        find "$source_dir" -mindepth 1 -maxdepth 1 -type d -print0 | sort -z | while IFS= read -r -d $'\0' dataset_dir; do
            dataset=$(basename "$dataset_dir")
            mkdir -p "$target_dir/$dataset"

            find "$dataset_dir" -mindepth 1 -maxdepth 1 -type d -name "*.json" -print0 | sort -z | while IFS= read -r -d $'\0' json_dir; do
                file_name=$(basename "$json_dir" .json)

                findings_file=$(find "$json_dir" -name "${file_name}_${solver}_findings.json" -print -quit)
                time_file=$(find "$json_dir" -name "${file_name}_${solver}_time.csv" -print -quit)

                if [ -n "$findings_file" ]; then
                    cp "$findings_file" "$target_dir/$dataset/${file_name}_result.json"
                else
                    echo "WARNING: Findings file not found for $file_name ($solver, $result_type)"
                fi

                if [ -n "$time_file" ]; then
                    cp "$time_file" "$target_dir/$dataset/${file_name}_time.csv"
                else
                    echo "WARNING: Time file not found for $file_name ($solver, $result_type)"
                fi
            done
        done
    done
done

for result_type in "${result_types[@]}"; do
    for solver in "${solvers[@]}"; do
        target_dir="accessanalyzer_${solver}_${result_type}"

        find "$target_dir" -mindepth 1 -maxdepth 1 -type d -print0 | sort -z | while IFS= read -r -d $'\0' dataset_dir; do
            dataset=$(basename "$dataset_dir")
            summary_file="$dataset_dir/summary.csv"

            echo "Generating summary for $target_dir/$dataset"
            echo "Number of Statements,Number of Findings,Rounds,Average Time per Round (s),Total Time (s)" > "$summary_file"

            find "$dataset_dir" -maxdepth 1 -type f -name "*_result.json" -print0 | sort -z | while IFS= read -r -d $'\0' result_file; do
                file_name=$(basename "$result_file" | sed 's/_result\.json$//')

                policy_file="data/$dataset/$file_name.json"
                if [ -f "$policy_file" ]; then
                    statement_count=$(jq '.Statement | length' "$policy_file")
                else
                    statement_count="N/A"
                    echo "WARNING: Policy file not found: $policy_file"
                fi

                if [ -f "$result_file" ]; then
                    intent_count=$(jq '.Findings | length' "$result_file")
                else
                    intent_count="N/A"
                    echo "WARNING: Result file not found: $result_file"
                fi

                time_file="$dataset_dir/${file_name}_time.csv"
                if [ -f "$time_file" ]; then
                    rounds=$(($(wc -l < "$time_file") - 1))

                    last_line=$(tail -1 "$time_file")

                    IFS=',' read -r _ cumulative_time total_time <<< "$last_line"

                    if [ "$rounds" -gt 0 ]; then
                        avg_time=$(echo "scale=6; $cumulative_time / $rounds" | bc | awk '{printf "%.4f\n", $0}')
                        if [[ "$avg_time" == .* ]]; then
                            avg_time="0$avg_time"
                        fi
                    else
                        avg_time="N/A"
                    fi
                else
                    rounds="N/A"
                    avg_time="N/A"
                    total_time="N/A"
                    echo "WARNING: Time file not found: $time_file"
                fi

                echo "$statement_count,$intent_count,$rounds,$avg_time,$total_time" >> "$summary_file"
            done
        done
    done
done

echo "Result organization and summary generation completed."
