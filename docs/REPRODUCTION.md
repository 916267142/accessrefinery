
## Evaluation Reproduction

This section describes (1) how to reproduce the results in `archive_results/`, and (2) how to map archived results to the corresponding figures, tables, and conclusions in the paper.

> We omit the results for the real-world datasets because of commercial restrictions.

<!-- We recommend skipping reproduction of results from the **reimplemented Access Analyzer** (because it takes a very long time) and the **CLI-based Access Analyzer** (because the setup is complex and requires AWS account registration, billing setup, and CLI credential configuration). We still provide instructions for developers.

- See [details](baselines/accessanalyzer-reimpl/README.md) for the **Reimplemented Access Analyzer**.
- See [details](baselines/accessanalyzer-cli/AccessAnalyzerCLI.md) for the **CLI-based Access Analyzer**. -->

### Reproducing Archived Results

#### Reproducing AccessRefinery Archived Results

All previously mined intents are archived in `archive_results/` directory, which allows skipping the following step.

The following scripts invoke `target/accessrefinery-1.0.jar` and reproduce the **AccessRefinery** results.

```bash
# The execution takes about 5min
sh tools/accessrefinery/running_bdd_miner.sh

# The execution takes about 5min
sh tools/accessrefinery/running_sat_miner.sh

# The execution takes about 10min
sh tools/accessrefinery/running_bdd_reducer.sh

# The execution takes about 1h
sh tools/accessrefinery/running_sat_reducer.sh
```

<!-- # The execution takes about 10 seconds.
sh tools/accessrefinery/running_batch_compare.sh -->


Output:

- `results/`:
  - `accessrefinery_bdd_miner_10rs/`: Contains intent mining results for 10 rounds using JavaBDD.
  - `accessrefinery_sat_miner_10rs/`: Contains intent mining results for 10 rounds using MiniSAT.
  - `accessrefinery_bdd_reducer_10rs/`: Contains intent mining and reduction results for 10 rounds using JavaBDD.
  - `accessrefinery_sat_reducer_3rs/`: Contains intent mining and reduction results for 3 rounds using MiniSAT (limited to 3 rounds due to slow execution).


#### Reproducing Reimplemented Access Analyzer Archived Results

All previously mined intents are archived in `archive_results/` directory, which allows skipping the following step.

The following scripts invoke `target/accessanalyzer-1.0.jar` and reproduce the **reimplemented Access Analyzer** results.

```bash
# The execution takes about ???
@ after-the-end

# The execution takes about ???
@ after-the-end

# The execution takes about ???
@ after-the-end

# The execution takes about ???
@ after-the-end
```

- `results/`: All result run one round due to the limited execution time.
  - `accessrefinery_z3_miner_1rs/`: Contains intent mining results using Z3 Solver.
  - `accessrefinery_cvc5_miner_1rs/`: Contains intent mining results  using CVC5 Solver.
  - `accessrefinery_z3_reducer_1rs/`: Contains intent mining and reduction results using Z3 Solver.
  - `accessrefinery_cvc5_reducer_1rs/`: Contains intent mining and reduction results using CVC5 Solver.

#### Reproducing CLI-based Access Analyzer Archived Results

All previously mined intents are archived in `archive_results/accessanalyzer_cli/` directory, which allows skipping the following step. The archived results can serve as the ground truth for subsequent correctness verification.

- **If you are using your own environment:**  
  We strongly recommend skipping reproduction of results from the **CLI-based Access Analyzer**, as setup is complex and requires AWS account registration, billing configuration, and CLI credential setup. We still provide details in [AccessAnalyzerCLI.md](../baselines/accessanalyzer-cli/AccessAnalyzerCLI.md) for developers.

- **If you are using the provided cloud platform via SSH:**  
  The environment is already configured, and you can test our scripts directly. However, because our original AWS account was suspended, the previous bucket name is no longer accessible. We migrated to a new account and created a new bucket accordingly. This new account may still be at risk of AWS suspension due to unusual IP access patterns.

Running (takes about 10 minutes):
```bash
sh baselines/accessanalyzer-cli/aws_batch.sh data/TestCLI
```

Output directory:

- `results/accessanalyzer_cli/`


<!-- We recommend skipping reproduction of results from the **reimplemented Access Analyzer** (because it takes a very long time) and the **CLI-based Access Analyzer** (because the setup is complex and requires AWS account registration, billing setup, and CLI credential configuration). We still provide instructions for developers. -->

### Correspondence to Paper Sections

After generating the `archived_results/`, we explain how to reproduce the figures, tables, and conclusions reported in the paper.

---

### 5 Experiment Setup

**Target Conclusion (Line 750):** "*AWS provides an online Command Line Interface (CLI) for Access Analyzer, which we use to validate the correctness of our re-implementation. Specifically, for the 6-key dataset with 11 to 15 statements, both versions time out (> 1 hour). ...*"

**Reproduced Steps:**

See `archive_results/accessanalyzer_cli/run.log` for the 10_allow_result.json test case. AWS automatically terminated the mining process after `3386` seconds. This indicates that invoking Access Analyzer via the CLI with a 6-key dataset containing 10 to 15 statements results in a timeout.

```test
[4/5] intents saved at ./aws_result/Scalability_06Keys//10_allow_result.json
[5/5] 2025-05-11 18:06:51: Total running time  : 3386 seconds
[5/5] 2025-05-11 18:06:51: Final intents count : 1
```

See the last line of `archive_results/accessanalyzer_z3_miner_1rs/summary.csv`. The final column value of `2596.6094` seconds indicates that only up to 10 statements were mined. This implies that the reproduced Access Analyzer timed out when handling 11 to 15 statements.

**To this end, the conclusion holds.**

---

**Target Conclusion (Line 751):** "*AWS provides an online Command Line Interface (CLI) for Access Analyzer, which we use to validate the correctness of our re-implementation. ... Both versions produce identical intents on the Correctness, 5-key, and 6-key datasets.*"

**Reproduced Steps:** 

---

### 6.1 Is AccessRefinery correct?

#### Correctness of MCP

**Target:** "*We conducted a series of basic Boolean operation tests.*"

**Reproduced Steps:**

Basic Boolean operations are tested in [MCPTest.java](projects/mcp/src/test/java/org/mcp/core/MCPTest.java). These tests run automatically during `mvn package`.

---

#### Correctness of Intent Miner

**Target:** Figure 10 in the paper

<img src="../docs/figures/figure10.png" width="450"/>

**Required logs**:
Use the `NumberMCI` values in `accessrefinery_bdd_miner_10rs/Correctness/summary.txt` to plot Figure 10 of the paper.

**Target:** "*We compared the intents produced by AccessRefinery (without intent reduction), our re-implementation of Access Analyzer, and the AWS Access Analyzer via the CLI API. On synthetic datasets, all three produce the same set of intents.*"

**Required Steps:**

The following commands check whether the intents mined by AccessRefinery are consistent with those from AWS Access Analyzer (via CLI). Logs are generated in `result/compare_accessrefinery_with_accessanalyzer_cli/`:

```bash
sh tools/running_batch_compare.sh
```
---

#### Correctness of Intent Reducer

**Target:** "*(1) The reduced intents fully cover the policy. (2) Removing any intent from the reduced intents causes the remaining intents to no longer cover the policy.*"

Required Steps:

//Todo @after-the-end

---

### Section 6.2 Can AccessRefinery reduce the number of intents?

**Target**: Figure 11 in the paper.

<img src="../docs/figures/figure11.png" width="450"/>

**Required logs**:

- `accessrefinery_bdd_reducer_10rs/`
  - `Scalability_05Keys/summary.txt`
  - `Scalability_06Keys/summary.txt`

The `NumberMCI` column represents the number of intents before reduction, and the `NumberRRI` column represents the number after reduction.

> Note: The real-world results in the paper cannot be open sourced for commercial reasons.

---

### Section 6.3 Can AccessRefinery speed up intent mining and reduction by using MCP?

**Target**: Figure 12 in the paper.

<img src="../docs/figures/figure12.png" width="450"/>

**Required logs**:

- `accessrefinery_bdd_miner_10rs/`
  - `Scalability_05Keys/summary.txt`
  - `Scalability_06Keys/summary.txt`

The `TotalTimeAverage` column represents the average runtime over 10 rounds.

---

**Target**: Figure 13 in the paper.

<img src="../docs/figures/figure13.png" width="450"/>

**Required logs**:

- `accessrefinery_bdd_reducer_10rs/`
  - `Scalability_05Keys/summary.txt`
  - `Scalability_06Keys/summary.txt`

The `TotalTimeAverage` column represents the average runtime over 10 rounds.

---

### Section 6.4 How does AccessRefinery perform on real-world datasets?

These logs are omitted for commercial reasons.

---

### Section 6.5 Is SAT or BDD better for intent mining and reduction?

**Target (Intent Mining)**:
"For intent mining, using JavaBDD is 1-6x faster than using MiniSAT (for clarity, the figure is omitted)."

**Required logs (Intent Mining)**:

- `accessrefinery_bdd_miner_10rs/`
  - `Scalability_05Keys/summary.txt`
  - `Scalability_06Keys/summary.txt`
- `accessrefinery_sat_miner_10rs/`
  - `Scalability_05Keys/summary.txt`
  - `Scalability_06Keys/summary.txt`

The `TotalTimeAverage` column represents the average runtime over 10 rounds.

---

**Target (Intent Reduction)**: Figure 15 in the paper.

<img src="../docs/figures/figure15.png" width="450"/>

**Required logs (Intent Reduction)**:

- `accessrefinery_bdd_reducer_10rs/`
  - `Scalability_05Keys/summary.txt`
  - `Scalability_06Keys/summary.txt`
- `accessrefinery_sat_reducer_3rs/`
  - `Scalability_05Keys/summary.txt`
  - `Scalability_06Keys/summary.txt`

For a fair comparison, compare average runtime per round using `TotalTimeAverage / rounds`. Since SAT-based reduction is much slower, we report SAT results for only 3 rounds.

---

### Section 6.6 How does AccessRefinery accelerate single-round solving in multi-round SMT solving compared to SMT solvers?

**Target**: Table 2 in the paper.

<img src="../docs/figures/table2.png" width="450"/>

**Required logs**:

- `accessrefinery_bdd_miner_10rs/`
  - `Scalability_05Keys/`
  - `Scalability_06Keys/`

`MCILabelsTimeAverage` is the average MCP preprocessing time.
`NumberRRI` is the number of reduced intents.

---

