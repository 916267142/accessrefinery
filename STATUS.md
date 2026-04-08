
# STATUS

We believe our artifact is consistent with the categories of artifacts of interest:

- **Software:** We provide the implementation presented in our paper, AccessRefinery, together with our re-implementation of AWS Access Analyzer, which allows follow-up studies.
- **Frameworks:** We design Multi-round Constraints Preprocessor (MCP) as a data structure (Java JAR package) that accelerates multi-round SMT solving. It can potentially be applied in other software engineering systems.
- **Datasets:** We provide two open-source datasets synthesized from realistic data for scalability and correctness evaluation, respectively.

Thus, we apply for the following ACM Artifact Evaluation badges:

## Evaluated - Functional

We believe this artifact satisfies the Functional criteria (documented, consistent, complete, exercisable, and supported by appropriate evidence of verification and validation), for the following reason:

> Note that: The baseline approach, AWS Access Analyzer, is not open-sourced.

- **Documented:** The artifact provides clear instructions for *project structure*, *environment setup* (requirements and installation), and *execution*.
- **Consistent:** The artifact is consistent with the paper claims and includes *MCP*, *AccessRefinery*, our *re-implementation of AWS Access Analyzer*, and *scripts for invoking AWS Access Analyzer via CLI API*.
- **Complete:** The artifact includes all materials needed to reproduce the experimental results, including the *AccessRefinery implementation*, *baseline implementations*, *datasets*, and *experiment and plotting scripts*.
- **Exercisable:** The artifact provides instructions that allow users to run standard Maven workflows to execute our system and reproduce the experimental results.
- **Verification and Validation Evidence:** The artifact provides *archived results* for reproducibility checks.

## Evaluated - Reusable

We believe this artifact satisfies the Reusable criteria (Functional plus high-quality engineering and documentation for repurposing), for the following reasons:

- **MCP Design**: 
  - MCP is engineered as an independently reusable module and can be integrated into other projects via a packaged JAR and Maven dependency.
  - MCP is implemented with a generic architecture and currently supports multiple constraint types, including regex, bit-vector/prefix, range, and set. Each type is defined through a dedicated interface and can be automatically assembled into the MCP pipeline.
  - MCP provides standard Maven-based documentation.

- **Project Design**: 
  - The codebase is modular and well-structured, with clear separation among the BDD backend, MCP preprocessing, and intent mining/reduction logic.
  - The project provides a unified build-and-run workflow. Building from the repository root automatically generates `mcp-1.0.jar`, `accessrefinery-1.0.jar`, and `accessanalyzer-1.0.jar`.
  - Both AccessRefinery and Access Analyzer expose standard command-line interfaces.
  - Both AccessRefinery and Access Analyzer include scripts for automatically collecting experimental results.

- **Document**: 
  - The repository includes concrete MCP usage examples and command-level integration and execution instructions.
  - Reproduction scripts, organized datasets, archived results, and figure-generation tooling support independent extension and comparative studies.
  
## Available

We make the artifact publicly available through the following channels:

- Archival release: [Zenodo]() snapshot with DOI 1111:1111.
- Development release: public [GitHub repository]().
