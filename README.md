<!-- 
<img src="images/logo.png?raw=True" align="right" width="20%"/> AccessRefinery: Fast 
<img src="logo2.png?raw=True" align="right" width="20%"/> 
-->

<!-- <div style="display: flex; justify-content: space-between; align-items: center;">
    <h1 style="margin: 0;">AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud</h1>
    <img src="logo.png" width="30%">
</div> -->

# AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud

by [Ning Kang](https://xjtu-netverify.github.io/people/nkang/), [Peng Zhang ](https://xjtu-netverify.github.io/people/pzhang/) and [Jianyuan Zhang](https://xjtu-netverify.github.io/people/jyzhang/) at [ANTS lab](https://xjtu-netverify.github.io/).

![Java](https://img.shields.io/badge/Java-17-007396?logo=java&logoColor=white)
![Tests](https://img.shields.io/badge/tests-passing-brightgreen?logo=java)
![License](https://img.shields.io/badge/license-MIT-green)
![Paper](https://img.shields.io/badge/paper-FSE2026-orange)

> Ning Kang, Peng Zhang, Jianyuan Zhang, Hao Li, Dan Wang, Zhenrong Gu, Weibo Lin, 
> Shibiao Jiang, Zhu He, Xu Du, Longfei Chen, Jun Li, and Xiaohong Guan
> "AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud", ACM FSE 2026

## About AccessRefinery

AccessRefinery automatically mines access control intents from IAM (Identity and Access Management) policies. These intents help users verify the correctness and security of their policies. Compared with [AWS Access Analyzer](https://link.springer.com/content/pdf/10.1007/978-3-030-53288-8_9.pdf) and their [commercial system](https://docs.aws.amazon.com/IAM/latest/UserGuide/access-analyzer-concepts.html), AccessRefinery reduces mining time by 10–100× and eliminates roughly 10× redundant intents.

The key idea behind AccessRefinery for accelerating intent mining is to reduce the redundancy of multi-round SMT solving by preprocessing constraints into bit-vector constraints using our Multi-Theory Constraint Preprocessor (MCP).  
For intent reduction, AccessRefinery computes a compact set that covers all mined intents by solving a min-set-cover problem.

In addition, the MCP module supports the extra feature, fully integrated into the tool Performing multi-round SMT solving, equivalent to standard SMT computations.

For technical details and a full evaluation, refer to our FSE 2026 paper: [*AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud* ](https://xjtu-netverify.github.io/papers/AccessRefinery/accessrefinery_final_version.pdf).

## Install

All code, datasets (except real-world dataset), and results for the paper are contained in this repository.

Note that when browsing on an anonymous website, the page may need to be refreshed after clicking a link.

- [AccessRefinery and AWS Access Analyzer CLI version](accessrefinery/README.md)
- [Access Analyzer reproduction version](accessanalyzer/README.md)
- [Experimental Figures](experiment-figures/README.md)


## Project Structure

**To simplify the experimental setup, MCP and AccessRefinery have been integrated. The two modules remain fully decoupled, and a standalone repository containing only MCP is also provided. You can still follow the instructions below to run MCP.**

The `mcp` module is Multi-Theory Constraint Preprocessor (MCP) that supports fast multi-round SMT solving. 
The `refinery` module implements intent mining and reduction based on MCP.

```text
accessrefinery
|
|----data
|       |---Correctness       # input datasets
|       |   |---11_allow_allow_equal.json
|       |   |---...
|       |---Scalability_05Keys
|       |---Scalability_06Keys
|----projects
|       |---bdd               # binary decision diagram module
|       |---mcp               # Multi-Theory Constraint Preprocessor
|       |---refinery          # mining and reducing intents module
|----tools
|       |---...               # scripts for running experiments
|----pom.xml                  # Maven root configuration
```

## Using Multi-Theory Constraint Preprocessor (MCP)


MCP supports multi-round SMT solving. Each domain supports regular expressions, IP prefixes/bit-vectors, ranges, and sets.
An example of using MCP is shown below. This example is also included in the test directory [MCPFactoryTest.java](projects/mcp/src/test/java/org/iam/core/MCPFactoryTest.java) and will be executed automatically during `mvn package`.

```java
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

if(policy.and(intent6.not()).isZero()) {
    // ... main logic for empty result
} else {
    // ... main logic for non-empty result
}
```


## Using AccessRefinery

All dependencies, including the ILP solver, JavaBDD, and MiniSat, are integrated into the project either as source code or via Maven. 
Clone the repository and build the project with Maven:

<!-- ```shell
$ git clone <your-repo-url> 
$ cd accessrefinery
$ mvn clean package
``` -->

```shell
$ cd accessrefinery
$ mvn clean package
```

To run AccessRefinery, use:

```shell
$ java -jar target/refinery-1.0-SNAPSHOT-jar-with-dependencies.jar [options]
```

**Command-line options:**
- `-h, --help` : Show help information.
- `-m, --mine` : Enable mining mode (extract intents).
- `-r, --reduce` : Enable reduction of intents.
- `-f, --file <path>` : Input path for policy files (must be under `data/`).
- `-s, --sat` : Use SAT as the solving core (default is BDD).
- `--round <number>` : Number of mining rounds (to reduce experimental bias).
- `--merge` : The optimization of merging the output format of intents.

**Example:**
```shell
$ java -jar target/refinery-1.0-SNAPSHOT-jar-with-dependencies.jar -m -r --round 10 -f data/Correctness
```

Results are generated in the `results/` directory. The output includes:
- `xxx.json`: The generated intents for each policy.
- `xxx.csv`: Statistics for multi-round SMT solving for each policy.
- `summary.txt`: Summary statistics for all policies in a folder.

## Evaluation

<!-- Note: AWS AccessAnalyzer is accessed remotely, so only correctness experiments can be performed.
Performance experiments require a consistent environment, so we have re-implemented a version of Access Analyzer. -->

1. 功能性奖 说明可复现  
2. 可用性奖 代码结构性很好，别人可以复用
3. 公开性奖 代码挂到Zendo上面

注意：
1. 附上作者邮件，解释如何运行和安装
2. MCP解耦，AccessRefinery和MCP都附上小例子，说明如何使用
 
 
REQUIREMENTS 

STATUS

LICENSE Xijiaotong 

INSATLL
