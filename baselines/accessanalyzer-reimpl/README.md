# AccessAnalyzer: Stratified Intent Mining Tool
A reimplemented version of AWS AccessAnalyzer for automatically discovering and organizing stratified intent structures from IAM policies.

## Setup

### Prerequisites
- Ubuntu 22.04
- Java JDK >= 17
- Maven 3.6.3
- jq 1.6
- bc 1.07.1
- Z3 4.14.1
- CVC5 1.2.1

### Install Z3 and add to PATH
```bash
$ cd accessanalyzer
$ echo 'export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:'$(pwd)'/lib/z3-4.14.1/bin' >> ~/.bashrc
$ source ~/.bashrc
```

### Build the project
```bash
$ mvn clean package
```

## Run

```bash
$ java -jar target/accessanalyzer-1.0.jar <options>
```

### Command-line options:
- `-r, --reduce`: Reduce the number of intents.
- `-s, --solver <Z3/CVC5>`: Select the solver to use (Z3 or CVC5), default is Z3.
- `-f, --file <DATA_PATH>`: Path to the input policies (JSON file or folder).
- `-h, --help`: Show help message and exit.

Example:
```bash
$ java -jar target/accessanalyzer-1.0.jar -r -s Z3 -f data/Correctness/
``` 

Then the result will be output to the `results/` folder.

For the file `<file_name>.json`, the tool will output a folder named `results/<file_name>.json/`, containing the following files:
- `<file_name>_<solver>_findings.json`: The mined intents in JSON format.
- `<file_name>_<solver>_time.csv`: The time spent in mining the intents.

## Project Structure
```bash
accessanalyzer
|
|----lib                      # third-party libraries
|----data
|       |---Correctness       # input datasets
|       |   |---11_allow_allow_equal.json
|       |   |---...
|       |---Scalability_05Keys
|       |---Scalability_06Keys
|----src                      # mining tool source code
|----tools
|       |---...               # scripts for running experiments
|----pom.xml                  # Maven root configuration
|----assembly.xml             # Maven assembly configuration
```

## Reproduction
All experimental results are archived in `/archive_result`.

### Result of AccessAnalyzer

- `accessanalyzer_cvc5_unredueced'`: Results of intent mining using CVC5
- `accessanalyzer_cvc5_redueced'`: Results of intent mining and reduction using CVC5
- `accessanalyzer_z3_unredueced'`: Results of intent mining using Z3
- `accessanalyzer_z3_redueced'`: Results of intent mining and reduction using Z3

Scripts are provided to generate these results.

#### Generate preliminary results

**Note**: This project will process both datasets in a folder. A 1-hour timeout is set.

```bash
$ bash tools/mining_unreduced.sh
$ bash tools/mining_reduced.sh
```
The two scripts above will take 10 to 20 hours to run. Alternatively, the result can be acquired from the archived data using the following command.

```bash
$ cp -r ./archived_result/reduced_result ./reduced_result
$ cp -r ./archived_result/unreduced_result ./unreduced_result
```

#### Organize and categorize the results

```bash
$ sudo apt install jq
$ bash tools/organize_results.sh
```

### Results of AccessRefinery

- `accessrefinery_bdd_miner_10rs`: Results of intent mining for 10 rounds using JavaBDD backend
- `accessrefinery_bdd_reducer_10rs`: Results of intent mining and reduction for 10 rounds using JavaBDD

These results are copied from `accessrefinery/archived_results/` for comparison.

### Results of Comparing AccessRefinery with AWS AccessAnalyzer

- `compare_result`: Results of comparison

The following instructions can be used to reproduce the results:

```bash
$ bash tools/running_batch_compare.sh
```

---

Thank you for reading AccessAnalyzer! 
