# AccessAnalyzer: Stratified Abstraction of Access Control Policies
A re-implemented version of AWS AccessAnalyzer. https://assets.amazon.science/0f/23/212ac40144f193bb30fb19bd77af/stratified-abstraction-of-access-control-policies.pdf

## Setup

### Prerequisites
- Linux Ubuntu 22.04 LTS 
- Java JDK >= 17
- Maven >= 3.6.3
- Shell jq = 1.6
  Shell bc = 1.07.1

### Install Z3 and add to PATH
```bash
$ cd accessanalyzer
$ echo 'export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:'$(pwd)'/z3-4.15.0/bin' >> ~/.bashrc
$ source ~/.bashrc
```

### Build the project
```bash
$ mvn clean package
```

## Run

```bash
$ java -jar target/accessanalyzer-1.0-SNAPSHOT-release.jar <options>
```

### Command-line options:
- `-r, --reduce`: Reduce the number of intents.
- `-s, --solver <Z3/CVC5>`: Select the solver to use (Z3 or CVC5), default is Z3.
- `-f, --file <DATA_PATH>`: Path to the input policies (JSON file or folder).
- `-h, --help`: Show help message and exit.

Example:
```bash
$ java -jar target/accessanalyzer-1.0-SNAPSHOT-release.jar -r -s Z3 -f data/Correctness/
``` 

Then the result will be output to the `results/` folder.

For the file `<file_name>.json`, the tool will output a folder named `results/<file_name>.json/`, containing the following files:
- `<file_name>_<solver>_findings.json`: The mined intents in JSON format.
- `<file_name>_<solver>_time.csv`: The time spent in mining the intents.

## Project Structure
```
accessanalyzer
|
|----lib                      # third-party libraries
|----data
|       |---Correctness       # input datasets
|       |   |---11_allow_allow_equal.json
|       |   |---...
|       |---Scalability_05Keys
|       |---Scalability_06Keys
|----src                      # the source code accessanalyzer
|----tools
|       |---...               # scripts for running experiments
|----pom.xml                  # maven root configuration
|----assembly.xml             # maven assembly configuration
```

## Reproduction
All experimental results are archived in `/archive_result`.

### Result of AccessAnalyzer

- `accessanalyzer_cvc5_unredueced'`: Results of intent mining using CVC5
- `accessanalyzer_cvc5_redueced'`: Results of intent mining and reduction using CVC5
- `accessanalyzer_z3_unredueced'`: Results of intent mining using Z3
- `accessanalyzer_z3_redueced'`: Results of intent mining and reduction using Z3

The following commands processes all datasets under the data directory in batch mode, with the timeout set to one hour.
```bash
$ sh tools/mining_unreduced.sh
$ sh tools/mining_reduced.sh
```
The following script moves the results to the specific directory.
```bash
$ sh tools/organize_result.sh
```

### Results of AccessRefinery

- `accessrefinery_bdd_miner_10rs`: Results of intent mining for 10 rounds using JavaBDD backend
- `accessrefinery_bdd_reducer_10rs`: Results of intent mining and reduction for 10 rounds using JavaBDD

These results are copied from `accessrefinery/archived_results/` for comparison.

### Results of Comparing AccessRefinery with AWS AccessAnalyzer

- `compare_result`: Results of comparison

The following commands can be used to reproduce the results:

```bash
$ sudo apt install jq # JSON library required by the comparison script
$ sh tools/running_batch_compare.sh
```

---

Thank you for reading AccessAnalyzer! 
