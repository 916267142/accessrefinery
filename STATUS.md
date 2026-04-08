# STATUS

Our artifact aligns with the focus of the FSE 2026 Artifacts Track, as follows:

- **Software:** We provide the implementation of AccessRefinery and our reimplementation of AWS Access Analyzer (baseline), which enables follow-up studies.
- **Frameworks:** We design the Multi-round Constraints Preprocessor (MCP) as a data structure, which accelerates multi-round SMT solving. Moreover, we design MCP as a Java JAR package, which allows easy reuse.
- **Datasets:** We provide three open-source datasets synthesized from real-world data.

We apply for the following FSE 2026 Artifact Evaluation badges:

## Evaluated - Functional

We believe this artifact satisfies the Functional criteria (*The artifacts associated with the research are found to be documented, consistent, complete, exercisable, and include appropriate evidence of verification and validation.*), for the following reasons:

- **Documented:** The artifact provides details on:
  - Requirements
  - Installation
  - Project structure
  - Usage and examples
  - Reproduction scripts and instructions
  - Plotting scripts
- **Exercisable:** Users can run standard Maven workflows (Java JDK 17) to build and execute the system from source.
- **Complete:** The artifact includes all required materials to reproduce the reported experiments:
  - Source code for AccessRefinery
  - Source code for our reimplementation of AWS Access Analyzer
  - Scripts for invoking AWS Access Analyzer via the CLI API (The baseline approach, AWS Access Analyzer, is not open-source and provides only a CLI API.)
  - Three synthetic datasets
  - Reproduction scripts and instructions
- **Consistency and Evidence:** The released artifact aligns with the paper's evaluation conclusions. We provide archived results and instructions on how to map the results to the conclusions in the paper.

## Evaluated - Reusable

We believe this artifact satisfies the Reusable criteria (*They are very carefully documented and well-structured to the extent that reuse and repurposing is facilitated*), for the following reasons:

- **Reuse and repurposing of MCP:** To make MCP easy to use and extend, we provide the following design:
  - **Modular Design:** MCP is designed as an independent Maven module. Compiling the project will produce `mcp-1.0.jar`, which can be directly imported by other Java projects.
  - **Easy-to-use Java API and Example:** MCP supports Java-style chained Boolean operations, for example, `policy.not().and(intent1.or(intent2))`. Moreover, we provide examples of how to use MCP.
  - **General Design:** MCP uses a general design for multiple variable types, including regex (RegexpLabel.java), prefix (PrefixLabel.java), range (RangeLabel.java), and set (IntegerSetLabel.java). These types are mapped to a unified superclass (Label.java) based on language features such as polymorphism in Java. MCP then performs processing (e.g., equivalence-class partitioning and bit-vector encoding) on the unified superclass. This general design makes MCP easy to extend to other variable types.

- **Reuse and repurposing of AccessRefinery and Access Analyzer:** To facilitate intent mining for cloud users and comparison experiments for developers, we provide the following design:
  - **Easy-to-use Command-Line API and Example:** Running `mvn package` at the repository root generates `accessrefinery-1.0.jar` and `accessanalyzer-1.0.jar`. For each JAR package, we provide command-line parsing support, so developers do not need to deal with internal implementation details when conducting comparison experiments.
  For example, `$ java -jar target/refinery-1.0.jar -m -r --sat --round 10 -f data/Correctness` will automatically run 10 rounds to compute the average time, and `--sat` indicates that MiniSAT is used instead of BDD to represent bit-vectors.
  Moreover, we provide examples of how to use AccessRefinery and Access Analyzer.
  - **Standardized Comments:** We provide standardized comments for MCP and AccessRefinery, enabling automatic Maven documentation generation and helping users understand the internal system structure for further development.

## Available

We believe this artifact satisfies the Available criteria (*A DOI or link to this repository, along with a unique identifier for the object, is provided*) for the following reasons:

The artifact is publicly available in two ways:

- Archival release: a [Zenodo]() snapshot version with DOI 1111:1111.
- Development release: a [GitHub]() repository version.
