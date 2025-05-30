#!/bin/bash

mkdir -p unreduced_logs

solvers=("Z3" "CVC5")

TIMEOUT=3600

echo "Scanning for datasets in data/ directory..."
datasets=($(find data -mindepth 1 -maxdepth 1 -type d | sort))
echo "Found ${#datasets[@]} datasets:"
printf ' - %s\n' "${datasets[@]}"
echo

for dataset_dir in "${datasets[@]}"; do
    dataset_name=$(basename "$dataset_dir")

    dataset_log_dir="unreduced_logs/$dataset_name"
    mkdir -p "$dataset_log_dir"

    echo "Processing dataset: $dataset_name"
    echo "======================================"

    files=($(find "$dataset_dir" -name "*.json" | sort))
    if [ ${#files[@]} -eq 0 ]; then
        echo "No JSON files found in $dataset_dir, skipping"
        echo
        continue
    fi

    declare -A solver_timeout_flags
    for solver in "${solvers[@]}"; do
        solver_timeout_flags[$solver]=0
    done

    for file in "${files[@]}"; do
        filename=$(basename "$file")
        echo "Processing file: $filename"

        for solver in "${solvers[@]}"; do
            if [ ${solver_timeout_flags[$solver]} -eq 1 ]; then
                echo "|-- Skipping solver $solver (previous timeout)"
                continue
            fi

            log_file="$dataset_log_dir/${filename%.*}_$solver.log"
            echo "|-- Running solver: $solver"
            echo "|   Log: $log_file"

            timeout $TIMEOUT java -jar target/accessanalyzer-1.0-SNAPSHOT-release.jar \
                -s "$solver" \
                -f "$file" > "$log_file" 2>&1

            exit_status=$?
            if [ $exit_status -eq 124 ]; then
                echo "|   !!! TIMEOUT after $TIMEOUT seconds"
                solver_timeout_flags[$solver]=1
                echo "Execution timed out after $TIMEOUT seconds" >> "$log_file"
            elif [ $exit_status -ne 0 ]; then
                echo "|   !!! ERROR: Exit code $exit_status"
            else
                echo "|   Completed successfully"
            fi
        done
    done

    echo "Finished dataset: $dataset_name"
    echo "======================================"
    echo
done

mv result/ unreduced_result/

echo "All experiments completed."