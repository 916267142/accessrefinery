#!/bin/bash

# Script to check if policy is covered by findings based on specific directory structures
# Usage: ./tools/check_coverage.sh (Run from project root)

JAR_PATH="target/accessanalyzer-1.0.jar"

# Define the specific result folders to check (Folder0)
RESULT_FOLDERS=(
    "accessrefinery_bdd_reducer_10rs"
)

# Define the categories (Folder1)
CATEGORIES=(
    "Correctness"
    "Scalability_05Keys"
    "Scalability_06Keys"
)

if [ ! -f "$JAR_PATH" ]; then
    echo "Error: JAR file not found at $JAR_PATH"
    echo "Please build the project first."
    exit 1
fi

echo "Starting coverage check..."
echo "-----------------------------------------------------------"

mkdir -p ./results/coverage_check/

# Enable nullglob so that if no files match the glob, the loop variable is empty/skipped
shopt -s nullglob

for FOLDER0 in "${RESULT_FOLDERS[@]}"; do
    for FOLDER1 in "${CATEGORIES[@]}"; do
        # Construct the directory path for findings
        FINDINGS_DIR="./results/$FOLDER0/$FOLDER1"

        if [ -d "$FINDINGS_DIR" ]; then
            # Iterate over all _result.json files in this directory
            # We use finding pattern: $FileName_result.json
            FILES=("$FINDINGS_DIR"/*_result.json)

            for FINDINGS_FILE in "${FILES[@]}"; do
                # Double check if the file exists (redundant with nullglob but safe)
                if [ -f "$FINDINGS_FILE" ]; then
                    # Extract the filename (e.g., 01_allow_result.json)
                    FILENAME=$(basename "$FINDINGS_FILE")

                    # Extract the policy name (e.g., 01_allow)
                    # Removing the suffix "_result.json"
                    POLICY_NAME="${FILENAME%_result.json}"

                    # Construct the corresponding policy path
                    # Path: ./data/$Folder1/$FileName.json
                    POLICY_PATH="./data/$FOLDER1/${POLICY_NAME}.json"

                    if [ -f "$POLICY_PATH" ]; then
                        # Prepare log directory
                        mkdir -p "./results/coverage_check/$FOLDER1"

                        # Prepare the findings file to be used
                        ACTUAL_FINDINGS_FILE="$FINDINGS_FILE"
                        TMP_FILE=""

                        # Special handling for accessrefinery_bdd_reducer_10rs: replace "Finding" with "Findings"
                        if [[ "$FOLDER0" == "accessrefinery_bdd_reducer_10rs" ]]; then
                             TMP_FILE=$(mktemp)
                             # Use sed to replace "Finding" : with "Findings" :
                             # We use simple pattern matching assuming checking standard JSON format
                             sed 's/"Finding"[[:space:]]*:/"Findings" :/g' "$FINDINGS_FILE" > "$TMP_FILE"
                             ACTUAL_FINDINGS_FILE="$TMP_FILE"
                        fi

                        # Execute the check sequentially with a timeout of 3600 seconds
                        timeout 3600 java -jar "$JAR_PATH" -c "$POLICY_PATH" "$ACTUAL_FINDINGS_FILE" > "./results/coverage_check/$FOLDER1/${POLICY_NAME}_coverage.log" 2>&1
                        EXIT_CODE=$?

                        # Remove ANSI color codes from the generated log
                        LOG_FILE="./results/coverage_check/$FOLDER1/${POLICY_NAME}_coverage.log"
                        sed -i -r "s/\x1B\[[0-9;]*[a-zA-Z]//g" "$LOG_FILE"

                        if [ $EXIT_CODE -eq 124 ]; then
                            echo "❌ $POLICY_PATH: timeout"
                        elif grep -q "The findings cover the policy." "$LOG_FILE"; then
                            if grep -q "The findings are minimal" "$LOG_FILE"; then
                                echo "✅ $POLICY_PATH: cover & minimal"
                            else
                                echo "❌ $POLICY_PATH: cover & not minimal"
                            fi
                        else
                            echo "❌ $POLICY_PATH: not cover"
                        fi

                        # Cleanup temp file if used
                        if [[ -n "$TMP_FILE" && -f "$TMP_FILE" ]]; then
                            rm "$TMP_FILE"
                        fi

                        if [ $EXIT_CODE -eq 124 ]; then
                            echo "Timeout reached for $POLICY_NAME. Skipping remaining files in $FINDINGS_DIR."
                            break
                        fi
                    else
                        echo "Warning: Policy file not found for result: $FINDINGS_FILE (Expected: $POLICY_PATH)"
                    fi
                fi
            done
        else
            echo "Directory not found (skipping): $FINDINGS_DIR"
        fi
    done
done

echo "Check completed."
