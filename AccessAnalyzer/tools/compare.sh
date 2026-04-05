#!/bin/bash

# Usage: ./compare.sh dir1 dir2 [log_file]

DIR1=$1
DIR2=$2
LOG_FILE="${3:-"comparison.log"}"

if [ ! -d "$DIR1" ] || [ ! -d "$DIR2" ]; then
  echo "❌ Error: Both arguments must be directories."
  exit 1
fi

# Initialize log file with English date format
echo "=== JSON Directory Comparison Log ===" > "$LOG_FILE"
echo "Start time: $(date "+%Y-%m-%d %H:%M:%S")" >> "$LOG_FILE"
echo "Comparing directories: $DIR1 and $DIR2" >> "$LOG_FILE"
echo "" >> "$LOG_FILE"

# Function to normalize JSON with safe sorting
normalize_json() {
  jq '
    def ensure_array: if type == "array" then . else [.] end;
    def sort_condition: 
      if type == "object" then 
        with_entries(.value |= ensure_array) | to_entries | sort_by(.key) | from_entries
      else . end;
    
    if (.findings | type == "array") then
      {
        findings: [.findings[] | {
          action: (.action // [] | ensure_array),
          resource: ([.resource // ""] | if type == "array" then sort else . end),
          principal: { 
            AWS: (.principal.AWS // [] | ensure_array | if type == "array" then sort else . end) 
          },
          condition: (.condition // {} | sort_condition)
        }]
      }
    elif (.Finding | type == "array") then
      {
        findings: [.Finding[] | {
          action: (.Action // [] | ensure_array),
          resource: (.Resource // [] | ensure_array),
          principal: { 
            AWS: (.Principal.AWS // [] | ensure_array) 
          },
          condition: (.Condition // {} | sort_condition)
        }]
      }
      elif (.Findings | type == "array") then
      {
        findings: [.Findings[] | {
          action: (.Action // [] | ensure_array),
          resource: (.Resource // [] | ensure_array),
          principal: { 
            AWS: (.Principal.AWS // [] | ensure_array) 
          },
          condition: (.Condition // {} | sort_condition)
        }]
      }
    else
      { findings: [] }
    end
  ' "$1" | jq -S 'walk(if type == "object" then to_entries | sort_by(.key) | from_entries elif type == "array" then sort else . end)'
}

echo "🔍 Comparing files in $DIR1 and $DIR2..."
echo "🔍 Comparing files in $DIR1 and $DIR2..." >> "$LOG_FILE"

MATCHED_FILES=0
DIFF_FOUND=0

# Loop through all JSON files in DIR1
for FILE1 in "$DIR1"/*.json; do
  FILENAME=$(basename "$FILE1")
  FILE2="$DIR2/$FILENAME"

  if [ -f "$FILE2" ]; then
    MATCHED_FILES=$((MATCHED_FILES + 1))
    TMP1=$(mktemp)
    TMP2=$(mktemp)

    # Normalize both JSON files
    if ! normalize_json "$FILE1" > "$TMP1" 2>> "$LOG_FILE"; then
      echo "❌ Error processing $FILE1" | tee -a "$LOG_FILE"
      continue
    fi
    
    if ! normalize_json "$FILE2" > "$TMP2" 2>> "$LOG_FILE"; then
      echo "❌ Error processing $FILE2" | tee -a "$LOG_FILE"
      continue
    fi

    # Compare using jq
    if jq --slurpfile a "$TMP1" --slurpfile b "$TMP2" -n 'if $a == $b then true else false end' | grep -q true; then
      echo "✅ $FILENAME: equivalent" | tee -a "$LOG_FILE"
    else
      echo "❌ $FILENAME: different" | tee -a "$LOG_FILE"
      echo "=== Differences in $FILENAME ===" >> "$LOG_FILE"
      echo "=== Normalized $DIR1/$FILENAME ===" >> "$LOG_FILE"
      cat "$TMP1" >> "$LOG_FILE"
      echo "" >> "$LOG_FILE"
      echo "=== Normalized $DIR2/$FILENAME ===" >> "$LOG_FILE"
      cat "$TMP2" >> "$LOG_FILE"
      echo "" >> "$LOG_FILE"
      DIFF_FOUND=$((DIFF_FOUND + 1))
    fi

    rm "$TMP1" "$TMP2"
  fi
done

# Summary with English timestamp
SUMMARY="
=== Summary ===
Timestamp: $(date "+%Y-%m-%d %H:%M:%S")
Total matched files: $MATCHED_FILES
Different files: $DIFF_FOUND
Identical files: $((MATCHED_FILES - DIFF_FOUND))
"

echo "$SUMMARY" | tee -a "$LOG_FILE"
echo "Log saved to: $LOG_FILE"

if [ "$MATCHED_FILES" -eq 0 ]; then
  echo "⚠️ No matching .json files found in both directories." | tee -a "$LOG_FILE"
elif [ "$DIFF_FOUND" -eq 0 ]; then
  echo "🎉 All $MATCHED_FILES matched files are semantically equivalent." | tee -a "$LOG_FILE"
else
  echo "🚫 $DIFF_FOUND of $MATCHED_FILES matched files are different." | tee -a "$LOG_FILE"
fi
