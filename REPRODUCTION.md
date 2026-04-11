
### Verifying Claims in the Paper

After generating `results/`, we explain how to reproduce the figures, tables, and conclusions reported in the paper.

#### 1. Target Conclusion (Line 750 in Section 5):

"*AWS provides an online Command Line Interface (CLI) for Access Analyzer, which we use to validate the correctness of our re-implementation. Specifically, for the 6-key dataset with 11 to 15 statements, both versions time out (> 1 hour). ...*"

**Steps:**

See `archive_results/accessanalyzer_cli/run.log` for the 10_allow_result.json case. AWS automatically terminated the mining process after `3386` seconds. This indicates that invoking *AWS Access Analyzer* via the CLI will time out on a 6-key dataset with 10 to 15 statements.

```test
[4/5] intents saved at ./aws_result/Scalability_06Keys//10_allow_result.json
[5/5] 2025-05-11 18:06:51: Total running time  : 3386 seconds
[5/5] 2025-05-11 18:06:51: Final intents count : 1
```

See the last line of `archive_results/accessanalyzer_z3_miner_1rs/Scalability_05Keys/summary.csv`. The final column value of `2596.6094` seconds indicates that only up to 10 statements were mined. This implies that the *reimplemented Access Analyzer* timed out when handling 11 to 15 statements.

```text
9,81,676,2.3068,1569.1607
10,100,881,2.9338,2596.6094
```

To this end, the conclusion holds.

#### 2. Target Conclusion (Line 751 in Section 5):

"*AWS provides an online Command Line Interface (CLI) for Access Analyzer, which we use to validate the correctness of our re-implementation. ... Both versions produce identical intents on the Correctness, 5-key, and 6-key datasets.*"

**Running:**

The following command compares intents between the *reimplemented Access Analyzer* and the *CLI-based Access Analyzer*.

```shell
sh tools/accessanalyzer-reimpl/running_accessanalyzer_compare.sh
```

**Expected Output:**

- `results/accessanalyzer_miner_compare_results/*.log`

#### 3. Target Conclusion (Line 760 in Section 6.1): 

"*We conducted a series of basic Boolean operation tests.*"

**Running:**

Running maven test for [MCPTest.java](https://github.com/XJTU-NetVerify/accessrefinery/blob/main/accessrefinery/mcp/src/test/java/org/mcp/core/MCPTest.java).

```shell
# The execution takes about 3 minutes.
mvn clean install
mvn test -pl ./accessrefinery/mcp -Dtest=MCPTest.java#testComplexSATOperations
```

**Expected Output:**

```text
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.212 s
[INFO] Finished at: 2026-04-10T17:10:38+08:00
[INFO] ------------------------------------------------------------------------
```

#### 4. Target Figure (Line 776 in Section 6.1): Figure 10  

<img src="docs/figures/figure10.png" width="450"/>


**Running:**

We provide a script to clear previous plotting results and ensure that plotting scripts use data from `results`.

```bash
sh tools/clean_plotting.sh
```

**Expected Output:**

```bash
Clearing paper_figures/data
Clearing paper_figures/results
```

**Running:**

Use the `NumberMCI` values in `accessrefinery_bdd_miner_10rs/Correctness/summary.txt` to generate data of Figure 10 in the paper.

```bash
bash ./tools/figures/extract_correctness_synthetic.sh
```

**Expected Output:**

- `paper_figures/data/Experiment-Correctness-Synthetic.dat`

**Running:**
(Preserve the parentheses when executing the command.)

```shell
(cd paper_figures && gnuplot gnuplot/RQ1-Experiment-Correctness.plt)
```

**Expected Output:**

- `paper_figures/results/RQ1-Experiment-Correctness.pdf`

#### 5. Target Conclusion (Line 770 in Section 6.1):

"*We compared the intents produced by AccessRefinery (without intent reduction), our re-implementation of Access Analyzer, and the AWS Access Analyzer via the CLI API. On synthetic datasets, all three produce the same set of intents.*"

**Running:**

```bash
# Compare AccessRefinery and CLI-based Access Analyzer
sh tools/accessrefinery/running_accessrefinery_miner_compare.sh

# Compare AccessRefinery and reimplemented Access Analyzer
sh tools/accessanalyzer-reimpl/running_accessanalyzer_compare_with_refinery.sh
```

**Expected Output:**

- `results/`
  - `accessrefinery_miner_compare_results/*.log`
  - `accessanalyzer_miner_compare_results_with_refinery/*.log`

#### 6. Target Conclusion (Line 788 in Section 6.1):

"*(1) The reduced intents fully cover the policy. (2) Removing any intent from the reduced intents causes the remaining intents to no longer cover the policy.*"

**Running:**

```bash
bash ./tools/accessanalyzer-reimpl/check_coverage.sh
```

**Expected Output:**

- `results/coverage_check/`
  - `Correctness/*_coverage.log`
  - `Scalability_05Keys/*_coverage.log`
  - `Scalability_06Keys/*_coverage.log`

#### 7. Target Figure (Line 804 in Section 6.2): Figure 11

<img src="docs/figures/figure11.png" width="450"/>

**Running:**

The following command generates data of Figure 11 in the paper from

- `accessrefinery_bdd_reducer_10rs/`
  - `Scalability_05Keys/summary.txt`
  - `Scalability_06Keys/summary.txt`

The `NumberMCI` column represents the number of intents before reduction, and the `NumberRRI` column represents the number after reduction.

*Note: The real-world results in the paper cannot be open sourced for commercial reasons.*

```bash
bash tools/figures/extract_effectiveness_synthetic.sh
```

**Expected Output:**

- `paper_figures/data/`
  - `Experiment-Effectiveness-Synthetic-K2.dat`
  - `Experiment-Effectiveness-Synthetic-K3.dat`

**Running:**

```shell
# plot the figure
(cd paper_figures && gnuplot gnuplot/RQ2-Experiment-Effectiveness.plt)
ls paper_figures/results/RQ2*
```

**Expected Output:**

```text
paper_figures/results/RQ2-Experiment-Effectiveness.pdf
```

#### 8. Target Figure (Line 842 in Section 6.3): Figure 12

<img src="docs/figures/figure12.png" width="450"/>

**Running:**

The following command generates data of Figure 12 in the paper from

- `accessrefinery_bdd_miner_10rs/` : `AccessRefinery` in the figure.
  - `Scalability_05Keys/summary.txt` : see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` : see `TotalTimeAverage` column
- `accessanalyzer_z3_miner_1rs/` : `Access Analyzer(Z3)` in the figure.
  - `Scalability_05Keys/summary.csv` : see `Total Time (s)` column
  - `Scalability_06Keys/summary.csv` : see `Total Time (s)` column
- `accessanalyzer_cvc5_miner_1rs/` : `Access Analyzer(CVC5)` in the figure.
  - `Scalability_05Keys/summary.csv` : see `Total Time (s)` column
  - `Scalability_06Keys/summary.csv` : see `Total Time (s)` column

```bash
bash tools/figures/extract_scalability_MCI.sh
```

**Expected Output:**

- `paper_figures/data/`
  - `Experiment-Scalability-MCI-K2.dat`
  - `Experiment-Scalability-MCI-K3.dat`

**Running:**

```shell
(cd paper_figures && gnuplot gnuplot/RQ3-Experiment-Scalability-Mining.plt)
```

**Expected Output:**

- `paper_figures/results/RQ3-Experiment-Scalability-Mining.pdf`

#### 9. Target Figure (Line 850 in Section 6.3): Figure 13

<img src="docs/figures/figure13.png" width="450"/>

**Running:**

The following command generates data of Figure 13 in the paper from

- `accessrefinery_bdd_reducer_10rs/` : `AccessRefinery` in the figure.
  - `Scalability_05Keys/summary.txt` : see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` : see `TotalTimeAverage` column
- `accessanalyzer_z3_reducer_1rs/` : `Access Analyzer(Z3)` in the figure. 
  - `Scalability_05Keys/summary.csv` : see `Total Time (s)` column
  - `Scalability_06Keys/summary.csv` : see `Total Time (s)` column
- `accessanalyzer_cvc5_reducer_1rs/` : `Access Analyzer(CVC5)` in the figure. 
  - `Scalability_05Keys/summary.csv` : see `Total Time (s)` column
  - `Scalability_06Keys/summary.csv` : see `Total Time (s)` column

```bash
bash tools/figures/extract_scalability_RRI.sh
```

**Expected Output:**

- `paper_figures/data/`
  - `Experiment-Scalability-RRI-K2.dat`
  - `Experiment-Scalability-RRI-K3.dat`

**Running:**

```shell
(cd paper_figures && gnuplot gnuplot/RQ3-Experiment-Scalability-Reducing.plt)
```

**Expected Output:**

- `paper_figures/results/RQ3-Experiment-Scalability-Reducing.pdf`

#### 10. Target Figure (Line 875 in Section 6.4): Figure 14

<img src="docs/figures/figure14.png" width="450"/>

These logs are omitted for commercial reasons.

#### 11. Target Conclusion (Line 884 in Section 6.5):

"*For intent mining, using JavaBDD is 1-6x faster than using MiniSAT (for clarity, the figure is omitted).*"

**Running:**

The following command generates data of Figure 14 in the paper from

- `accessrefinery_bdd_miner_10rs/` : time for `JavaBDD` in the paper
  - `Scalability_05Keys/summary.txt` : see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` : see `TotalTimeAverage` column
- `accessrefinery_sat_miner_10rs/` : time for `MiniSAT` in the paper
  - `Scalability_05Keys/summary.txt` : see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` : see `TotalTimeAverage` column

// TODO @916267142
// Is it duplicated to run the command again here?

```bash
bash tools/figures/extract_scalability_MCI.sh
```

**Expected Output:**

- `paper_figures/data/`
  - `Experiment-Scalability-MCI-K2.dat`
  - `Experiment-Scalability-MCI-K3.dat`

**Running:**

The following command generates the figure omitted in the paper.

```shell
(cd paper_figures && gnuplot gnuplot/RQ5-Experiment-MicroBenchmark-Mining.plt)
```

**Expected Output:**

- `paper_figures/results/RQ5-Experiment-MicroBenchmark-Mining.pdf`

#### 12. Target Figure (Line 898 in Section 6.5): Figure 15

<img src="docs/figures/figure15.png" width="450"/>

**Running:**

The following command generates data of Figure 15 in the paper from

- `accessrefinery_bdd_reducer_10rs/` time for `JavaBDD` in the paper
  - `Scalability_05Keys/summary.txt` see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` see `TotalTimeAverage` column
- `accessrefinery_sat_reducer_3rs/` time for `MiniSAT` in the paper
  - `Scalability_05Keys/summary.txt` see `TotalTimeAverage` column
  - `Scalability_06Keys/summary.txt` see `TotalTimeAverage` column

> Note: For a fair comparison, compare average runtime per round (normalized to 10 rounds). Since SAT-based reduction is much slower, we report SAT results for only 3 rounds.

// TODO @916267142
// Is it duplicated to run the command again here?

```bash
bash tools/figures/extract_scalability_RRI.sh
```

**Expected Output:**

- `paper_figures/data/`
  - `Experiment-Scalability-RRI-K2.dat`
  - `Experiment-Scalability-RRI-K3.dat`

**Running:**

The following command generates Figure 15 in the paper.

```shell
(cd paper_figures && gnuplot gnuplot/RQ5-Experiment-MicroBenchmark-Reducing.plt)
```

**Expected Output:**

- `paper_figures/results/RQ5-Experiment-MicroBenchmark-Reducing.pdf`

#### 13. Target Table (Line 913 in Section 6.6): Table 2

<img src="docs/figures/table2.png" width="450"/>

**Required logs**:

- `accessrefinery_bdd_miner_10rs/`
  - `Scalability_05Keys/summary.txt`

The `NumberRRI` column in `summary.txt` file is the number of SMT solving rounds in the table. The `MCILabelsTimeAverage` column is the average MCP preprocessing time in the table. `MCIOperationsTimeAverage / NumberMCI` is the single-round Boolean solving time in the table.

- `accessanalyzer_z3_miner_1rs/`
  - `Scalability_05Keys/summary.csv`
- `accessanalyzer_cvc5_miner_1rs/`
  - `Scalability_05Keys/summary.csv`

The `Average Time per Round (s)` column in `summary.csv` file is the average single-round SMT solving time in the table for `Z3` and `CVC5`.

The table is generated by LaTeX. Therefore, no plotting program is used.