
### Correspondence to Paper Sections

After generating the experimental results, we explain how to reproduce the figures, tables, and conclusions reported in the paper.

### 5 Experiment Setup

**Target:** "*AWS provides an online Command Line Interface (CLI) for Access Analyzer, which we use to validate the correctness of our re-implementation. Specifically, for the 6-key dataset with 13 to 15 statements, both versions time out (> 1 hour). Both versions produce identical intents on the Correctness, 5-key, and 6-key datasets.*"

**Reproduced Steps:**

//Todo @after-the-end

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

