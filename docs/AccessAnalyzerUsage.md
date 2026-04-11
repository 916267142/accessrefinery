# Using Reimplemented Access Analyzer (Baseline)

#### Access Analyzer API

To run *Access Analyzer*, use:

```bash
java -jar target/accessanalyzer-1.0.jar <options>
```

Command-line options:

- `-r, --reduce`: Reduce the number of intents.
- `-s, --solver <Z3/CVC5>`: Select the solver to use (Z3 or CVC5), default is Z3.
- `-f, --file <DATA_PATH>`: Path to the input policies (JSON file or folder).
- `-h, --help`: Show help message and exit.

#### Example

```bash
java -jar target/accessanalyzer-1.0.jar -r -s Z3 -f data/TestCLI/01_allow.json
```

Expected output:

```text
[INFO] 2026-04-10 23:08:41 : ----------[ Shaky Jenga Tower Code ]-------------
[INFO] 2026-04-10 23:08:41 : logger path: /home/simple/workspace/accessrefinery-workspace/accessrefinery/miner.log
[INFO] 2026-04-10 23:08:41 : input  path: data/Correctness/11_allow_allow_equal.json
[INFO] 2026-04-10 23:08:41 : output path: result/Correctness/11_allow_allow_equal.json
[INFO] 2026-04-10 23:08:41 : ----------< Processing policy - 11_allow_allow_equal.json >-----------
[INFO] 2026-04-10 23:08:41 : [1/5]  finish parser policy
[INFO] 2026-04-10 23:08:41 : [2/5]  finish label tree calculation
[INFO] 2026-04-10 23:08:41 : [3/5]  finish findings mining : 1
[INFO] 2026-04-10 23:08:41 : [4/5]  finish atomic predicates calculation
```

For the file `<file_name>.json`, the tool will output a folder named `results/<file_name>.json/`, containing the following files:

- `<file_name>_<solver>_findings.json`: The mined intents in JSON format.
- `<file_name>_<solver>_time.csv`: The time spent in mining the intents.