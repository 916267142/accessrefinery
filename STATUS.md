
# STATUS

We believe our artifact is consistent with the categories of artifacts of interest:

- **Software:** We provide the implementation presented in our paper, AccessRefinery, together with our re-implementation of AWS Access Analyzer, which allows follow-up studies.
- **Frameworks:** We design Multi-round Constraints Preprocessor (MCP) as a data structure (Java JAR package) that accelerates multi-round SMT solving. It can potentially be applied in other software engineering systems.
- **Datasets:** We provide two open-source datasets synthesized from realistic data for scalability and correctness evaluation, respectively.

Thus, we apply for the following ACM Artifact Evaluation badges:

## Evaluated - Functional

We believe this artifact satisfies the Functional criteria (*The artifacts associated with the research are found to be documented, consistent, complete, exercisable, and include appropriate evidence of verification and validation.*), for the following reasons:

> Note: The baseline approach, AWS Access Analyzer, is not open-source.

- **Documented:** The repository provides concrete instructions for project structure, environment setup (requirements and installation), and command-level execution.
- **Exercisable:** Users can run standard Maven workflows to build and execute the system from source.
- **Complete:** The artifact includes all required components to reproduce the reported experiments, including AccessRefinery, baseline implementations, datasets, and experiment/plotting scripts.
- **Consistent:** The released artifact aligns with the paper's claims and includes MCP, AccessRefinery, our re-implementation of AWS Access Analyzer, and scripts for invoking AWS Access Analyzer through the CLI API.
- **Verification and Validation Evidence:** Archived experiment outputs are provided to support reproducibility checks.

## Evaluated - Reusable

In addition to being functional, we believe this artifact satisfies the Reusable criteria (*They are very carefully documented and well-structured to the extent that reuse and repurposing is facilitated*), for the following reasons:

- **Modular architecture and clear interfaces:**
  - MCP is packaged as an independent Maven module; compiling the project produces `mcp-1.0.jar`, which can be directly integrated into other Java projects.
  - MCP uses a generic design with unified preprocessing (e.g., equivalence-class partitioning and bit-vector encoding) across multiple variable types, including regex, bit-vector/prefix, range, and set.
  - This abstraction-based design makes MCP straightforward to extend to additional variable types.

- **Reusable project organization and build outputs:**
  - The repository is split into clear Maven modules (BDD, MCP, AccessRefinery, and Access Analyzer), enabling targeted reuse of components.
  - Running `mvn package` at the repository root generates reusable deliverables (`mcp-1.0.jar`) and executable artifacts (`accessrefinery-1.0.jar`, `accessanalyzer-1.0.jar`).

- **Documentation and automation for extension and adoption:**
  - MCP, AccessRefinery, and Access Analyzer include usage-oriented documentation with concrete examples.
  - Auto-generated Maven documentation is provided for MCP APIs.
  - Automation scripts generate organized experiment outputs, and archived logs are mapped to paper figures, which lowers the cost of extension, comparison, and follow-up studies.
  
## Available

We believe this artifact satisfies the Available criteria (*A DOI or link to this repository, along with a unique identifier for the object, is provided*).

We make the artifact publicly available through the following channels:

- Archival release: [Zenodo]() snapshot with DOI 1111:1111.
- Development release: public [GitHub repository]().
