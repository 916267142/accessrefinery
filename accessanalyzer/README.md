# AccessAnalyzer: Stratified Intent Mining Tool
A re-implemented version of AWS AccessAnalyzer for automatically discovering and organizing stratified intent structures from IAM policies.

## Setup

### Prerequisites
- Java JDK >= 17
- Maven 3.9.9

### Clone the repository
```bash
$ git clone https://anonymous.4open.science/r/ase25-5671/
```

### Install Z3 and add to PATH
```bash
$ cd accessanalyzer
$ unzip z3-4.15.0-x64-glibc-2.39.zip -d ~/z3-4.15.0
$ echo 'export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:~/z3-4.15.0/bin' >> ~/.bashrc
$ source ~/.bashrc
```

### Build the project
```bash
$ mvn clean package
```

## Run

### Command-line options:
- `-r, --reduce`: Reduce the number of intents.
- `-s, --solver <Z3/CVC5>`: Select the solver to use (Z3 or CVC5), default is Z3.
- `-f, --file <DATA_PATH>`: Path to the input policies (JSON file or folder).
- `-h, --help`: Show help message and exit.

Example:
```bash
$ java -jar target/accessanalyzer-1.0-SNAPSHOT-release.jar -r -s Z3 -f data/POLICIES
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
|----src                      # mining tool source code
|----tools
|       |---...               # scripts for running experiments
|----pom.xml                  # Maven root configuration
|----assembly.xml             # Maven assembly configuration
```

## Reproduction
All experimental results are archived in `/archive_result`.

### Results of AccessAnalyzer

Still waiting for write up.