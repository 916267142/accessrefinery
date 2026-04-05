<!-- 
<img src="images/logo.png?raw=True" align="right" width="20%"/> AccessRefinery: Fast 
<img src="logo2.png?raw=True" align="right" width="20%"/> 
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

> Ning Kang, Peng Zhang, Jianyuan Zhang, Hao Li, Dan Wang, Zhenrong Gu, Weibo Lin, 
> Shibiao Jiang, Zhu He, Xu Du, Longfei Chen, Jun Li, and Xiaohong Guan
> "AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud", ACM FSE 2026

## About AccessRefinery

**AccessRefinery** automatically mines access control intents from IAM (Identity and Access Management) policies. These intents help users verify the correctness and security of their policies. Compared with the [commercial system](https://docs.aws.amazon.com/IAM/latest/UserGuide/access-analyzer-concepts.html) in [AWS Access Analyzer](https://link.springer.com/content/pdf/10.1007/978-3-030-53288-8_9.pdf), AccessRefinery speeds up mining by ~10–100× and reduces the number of intents by up to ~10×.

The key idea behind **AccessRefinery** for accelerating intent mining is to reduce redundancy in multi-round SMT solving by preprocessing constraints into bit-vector constraints using our Multi-Theory Constraint Preprocessor (MCP).  
For intent reduction, **AccessRefinery** computes a compact set that covers all mined intents by solving a minimum set-cover problem.

For technical details and a full evaluation, refer to our FSE 2026 paper: [*AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud*](https://xjtu-netverify.github.io/papers/AccessRefinery/accessrefinery_final_version.pdf).

> Note: MCP is decoupled from **AccessRefinery**. It could be a separate project, but installation would be more complex. We therefore keep MCP and **AccessRefinery** as two separate Maven modules, allowing other researchers to reuse MCP flexibly.

---

## Setup

### Prerequisites

Ubuntu 22.04.5 is recommended. See [Requirement](REQUIREMENTS.md) for details.

### Install and Compile

After setting up Linux, follow [Install](INSTALL.md) to install the environment, and compile **AccessRefinery** and our reproduced **Access Analyzer** (baseline).

## Structure

This repository is the artifact accompanying the AccessRefinery paper. It includes the implementation, experimental datasets, reproduction scripts, archived results, and the comparison baselines used in the evaluation.

- `projects/` contains the source code of **AccessRefinery**.
    - `bdd/` implements the binary decision diagram backend used by MCP.
    - `mcp/` implements the Multi-Theory Constraint Preprocessor.
    - `refinery/` implements intent mining and reduction.
- `data/` contains the datasets used in the experiments.
    - `Correctness/` contains the synthetic dataset for correctness experiments.
    - `Scalability_05Keys/` and `Scalability_06Keys/` contain the synthetic datasets for scalability experiments.
- `tools/` contains scripts for running the experiments.
- `pom.xml` is the Maven root configuration.
- `paper_figures/` contains the source used to generate the figures in the paper.
- `archive_results/` contains archived experimental results.

After compilation, the `target/` directory will contain `refinery-1.0.jar` (for intent mining) and `mcp-1.0.jar` (which can be reused in other projects to support multi-round SMT solving).

For comparison, the repository also includes two AWS Access Analyzer artifacts:

- `AccessAnalyzerCLI/` contains the scripts and instructions for running the AWS commercial Access Analyzer through the CLI.
- `AccessAnalyzer/` contains our reproduced AWS Access Analyzer implementation and run instructions.

---

## Using Multi-Theory Constraint Preprocessor (MCP)

MCP is a data structure for multi-round SMT solving. It supports regular expressions, IP prefixes/bit-vectors, ranges, and sets. In this repository, MCP is already integrated into AccessRefinery, so you can use it directly without a separate installation.

### Reuse in Another Project

Following [Install](INSTALL.md) to generate jar package. Recall again:

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

This example follows the running example in the paper (Line 375).
Suppose we have the following IAM policy and a target intent, `Intent_6` (`Resource`: `dept*/user1.txt`, `IpAddress`: `112.0.0.0/24`).

```json
{
    "Statement": [
        {
            "Effect": "Allow",
            "Resource": "dept*/user1.txt",
            "Condition": {
                "IpAddress": {
                    "aws:SourceIp": "112.0.0.0/24"
                }
            }
        },
        {
            "Effect": "Allow",
            "Resource": "dept1/user*.txt",
            "Condition": {
                "IpAddress": {
                    "aws:SourceIp": "113.0.0.0/24"
                }
            }
        }
    ]
}
```
Suppose our goal is to check the satisfiability of three formulas: $\neg I_6 \land P$, $I_6 \land \neg P$, and $I_6 \land P$. The corresponding MCP code is shown below.
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

---

## Using AccessRefinery

AccessRefinery builds on MCP for IAM intent mining.

Follow [Install](INSTALL.md) to build the JAR package, recall again:

```shell
mvn clean package
```

To run AccessRefinery, use:

```shell
$ java -jar target/refinery-1.0.jar [options]
```

**Command-line options:**
- `-h, --help` : Show help information.
- `-m, --mine` : Enable mining mode (extract intents).
- `-r, --reduce` : Enable reduction of intents.
- `-f, --file <path>` : Input path for policy files (must be under `data/`).
- `-s, --sat` : Use SAT as the solving core (default is BDD).
- `--round <number>` : Number of mining rounds (to reduce experimental bias).
- `--merge` : The optimization of merging the output format of intents.

AccessRefinery supports mining a batch of policies. For example:

**Example:**
```shell
$ java -jar target/refinery-1.0.jar -m -r --round 1 -f data/Correctness
```

The command produces logs similar to the following:

```cmd
[INFO] 2026-04-05 22:51:33 : ----------[ AccessRefinery Mode ]-------------
[INFO] 2026-04-05 22:51:33 : logger path: /home/simple/workspace/accessrefinery-workspace/accessrefinery/accessrefinery.log
[INFO] 2026-04-05 22:51:33 : input  path: data/Correctness
[INFO] 2026-04-05 22:51:33 : output path: result/Correctness
[INFO] 2026-04-05 22:51:33 : ----------< 1th policy - 11_allow_allow_equal.json >-----------
[INFO] 2026-04-05 22:51:33 : [1/6]  finish parser policy
[INFO] 2026-04-05 22:51:33 : [2/6]  finish ECs calculation
[INFO] 2026-04-05 22:51:33 : [3/6]  finish label tree calculation
[INFO] 2026-04-05 22:51:33 : [4/6]  finish findings mining : 1
[INFO] 2026-04-05 22:51:33 : [5/6]  finish ECs calculation
[INFO] 2026-04-05 22:51:33 : [6/6]  finish findings reduction : 1
[INFO] 2026-04-05 22:51:33 : ----------< 2th policy - 12_allow_allow_overriding.json >-----------
[INFO] 2026-04-05 22:51:33 : [1/6]  finish parser policy
[INFO] 2026-04-05 22:51:33 : [2/6]  finish ECs calculation
[INFO] 2026-04-05 22:51:33 : [3/6]  finish label tree calculation
...
```

After processing all policies, results are generated in the `results/` directory. The output includes:
- `xxx.json`: The generated intents for each policy.
- `xxx.csv`: Statistics for multi-round SMT solving for each policy.
- `summary.txt`: Summary statistics for all policies in a folder.
- `accessrefinery.log` : Record the runnning log.

---

## Evaluation Reproduction

All experimental results are archived in `/archive_result`.
The following commands reproduce the AccessRefinery results.
For reproduced Access Analyzer and AWS Access Analyzer results, see the sections below.
We recommend skipping those comparison runs because we have already archived and organized the corresponding outputs.

- `accessrefinery_bdd_miner_10rs`: Intent mining results for 10 rounds with the JavaBDD backend.
- `accessrefinery_sat_miner_10rs`: Intent mining results for 10 rounds with the MiniSAT backend.
- `accessrefinery_bdd_reducer_10rs`: Intent mining and reduction results for 10 rounds with JavaBDD.
- `accessrefinery_sat_reducer_3rs`: Intent mining and reduction results for 3 rounds with MiniSAT.

The following scripts generate these results. Note that `accessrefinery_sat_reducer_3rs` runs very slowly.

```bash
$ sh tools/running_bdd_miner.sh
$ sh tools/running_sat_miner.sh
$ sh tools/running_bdd_reducer.sh
$ sh tools/running_sat_reducer.sh
```

### Section 6.1 Correctness of MCP

- **Basic Boolean operations**
Basic Boolean operations are tested in [MCPTest.java](projects/mcp/src/test/java/org/mcp/core/MCPTest.java). These tests run automatically during `mvn package`.

- **Consistency in the number of intents.**

 `compare_result`: Results of comparison

The following instructions can be used to reproduce the results:

```bash
$ # JSON library required by the comparison script (jq 1.6)
$ sudo apt install jq 
$ sh tools/running_batch_compare.sh
```

See `archive_result/compare_result`. Use the `NumberMCI` column in `summary.txt` to plot Figure 9 in the paper.

### Section 6.2 Can AccessRefinery Reduce Intents?

Run:
```
$ sh tools/running_bdd_reducer.sh
```

This produces output in `accessrefinery_bdd_reducer_10rs/`.

In `summary.txt`, `NumberMCI` represents the number of intents before reduction, and `NumberRRI` represents the number after reduction. Use these values to plot Figure 10 in the paper.

> The real-world dataset in the paper cannot be open-sourced for commercial reasons.


#### Results of AWS AccessAnalyzer via CLI

- `/accessanalyzer_web`: Results of intent mining using AWS AccessAnalyzer via CLI

Note: AWS AccessAnalyzer is accessed remotely, so only correctness experiments can be performed, not performance experiments.

The following instructions can be used to reproduce the results (it is strongly recommended to skip this step, as AWS CLI environment configuration is extremely complicated):
- [Running AWS Access Analyzer via CLI](tools/AccessAnalyzer.md)

#### Results of Comparing AccessRefinery with AWS AccessAnalyzer

- `compare_result`: Results of comparison

The following instructions can be used to reproduce the results:

```bash
$ # JSON library required by the comparison script, the verson of jq is 1.6
$ sudo apt install jq 
$ sh tools/running_batch_compare.sh
```

<!-- Note: The result for `Scalability_05Keys/12_allow_result.json` may differ because AWS Access Analyzer may time out (the result will be marked with `"error": "INTERNAL_ERROR"` by Access Analyzer). This is normal. -->

---

Thank you for reading AccessRefinery!


 
 
REQUIREMENTS 

STATUS

LICENSE Xijiaotong 

INSATLL


<!-- Note: AWS AccessAnalyzer is accessed remotely, so only correctness experiments can be performed.
Performance experiments require a consistent environment, so we have re-implemented a version of Access Analyzer. -->

1. 功能性奖 说明可复现  
2. 可用性奖 代码结构性很好，别人可以复用
3. 公开性奖 代码挂到Zendo上面

注意：
1. 附上作者邮件，解释如何运行和安装
2. MCP解耦，AccessRefinery和MCP都附上小例子，说明如何使用