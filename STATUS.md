## STATUS

We believe our artifact is consistent with the categories of artifacts of interest:

- **Software:** We provide the implementation presented in our paper, AccessRefinery, together with our re-implementation of AWS Access Analyzer, which allows follow-up studies.
- **Frameworks:** We design Multi-round Constraints Preprocessor (MCP) as a data structure (Java JAR package) that accelerates multi-round SMT solving. It can potentially be applied in other software engineering systems.
- **Datasets:** We provide two open-source datasets synthesized from realistic data for scalability and correctness evaluation, respectively.

Thus, we apply for the following ACM Artifact Evaluation badges:

### 1) Evaluated - Functional

We believe this artifact satisfies the Functional criteria: documented, consistent, complete, exercisable, and supported by appropriate evidence of verification and validation.

- **Documented:** The artifact provides clear instructions for *project structure*, *environment setup* (requirements and installation), and *execution*.
- **Consistent:** The artifact is consistent with the paper claims and includes *MCP*, *AccessRefinery*, our *re-implementation of AWS Access Analyzer*, and *scripts for invoking AWS Access Analyzer via CLI API*.
> Note that: The baseline approach, AWS Access Analyzer, is not open-sourced.
- **Complete:** The artifact includes all materials needed to reproduce the experimental results, including the *AccessRefinery implementation*, *baseline implementations*, *datasets*, and *experiment and plotting scripts*.
- **Exercisable:** The artifact provides instructions that allow users to run standard Maven workflows to execute our system and reproduce the experimental results.
- **Verification and Validation Evidence:** The artifact provides *archived results* for reproducibility checks.

### 2) Evaluated - Reusable

We believe this artifact satisfies the Reusable criteria (Functional plus high-quality engineering and documentation for repurposing) for the following reasons:

- The codebase is modular and well-structured, with clear separation among the BDD backend, MCP preprocessing, and intent mining/reduction logic.
- MCP is engineered as an independently reusable module and can be integrated into other projects via a packaged JAR and Maven dependency.
- AccessRefinery is easy to deploy for intent mining tasks.
- The repository includes concrete MCP usage examples and command-level integration and execution instructions.
- Reproduction scripts, organized datasets, archived results, and figure-generation tooling support independent extension and comparative studies.
- Baseline implementations and comparison scripts are included, helping researchers bootstrap follow-up research with minimal setup overhead.

### 3) Available

We provide the artifact through two complementary channels to ensure both archival stability and long-term maintainability:

- Stable archival release (for evaluation and citation): [Zenodo]() snapshot with a version-specific DOI.
- Maintained development release (for ongoing updates): public [GitHub repository]().
