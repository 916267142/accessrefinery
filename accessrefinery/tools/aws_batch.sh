#!/bin/bash

analyzer_arn="arn:aws:access-analyzer:ap-northeast-1:222634363387:analyzer/ExternalAccess"
bucket_name="antsknmath"

single_policy_scan() {
    local policy_file="$1"
    local result_file="$2"
    local log_file="$3"
    local timeout="${4:-0}"
    local start_time=$(date +%s)


    initial_updated_at=$(aws accessanalyzer get-analyzed-resource \
        --analyzer-arn "$analyzer_arn" \
        --resource-arn "arn:aws:s3:::${bucket_name}" \
        --query 'resource.updatedAt' --output text)

    echo "[0/5] Initial updatedAt: $initial_updated_at" | tee -a "$log_file"\

    sleep 1
    
    aws s3api put-bucket-policy --bucket "$bucket_name" --policy file://"$policy_file"
    echo "[1/5] Bucket policy updated with $policy_file" | tee -a "$log_file"


    aws accessanalyzer start-resource-scan --analyzer-arn "$analyzer_arn" --resource-arn "arn:aws:s3:::$bucket_name"
    echo "[2/5] Resource scan started" | tee -a "$log_file"

    while true; do
        sleep 1
        current_updated_at=$(aws accessanalyzer get-analyzed-resource \
            --analyzer-arn "$analyzer_arn" \
            --resource-arn "arn:aws:s3:::${bucket_name}" \
            --query 'resource.updatedAt' --output text)

        elapsed_time=$(( $(date +%s) - start_time ))
        tput cr
        echo -n "running time: $elapsed_time s"

        if [ "$current_updated_at" != "$initial_updated_at" ]; then
            echo
            echo "[3/5] updatedAt changed: $current_updated_at" | tee -a "$log_file"
            break
        fi

        if [ "$timeout" -ne 0 ] && [ "$elapsed_time" -ge "$timeout" ]; then
            echo
            echo "[3/5] Timeout after $timeout seconds, updatedAt not changed." | tee -a "$log_file"
            break
        fi
    done

    sleep 3

    aws accessanalyzer list-findings --analyzer-arn "$analyzer_arn" --filter '{"status": {"eq":["ACTIVE"]}}' > "$result_file"
    echo "[4/5] intents saved at $result_file" | tee -a "$log_file"

    final_findings_count=$(jq '.findings | length' "$result_file")
    running_time=$(( $(date +%s) - start_time ))
    echo "[5/5] $(date "+%Y-%m-%d %H:%M:%S"): Total running time  : $running_time seconds" | tee -a "$log_file"
    echo "[5/5] $(date "+%Y-%m-%d %H:%M:%S"): Final intents count : $final_findings_count" | tee -a "$log_file"
}

multiple_policy_scan() {
    if [ -z "$1" ]; then
        echo "Usage: $0 <directory> [timeout]"
        exit 1
    fi

    directory="$1"
    timeout="${2:-0}"

    input_dir_name=$(basename "$directory")
    case "$directory" in
        data/*) input_dir_name="${directory#data/}" ;;
        *) input_dir_name=$(basename "$directory") ;;
    esac

    result_dir="./aws_result/${input_dir_name}"
    log_file="$result_dir/run.log"
    mkdir -p "$result_dir"

    echo "-----------------new run---------------------" | tee -a "$log_file"

    for policy_file in "$directory"/*; do
        if [ -f "$policy_file" ]; then
            base_name=$(basename "$policy_file" .json)
            result_file="$result_dir/${base_name}_result.json"
            echo "Processing file: $policy_file" | tee -a "$log_file"
            single_policy_scan "$policy_file" "$result_file" "$log_file" "$timeout"
            echo "---------------------------------------------" | tee -a "$log_file"
        fi
    done
}

multiple_policy_scan "$1" "$2"
