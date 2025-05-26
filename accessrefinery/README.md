# AccessRefinery: Mining Compact Access Control Intents on Public Cloud.

This repository contains the code for AccessRefinery: a system to automatically mine and reduce intents from cloud Identity and Access Management (IAM) policies.

---

### Setup

```shell
Linux
Java JDK >= 17
Maven >= 3.6.3
```

---

### Run

All dependencies, including the ILP solver, JavaBDD, and MiniSat, are integrated into the project either as source code or via Maven. 
Clone the repository and build the project with Maven:

```shell
$ git clone <your-repo-url>
$ cd accessrefinery
$ mvn clean package
```

To run AccessRefinery, use:

```shell
$ java -jar target/miner-1.0-SNAPSHOT-jar-with-dependencies.jar [options]
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
$ java -jar target/miner-1.0-SNAPSHOT-jar-with-dependencies.jar -m -r --round 10 -f data/Correctness
```

Results are generated in the `results/` directory. The output includes:
- `xxx.json`: The generated intents for each policy.
- `xxx.csv`: Statistics for multi-round SMT solving for each policy.
- `summary.txt`: Summary statistics for all policies in a folder.

---

### Project Structure

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

#### Multi-Theory Constraint Preprocessor (MCP)

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

---

### Reproduction

All experimental results are archived in `/archive_result`. 

#### Results of AccessRefinery

- `accessminer_bdd_mci_10rs`: Results of intent mining for 10 rounds using JavaBDD backend
- `accessminer_sat_mci_10rs`: Results of intent mining for 10 rounds using MiniSAT backend
- `accessminer_bdd_rri_10rs`: Results of intent mining and reduction for 10 rounds using JavaBDD
- `accessminer_sat_rri_3rs`: Results of intent mining and reduction for 3 rounds using MiniSAT

Scripts are provided to generate these results. Note that `accessminer_sat_rri_3rs` runs very slowly.

```bash
$ sudo mvn clean package
$ sh tools/running_bdd_mci.sh
$ sh tools/running_sat_mci.sh
$ sh tools/running_bdd_rri.sh
$ sh tools/running_sat_rri.sh
```

#### Results of AWS AccessAnalyzer via CLI

- `/accessanalyzer_web`: Results of intent mining using AWS AccessAnalyzer via CLI

Note: AWS AccessAnalyzer is accessed remotely, so only correctness experiments can be performed, not performance experiments.

The following instructions can be used to reproduce the results (it is strongly recommended to skip this step, as AWS CLI environment configuration is extremely complicated):
- [Running AWS Access Analyzer via CLI](tools/AccessAnalyzer.md)

#### Results of Comparing AccessRefinery with AWS AccessAnalyzer

- `compare_result`: Results of comparison

The following instructions can be used to reproduce the results:

```bash
$ sudo apt install jq # JSON library required by the comparison script
$ sh tools/running_batch_compare.sh
```

Note: The result for `Scalability_05Keys/12_allow_result.json` may differ because AWS Access Analyzer may time out (the result will be marked with `"error": "INTERNAL_ERROR"` by Access Analyzer). This is normal.

---

Thank you for reading AccessRefinery!