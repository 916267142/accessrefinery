
## Evaluation Reproduction

This section describes (1) how to reproduce the results in `archive_results/`, and (2) how to map `archive_results/` to the corresponding figures, tables, and conclusions in the paper.

> We omit the results for the real-world datasets because of commercial restrictions.

<!-- We recommend skipping reproduction of results from the **reimplemented Access Analyzer** (because it takes a very long time) and the **CLI-based Access Analyzer** (because the setup is complex and requires AWS account registration, billing setup, and CLI credential configuration). We still provide instructions for developers.

- See [details](baselines/accessanalyzer-reimpl/README.md) for the **Reimplemented Access Analyzer**.
- See [details](baselines/accessanalyzer-cli/AccessAnalyzerCLI.md) for the **CLI-based Access Analyzer**. -->

### Reproducing Archived Results

All experimental results are archived in `archive_results/`, which allows you to skip the following steps.

Due to repeated invocations of Access Analyzer, our AWS account has been suspended and is no longer usable. Therefore, we provide archived results for CLI-based Access Analyzer in `archive_results/accessanalyzer_cli/`. However, we still provide guidance for developers in [AccessAnalyzerCLI.md](baselines/accessanalyzer-cli/AccessAnalyzerCLI.md). We recommend skipping this step, as the setup process is prohibitively complex (it requires AWS account registration, billing configuration, and CLI credential setup).


#### Reproducing AccessRefinery Archived Results

**Running:**

The following scripts invoke `target/accessrefinery-1.0.jar`.

```bash
# The execution takes about 6 minutes.
sh tools/accessrefinery/running_bdd_miner.sh

# The execution takes about 1 hour.
sh tools/accessrefinery/running_sat_miner.sh

# The execution takes about ???.
sh tools/accessrefinery/running_bdd_reducer.sh

# The execution takes about ???.
sh tools/accessrefinery/running_sat_reducer.sh
```

<!-- # The execution takes about 10 seconds.
sh tools/accessrefinery/running_batch_compare.sh -->

**Output:**

- `results/`: All experiments are run for 10 rounds, and average time is reported.

  - `accessrefinery_bdd_miner_10rs/`: intent mining using JavaBDD.
  - `accessrefinery_sat_miner_10rs/`: intent mining using MiniSAT.
  - `accessrefinery_bdd_reducer_10rs/`: intent mining and reduction using JavaBDD.
  - `accessrefinery_sat_reducer_3rs/`: intent mining and reduction using MiniSAT (limited to 3 rounds due to slow execution).


#### Reproducing Reimplemented Access Analyzer Archived Results

**Running:**

The following scripts invoke `target/accessanalyzer-1.0.jar`.

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

**Output:**

- `results/`: All results run for one round due to limited execution time.
  - `accessrefinery_z3_miner_1rs/`: intent mining using Z3 Solver.
  - `accessrefinery_cvc5_miner_1rs/`: intent mining using CVC5 Solver.
  - `accessrefinery_z3_reducer_1rs/`: intent mining and reduction using Z3 Solver.
  - `accessrefinery_cvc5_reducer_1rs/`: intent mining and reduction using CVC5 Solver.

<!-- #### Reproducing CLI-based Access Analyzer Archived Results

All previously mined intents are archived in `archive_results/accessanalyzer_cli/` directory, which allows skipping the following step. The archived results can serve as the ground truth for subsequent correctness verification.

- **If you are using your own environment:**  
  We strongly recommend skipping reproduction of results from the **CLI-based Access Analyzer**, as setup is complex and requires AWS account registration, billing configuration, and CLI credential setup. We still provide details in [AccessAnalyzerCLI.md](../baselines/accessanalyzer-cli/AccessAnalyzerCLI.md) for developers.

- **If you are using the provided cloud platform via SSH:**  
  The environment is already configured, and you can test our scripts directly. However, because our original AWS account was suspended, the previous bucket name is no longer accessible. We migrated to a new account and created a new bucket accordingly. Therefore, generating archive results is not feasible. However, we can still verify the functionality of our script. Note that this script may still fail if the AWS account in use is subject to rate limiting or access restrictions.

Running (takes about 10 minutes):
```bash
sh baselines/accessanalyzer-cli/aws_batch.sh data/TestCLI
```

Output directory:

- `results/accessanalyzer_cli/` -->

<!-- We recommend skipping reproduction of results from the **reimplemented Access Analyzer** (because it takes a very long time) and the **CLI-based Access Analyzer** (because the setup is complex and requires AWS account registration, billing setup, and CLI credential configuration). We still provide instructions for developers. -->

### Correspondence to Paper Sections

After generating the results, we explain how to reproduce the figures, tables, and conclusions reported in the paper.

---

**Target Conclusion (Line 750 in Section 5):** 

"*AWS provides an online Command Line Interface (CLI) for Access Analyzer, which we use to validate the correctness of our re-implementation. Specifically, for the 6-key dataset with 11 to 15 statements, both versions time out (> 1 hour). ...*"

**Steps:**

See `archive_results/accessanalyzer_cli/run.log` for the 10_allow_result.json case. 

```test
[4/5] intents saved at ./aws_result/Scalability_06Keys//10_allow_result.json
[5/5] 2025-05-11 18:06:51: Total running time  : 3386 seconds
[5/5] 2025-05-11 18:06:51: Final intents count : 1
```

AWS automatically terminated the mining process after `3386` seconds. This indicates that invoking Access Analyzer via the CLI will time out on a 6-key dataset with 10 to 15 statements.

See the last line of `archive_results/accessanalyzer_z3_miner_1rs/Scalability_05Keys/summary.csv`. 

```
14,196,225,3.9041,895.8580
15,225,256,4.5458,1183.9881
```

The final column value of `2596.6094` seconds indicates that only up to 10 statements were mined. This implies that the reimplemented Access Analyzer timed out when handling 11 to 15 statements.

**To this end, the conclusion holds.**

---

**Target Conclusion (Line 751 in Section 5):** 

"*AWS provides an online Command Line Interface (CLI) for Access Analyzer, which we use to validate the correctness of our re-implementation. ... Both versions produce identical intents on the Correctness, 5-key, and 6-key datasets.*"

**Running:** 

The following command compares intents between the reimplemented Access Analyzer and the CLI-based Access Analyzer.

```
sh tools/accessanalyzer-reimpl/running_accessanalyzer_miner_compare.sh
```

<!-- > Note: The CLI-based Access Analyzer experiences timeouts on some datasets. -->

**Output:**

- `results/accessanalyzer_miner_compare_results/*.log`

---

<!-- **Running:**

Besides, to confirm the correctness of our reimplemented Access Analyzer for intent reduction, we also compare intents between the reimplemented Access Analyzer and AccessRefinery.

```
sh tools/accessanalyzer-reimpl/running_accessanalyzer_reducer_compare.sh
```

**Output:**

- `results/accessanalyzer_reducer_compare_results/*.log`

---  -->

**Target Conclusion (Line 760 in Section 6.1):** 

"*We conducted a series of basic Boolean operation tests.*"

**Running:**

Running maven test for [MCPTest.java](accessrefinery/mcp/src/test/java/org/mcp/core/MCPTest.java). 

```
mvn test -pl ./accessrefinery/mcp -Dtest=MCPTest.java#testComplexSATOperations
```

**Output:**

```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.212 s
[INFO] Finished at: 2026-04-10T17:10:38+08:00
[INFO] ------------------------------------------------------------------------
```

---

**Target Figure (Line 776 in Section 6.1):** Figure 10  

<img src="docs/figures/figure10.png" width="600"/>

**Required logs:**

Use the `NumberMCI` values in `accessrefinery_bdd_miner_10rs/Correctness/summary.txt` to plot Figure 10 of the paper.


**Running:** (Preserve the parentheses when executing the command.)
```shell
(cd paper_figures && gnuplot gnuplot/RQ1-Experiment-Correctness.plt)
```

**Output:**

- `paper_figures/results/RQ1-Experiment-Correctness.pdf`

---

**Target Conclusion (Line 770 in Section 6.1):** 

"*We compared the intents produced by AccessRefinery (without intent reduction), our re-implementation of Access Analyzer, and the AWS Access Analyzer via the CLI API. On synthetic datasets, all three produce the same set of intents.*"

**Running:**

The following commands check whether the intents mined by AccessRefinery are consistent with those from CLI-based Access Analyzer. 

```bash
# Compare AccessRefinery and CLI-based Access Analyzer
sh tools/accessrefinery/running_accessrefinery_miner_compare.sh

# Compare AccessRefinery and reimplemented Access Analyzer
# 
sh tools/accessanalyzer-reimpl/running_accessanalyzer_miner_compare_with_refinery.sh
```

<!-- > Note that although we previously compared the reimplemented Access Analyzer against the CLI-based Access Analyzer, the CLI-based tool suffers from incomplete intent mining due to timeouts. Therefore, we instead compared the reimplemented Access Analyzer with AccessRefinery and confirmed that their results are consistent. -->

**Output:**

- `results/`
  - `accessrefinery_miner_compare_results/*.log`
  - `accessanalyzer_miner_compare_results_with_refinery/*.log`

---

**Target Conclusion (Line 788 in Section 6.1):** 
"*(1) The reduced intents fully cover the policy. (2) Removing any intent from the reduced intents causes the remaining intents to no longer cover the policy.*"

**Running:**

//Todo @after-the-end

**Output:**

---

**Target Figure (Line 804 in Section 6.2)**: Figure 11 in the paper.

<img src="docs/figures/figure11.png" width="600"/>

**Required logs**:

- `accessrefinery_bdd_reducer_10rs/`
  - `Scalability_05Keys/summary.txt`
  - `Scalability_06Keys/summary.txt`

The `NumberMCI` column represents the number of intents before reduction, and the `NumberRRI` column represents the number after reduction.

> Note: The real-world results in the paper cannot be open sourced for commercial reasons.

**Running:**

(cd paper_figures && gnuplot gnuplot/RQ2-Experiment-Effectiveness.plt)

**Output:**

- `paper_figures/results/RQ2-Experiment-Effectiveness.pdf`

---

**Target Figure (Line 842 in Section 6.3)**: Figure 12 in the paper.

<img src="docs/figures/figure12.png" width="450"/>

**Required logs**:

- `accessrefinery_bdd_miner_10rs/` : `AccessRefinery` in the figure.
  - `Scalability_05Keys/summary.txt` : see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` : see `TotalTimeAverage` column
- `accessanalyzer_z3_miner_1rs/` : `Access Analyzer(Z3)` in the figure. 
  - `Scalability_05Keys/summary.csv` : see `Total Time (s)` column
  - `Scalability_06Keys/summary.csv` : see `Total Time (s)` column
- `accessanalyzer_cvc5_miner_1rs/` : `Access Analyzer(CVC5)` in the figure. 
  - `Scalability_05Keys/summary.csv` : see `Total Time (s)` column
  - `Scalability_06Keys/summary.csv` : see `Total Time (s)` column

<!-- 
The `TotalTimeAverage` column represents the average runtime over 10 rounds of `AccessRefinery` in the figure. -->

**Running:**

```
(cd paper_figures && gnuplot gnuplot/RQ3-Experiment-Scalability-Mining.plt)
```

**Output:**

- `paper_figures/results/RQ3-Experiment-Scalability-Mining.pdf`

---

**Target Figure (Line 850 in Section 6.3)**: Figure 13 in the paper.

<img src="docs/figures/figure13.png" width="450"/>

**Required logs**:

- `accessrefinery_bdd_reducer_10rs/` : `AccessRefinery` in the figure.
  - `Scalability_05Keys/summary.txt` : see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` : see `TotalTimeAverage` column
- `accessanalyzer_z3_reducer_1rs/` : `Access Analyzer(Z3)` in the figure. 
  - `Scalability_05Keys/summary.csv` : see `Total Time (s)` column
  - `Scalability_06Keys/summary.csv` : see `Total Time (s)` column
- `accessanalyzer_cvc5_reducer_1rs/` : `Access Analyzer(CVC5)` in the figure. 
  - `Scalability_05Keys/summary.csv` : see `Total Time (s)` column
  - `Scalability_06Keys/summary.csv` : see `Total Time (s)` column

**Running:**

```
(cd paper_figures && gnuplot gnuplot/RQ3-Experiment-Scalability-Reducing.plt)
```

**Output:**

- `paper_figures/results/RQ3-Experiment-Scalability-Reducing.pdf`


---

**Target Figure (Line 875 in Section 6.4)**: Figure 14 in the paper.

<img src="docs/figures/figure13.png" width="450"/>

These logs are omitted for commercial reasons.

---

**Target Conclusion (Line 884 in Section 6.5)**:

"For intent mining, using JavaBDD is 1-6x faster than using MiniSAT (for clarity, the figure is omitted)."

**Required logs**:

- `accessrefinery_bdd_miner_10rs/` : time for `JavaBDD` in the paper
  - `Scalability_05Keys/summary.txt` : see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` : see `TotalTimeAverage` column
- `accessrefinery_sat_miner_10rs/` : time for `MiniSAT` in the paper
  - `Scalability_05Keys/summary.txt` : see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` : see `TotalTimeAverage` column

**Running:**

The following command generates the figure omitted in the paper.

```

(cd paper_figures && gnuplot gnuplot/RQ5-Experiment-MicroBenchmark-Mining.plt)

```

**Output:**

- `paper_figures/results/RQ5-Experiment-MicroBenchmark-Mining.pdf`

---

**Target Figure (Line 898 in Section 6.5)**: Figure 15 in the paper.

<img src="docs/figures/figure15.png" width="450"/>

**Required logs (Intent Reduction)**:

- `accessrefinery_bdd_reducer_10rs/` time for `JavaBDD` in the paper
  - `Scalability_05Keys/summary.txt` see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` see `TotalTimeAverage` column
- `accessrefinery_sat_reducer_3rs/` time for `MiniSAT` in the paper
  - `Scalability_05Keys/summary.txt` see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` see `TotalTimeAverage` column

> Note: For a fair comparison, compare average runtime per round (normalized to 10 rounds). Since SAT-based reduction is much slower, we report SAT results for only 3 rounds.


**Running:**

The following command generates Figure 15 in the paper.

```

(cd paper_figures && gnuplot gnuplot/RQ5-Experiment-MicroBenchmark-Reducing.plt)

```

**Output:**

- `paper_figures/results/RQ5-Experiment-MicroBenchmark-Reducing.pdf`


---

**Target Table (Line 913 in Section 6.6)**: Table 2 in the paper.

<img src="docs/figures/table2.png" width="450"/>

**Required logs**:

- `accessrefinery_bdd_miner_10rs/`
  - `Scalability_05Keys/summary.txt`

The `NumberRRI` column is the number of SMT solving rounds in the table. The `MCILabelsTimeAverage` column is the average MCP preprocessing time in the table. `MCIOperationsTimeAverage / NumberMCI` is the single-round Boolean solving time in the table.

- `accessanalyzer_z3_miner_1rs/`
  - `Scalability_05Keys/summary.csv`
- `accessanalyzer_cvc5_miner_1rs/`
  - `Scalability_05Keys/summary.csv`

The `Average Time per Round (s)` column is the average single-round SMT solving time in the table for `Z3` and `CVC5`.

The table is generated by LaTeX; therefore, no plotting program is used.

---
