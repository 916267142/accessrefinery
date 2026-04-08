<!-- 
<img src="images/logo.png?raw=True" align="right" width="20%"/> AccessRefinery: Fast 
-->

<!-- <div style="display: flex; justify-content: space-between; align-items: center;">
    <h1 style="margin: 0;">AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud</h1>
    <img src="logo.png" width="30%">
</div> -->

# AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud

by [Ning Kang](https://xjtu-netverify.github.io/people/nkang/), [Peng Zhang](https://xjtu-netverify.github.io/people/pzhang/) and [Jianyuan Zhang](https://xjtu-netverify.github.io/people/jyzhang/) at [ANTS lab](https://xjtu-netverify.github.io/).

![Java](https://img.shields.io/badge/Java-17-007396?logo=java&logoColor=white)
![Tests](https://img.shields.io/badge/tests-passing-brightgreen?logo=java)
![License](https://img.shields.io/badge/license-MIT-green)
![Paper](https://img.shields.io/badge/paper-FSE2026-orange)

<!-- > Ning Kang, Peng Zhang, Jianyuan Zhang, Hao Li, Dan Wang, Zhenrong Gu, Weibo Lin, 
> Shibiao Jiang, Zhu He, Xu Du, Longfei Chen, Jun Li, and Xiaohong Guan
> "AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud", ACM FSE 2026 -->

## About AccessRefinery

**AccessRefinery** automatically mines access control intents from IAM (Identity and Access Management) policies. These intents help users verify policy correctness. Compared with [AWS Access Analyzer](https://link.springer.com/content/pdf/10.1007/978-3-030-53288-8_9.pdf) and its [commercial deployment](https://docs.aws.amazon.com/IAM/latest/UserGuide/access-analyzer-concepts.html), AccessRefinery accelerates mining by ~10-100x and reduces the number of intents by up to ~10x.

The key idea of **AccessRefinery** for accelerating multi-round SMT solving is to reduce redundancy by preprocessing constraints into bit-vector constraints using our Multi-Theory Constraint Preprocessor (MCP).  
For intent reduction, **AccessRefinery** computes a compact set that covers all mined intents by solving a minimum set-cover problem.
Moreover, we design MCP as a separate module from **AccessRefinery**, allowing other researchers to reuse MCP flexibly.

For technical details, see our FSE 2026 paper: [*AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud*](https://xjtu-netverify.github.io/papers/AccessRefinery/accessrefinery_final_version.pdf).

<!-- 
After setting up Linux, follow [Install](INSTALL.md) to install the environment, and compile **AccessRefinery** and our reproduced **Access Analyzer** (baseline). -->

## Structure

This repository includes the implementation of **AccessRefinery**, along with datasets, reproduction scripts, and archived results.

- `projects/`: Implementation of **AccessRefinery**.
    - `bdd/`: Implementation of the binary decision diagram backend used by MCP.
    - `mcp/`: Implementation of the Multi-Theory Constraint Preprocessor (MCP).
    - `refinery/`: Implementation of intent mining and reduction.
- `data/`:
    - `Correctness/`: Dataset for correctness experiments.
    - `Scalability_05Keys/`: Synthetic dataset for scalability experiments.
    - `Scalability_06Keys/`: Synthetic dataset for scalability experiments.
- `tools/`: Scripts for running the experiments.
- `pom.xml`: Maven root configuration.
- `paper_figures/`: Scripts for generating the figures in the paper.
- `archive_results/`: Archived experimental results.

For comparison, the repository also includes two AWS Access Analyzer artifacts:

- `AccessAnalyzerCLI/`: Scripts for running AWS Access Analyzer via the CLI API, along with run instructions.
- `AccessAnalyzer/`: Our re-implementation of Access Analyzer and run instructions.

<!-- ## Project Structure

This repository contains the implementation of AccessRefinery and the baselines used for comparison.

- `accessrefinery/`: Implementation of AccessRefinery (our approach)
- `baselines/`: Compared methods
  - `accessanalyzer-reimpl/`: Our reimplementation of Access Analyzer
  - `accessanalyzer-cli/`: Scripts for the official AWS Access Analyzer CLI
- `data/`: Experimental datasets
- `tools/`: Scripts for running experiments -->

## Setup

See [Requirements](REQUIREMENTS.md) and [Installation](INSATLL.md) for setup details, including our **re-implementation of Access Analyzer**.

After compilation, the `target/` directory will contain `refinery-1.0.jar` (for intent mining and reduction) and `mcp-1.0.jar` (which can be reused in other projects for fast multi-round SMT solving).

## Using Multi-Theory Constraint Preprocessor (MCP)

MCP is a data structure for fast multi-round SMT solving. It supports regular expressions, IP prefixes/bit-vectors, ranges, and sets. In this repository, MCP is already integrated into **AccessRefinery**, so you can use it directly without a separate installation.

### Reuse in Another Project

Follow [Install](INSTALL.md) to generate the JAR package. Recall that:

```shell
mvn clean package
```

This generates `target/mcp-1.0.jar`. Install it into your local Maven repository:

```
mvn install:install-file \
    -Dfile=target/mcp-1.0.jar \
    -DgroupId=org.ants \
    -DartifactId=accessrefinery \
    -Dversion=1.0 \
    -Dpackaging=jar \
    -DgeneratePom=true
```

Then add the dependency to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.ants</groupId>
        <artifactId>accessrefinery</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

### Example

This example follows the running example in the paper (line 375).
Suppose we have the following IAM policy and a target intent, `Intent_6` (`Resource`: `dept*/user1.txt`, `IpAddress`: `112.0.0.0/24`).

```json
{
    "Statement": [
        {
            "Effect": "Allow",
            "Resource": ["dept*/user1.txt", "dept1/user*.txt"],
            "Condition": {
                "IpAddress": {
                    "aws:SourceIp": ["112.0.0.0/24", "113.0.0.0/24"]
                }
            }
        },
        {
            "Effect": "Deny",
            "NotResource": "dept1/user*.txt",
            "Condition": {
                "IpAddress": {
                    "aws:SourceIp": "112.0.0.0/24"
                }
            }
        },
        {
            "Effect": "Deny",
            "NotResource": "dept*/user1.txt",
            "Condition": {
                "IpAddress": {
                    "aws:SourceIp" : "113.0.0.0/24"
                }
            }
        }
    ]
}
```

Our goal is to check the satisfiability of three formulas: $\neg I_6 \land P$, $I_6 \land \neg P$, and $I_6 \land P$. The corresponding MCP code is shown below.
The example is also included in [MCPFactoryTest.java](projects/mcp/src/test/java/org/mcp/core/MCPFactoryTest.java) and runs automatically during `mvn package`.

```java
package com.example;
import org.batfish.datamodel.Prefix;
import org.junit.Assert;
import org.mcp.core.MCPBitVector;
import org.mcp.core.MCPFactory;
import org.mcp.core.MCPFactory.MCPType;
import org.mcp.variables.statics.LabelType;

public class Main {
    public static void main(String[] args) {
        MCPFactory mcp = new MCPFactory(MCPType.BDD);
        mcp.addVar("Res", LabelType.REGEXP, "dept*/user1.txt");
        mcp.addVar("Res", LabelType.REGEXP, "dept1/user*.txt");
        mcp.addVar("IP", LabelType.PREFIX, Prefix.parse("112.0.0.0/24"));
        mcp.addVar("IP", LabelType.PREFIX, Prefix.parse("113.0.0.0/24"));
        mcp.updates();

        MCPBitVector res1 = mcp.getVar("Res", "dept*/user1.txt");
        MCPBitVector res2 = mcp.getVar("Res", "dept1/user*.txt");
        MCPBitVector ip1 = mcp.getVar("IP", Prefix.parse("112.0.0.0/24"));
        MCPBitVector ip2 = mcp.getVar("IP", Prefix.parse("113.0.0.0/24"));
        MCPBitVector s1 = (res1.or(res2)).and(ip1.or(ip2));
        MCPBitVector s2 = res1.not().and(ip1);
        MCPBitVector s3 = res2.not().and(ip2);
        MCPBitVector policy = s1.diff(s2).diff(s3);
        MCPBitVector intent6 = res1.and(ip1);

        // ¬I6∧P is satisfiable.
        Assert.assertTrue(!policy.and(intent6.not()).isZero());
        // I6∧¬P is unsatisfiable.
        Assert.assertTrue(policy.not().and(intent6).isZero());
        // I6∧P is satisfiable.
        Assert.assertTrue(!policy.and(intent6).isZero());
    }
}
```

## Using AccessRefinery

**AccessRefinery** builds on MCP for IAM intent mining and reduction.

Follow [Install](INSTALL.md) to build the JAR package, recall again:

```shell
mvn clean package
```

To run **AccessRefinery**, use:

```shell
$ java -jar target/refinery-1.0.jar [options]
```

**Command-line options:**
- `-h, --help` : Show help information.
- `-m, --mine` : Enable intent mining.
- `-r, --reduce` : Enable intent reduction.
- `-f, --file <path>` : Input path for policy files (must be under `data/`).
- `-s, --sat` : Use SAT to encode bit-vectors (default is BDD).
- `--round <number>` : Number of mining rounds (to reduce experimental bias).

**Example:**
```shell
$ java -jar target/refinery-1.0.jar -m -r --round 1 -f data/Correctness
```

The command produces logs similar to the following:

```cmd
[INFO] 2026-04-05 22:51:33 : ----------[ AccessRefinery Mode ]-------------
[INFO] 2026-04-05 22:51:33 : input  path: data/Correctness
[INFO] 2026-04-05 22:51:33 : output path: result/Correctness
[INFO] 2026-04-05 22:51:33 : ----------< 1th policy - 11_allow_allow_equal.json >-----------
[INFO] 2026-04-05 22:51:33 : [1/6]  finish parser policy
[INFO] 2026-04-05 22:51:33 : [2/6]  finish ECs calculation
...
```

After processing all policies, results are generated in the `results/Correctness/` directory. The output includes:
- `xxx.json`: The generated intents for each policy.
- `xxx.csv`: Statistics for multi-round SMT solving for each policy.
- `summary.txt`: Summary statistics for all policies in a folder.

In addition, one file is generated in the current path:
- `accessrefinery.log` : Records the running log.

## Evaluation Reproduction

This section explains how to reproduce the AccessRefinery results reported in the paper figures.
For other results, see [Re-implementation of Access Analyzer]() and [Access Analyzer via the CLI API]().

If you do not want to run Access Analyzer, you can still verify the evaluation results reported in the paper, since all experimental results are archived in `/archive_result`.

> It is strongly recommended to skip AWS CLI reproduction, because the environment setup is complex (AWS account registration, billing setup, and CLI credential configuration).

### Running AccessRefinery

The following scripts reproduce the AccessRefinery results，which automatically invoke `target/refinery-1.0.jar`.
```bash
$ sh tools/running_bdd_miner.sh
$ sh tools/running_sat_miner.sh
$ sh tools/running_bdd_reducer.sh
$ sh tools/running_sat_reducer.sh
```

The following folders will be generated under `result/`. The difference between `bbd_` and `sat_` is the backend used to represent bit-vectors. The suffix `10rs` means the experiment is run for 10 rounds and the average is reported. Because `accessrefinery_sat_reducer_3rs` runs very slowly, we report results for only three rounds.

- `accessrefinery_bdd_miner_10rs/`: Intent mining results for 10 rounds with JavaBDD.
- `accessrefinery_sat_miner_10rs/`: Intent mining results for 10 rounds with MiniSAT.
- `accessrefinery_bdd_reducer_10rs/`: Intent mining and reduction results for 10 rounds with JavaBDD.
- `accessrefinery_sat_reducer_3rs/`: Intent mining and reduction results for 3 rounds with MiniSAT.

### Correspondence to Paper Sections

After generating the experimental results, we explain how to reproduce the figures，tables and conclusions reported in the paper.

---

#### 6.1 Is the re-implementation of Access Analyzer valid, and is AccessRefinery correct?

##### 6.1.2 Correctness of AccessRefinery

- **Correctness of MCP**
Basic Boolean operations are tested in [MCPTest.java](projects/mcp/src/test/java/org/mcp/core/MCPTest.java). These tests run automatically during `mvn package`.

- **Correctness of Intent Miner**
The following commands check whether the intents mined by AccessRefinery are consistent with those from AWS Access Analyzer (via CLI). Logs are generated in `compare_result/`:

```bash
$ # JSON library required by the comparison script (jq 1.6)
$ sudo apt install jq 
$ sh tools/running_batch_compare.sh
```

Then use the `NumberMCI` values in `accessrefinery_bdd_miner_10rs/Correctness/summary.txt` to plot Figure 9 of the paper.

- **Correctness of Intent Reducer**
Run `sh tools/running_bdd_reducer.sh`, then compare the values in `accessrefinery_bdd_reducer_10rs/Correctness/summary.txt`: `NumberMCI` is the number of intents before reduction, and `NumberRRI` is the number after reduction.

---

#### Section 6.2 Can AccessRefinery reduce the number of intents?

**Target**: Figure 10 in the paper.

<img src="./tools/Experiment-Effectiveness-ThreeGraph.png" width="450"/>
<!-- 
![Figure 10](tools/Experiment-Effectiveness-ThreeGraph.png) -->

**Required logs**:
- `accessrefinery_bdd_reducer_10rs/`
    - `Scalability_05Keys/summary.txt`
    - `Scalability_06Keys/summary.txt`

The `NumberMCI` column represents the number of intents before reduction, and the `NumberRRI` column represents the number after reduction.

> Note: The real-world results in the paper cannot be open-sourced for commercial reasons.

---

#### Section 6.3 Can AccessRefinery speedup intent mining and reduction by using MCP?

**Target**: Figure 13 in the paper.

**Required logs**:
- `accessrefinery_bdd_miner_10rs/`
    - `Scalability_05Keys/summary.txt`
    - `Scalability_06Keys/summary.txt`

The `TotalTimeAverage` column represents the average runtime over 10 rounds.

---

#### Section 6.4 How does AccessRefinery performon real-world datasets?

**Target**: Real-world evaluation discussed in the paper.

**Required logs**:
- Not released in this artifact.

These logs are omitted for commercial reasons.

---

#### Section 6.5 Is SAT or BDD better for intent mining and reduction?

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

**Target (Intent Reduction)**: Figure 13 in the paper.

**Required logs (Intent Reduction)**:
- `accessrefinery_bdd_reducer_10rs/`
    - `Scalability_05Keys/summary.txt`
    - `Scalability_06Keys/summary.txt`
- `accessrefinery_sat_reducer_3rs/`
    - `Scalability_05Keys/summary.txt`
    - `Scalability_06Keys/summary.txt`

For a fair comparison, compare average runtime per round using `TotalTimeAverage / rounds` (BDD: 10 rounds, SAT: 3 rounds).

---

#### Section 6.6 How does AccessRefinery accelerate single-round solving in multi-round SMT solving compared to SMT solvers?

**Target**: Table 2 in the paper.

**Required logs**:

- `accessrefinery_bdd_miner_10rs/`
    - `Scalability_05Keys/`
    - `Scalability_06Keys/`

`MCILabelsTimeAverage` is the average MCP preprocessing time.
`NumberRRI` is the number of reduced intents.

---

#### Drawing the figures in the paper

The following commands install `gnuplot` and generate all figures used in the experiments.
The generated figures are saved in `paper_figures/results/`.

```shell
sudo apt install gnuplot
cd paper_figures
sh draw.sh
```

## Developer

For questions about installation and running, please contact Ning Kang at 916267142@qq.com.


<!-- #### Results of AWS AccessAnalyzer via CLI

- `/accessanalyzer_web`: Results of intent mining using AWS AccessAnalyzer via CLI

Note: AWS Access Analyzer is accessed remotely, so only correctness experiments can be performed, not performance experiments.

The following instructions can be used to reproduce the results (it is strongly recommended to skip this step, as AWS CLI environment configuration is complex):
- [Running AWS Access Analyzer via CLI](tools/AccessAnalyzer.md) -->


<!-- Note: The result for `Scalability_05Keys/12_allow_result.json` may differ because AWS Access Analyzer may time out (the result will be marked with `"error": "INTERNAL_ERROR"` by Access Analyzer). This is normal. -->

<!-- 1. 功能性奖 说明可复现  
2. 可用性奖 代码结构性很好，别人可以复用
3. 公开性奖 代码挂到Zendo上面

注意：
1. 附上作者邮件，解释如何运行和安装
2. MCP解耦，AccessRefinery和MCP都附上小例子，说明如何使用 -->