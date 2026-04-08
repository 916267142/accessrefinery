## STATUS

Our artifact is consistent with the artifacts of interest:

- **Software:** The artifact provides AccessRefinery, the implementation presented in our paper, together with our re-implementation of AWS Access Analyzer, both of which support follow-up studies.
- **Frameworks:** The artifact introduces Multi-round Constraints Preprocessor (MCP), a data structure that provides basic operations for accelerating multi-round SMT solving and can potentially be applied in other software engineering studies. For example, ACL analysis for misconfigurations or redundancies also involves multi-round solving (Fireman, S&P 2006) and can potentially benefit from MCP.
- **Datasets and Reproduction Assets:** The artifact provides open-source datasets synthesized from realistic data for both scalability and correctness evaluation, together with open-source experiment workflows (execution scripts and plotting scripts) that provide a reusable reference for subsequent research.

Thus, we apply for the following ACM Artifact Evaluation badges:

### 1) Evaluated - Functional

We believe this artifact satisfies the Functional criteria: documented, consistent, complete, exercisable, and supported by appropriate evidence of verification and validation.

- **Documented:** The artifact provides clear instructions for project structure, environment setup (requirements and installation), and execution.
- **Consistent:** The artifact content is consistent with the paper claims: it includes MCP, AccessRefinery, our re-implementation of AWS Access Analyzer, and CLI invocation scripts for AWS Access Analyzer (with non-open-source parts explicitly noted where applicable).
- **Complete:** The artifact includes all core components needed to reproduce the paper claims: the AccessRefinery implementation, baseline implementations (including our re-implementation of AWS Access Analyzer), datasets, experiment scripts, and figure-generation scripts.
- **Exercisable:** Users can run standard Maven workflows for building, testing, execution, and experiment reproduction using the provided commands and scripts.
- **Verification and Validation Evidence:** The artifact provides archived experimental results as concrete evidence for reproducibility checks.

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
