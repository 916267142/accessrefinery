
# STATUS

We believe our artifact is consistent with the categories of artifacts of interest:

- **Software:** We provide the implementation presented in our paper, AccessRefinery, together with our re-implementation of AWS Access Analyzer, which allows follow-up studies.
- **Frameworks:** We design Multi-round Constraints Preprocessor (MCP) as a data structure (Java JAR package) that accelerates multi-round SMT solving. It can potentially be applied in other software engineering systems.
- **Datasets:** We provide two open-source datasets synthesized from realistic data for scalability and correctness evaluation, respectively.

Thus, we apply for the following ACM Artifact Evaluation badges:

## Evaluated - Functional

We believe this artifact satisfies the Functional criteria (*The artifacts associated with the research are found to be documented, consistent, complete, exercisable, and include appropriate evidence of verification and validation.*), for the following reasons:

> Note: The baseline approach, AWS Access Analyzer, is not open-source.

- **Documented:** The artifact provides clear instructions for *project structure*, *environment setup* (requirements and installation), and *execution*.
- **Consistent:** The artifact is consistent with the paper claims and includes *MCP*, *AccessRefinery*, our *re-implementation of AWS Access Analyzer*, and *scripts for invoking AWS Access Analyzer via CLI API*.
- **Complete:** The artifact includes all materials needed to reproduce the experimental results, including the *AccessRefinery implementation*, *baseline implementations*, *datasets*, and *experiment and plotting scripts*.
- **Exercisable:** The artifact provides instructions that allow users to run standard Maven workflows to execute our system and reproduce the experimental results.
- **Verification and Validation Evidence:** The artifact provides *archived results* for reproducibility checks.

## Evaluated - Reusable

We believe this artifact satisfies the Reusable criteria (*They are very carefully documented and well-structured to the extent that reuse and repurposing is facilitated*), for the following reasons:

- **Well-structured**:
  - MCP is designed as an independent Maven module. After compilation, it produces `mcp-1.0.jar`, which can be easily reused by other projects.
  - MCP adopts a generic architecture. For multiple variable types, including regex, bit-vector/prefix, range, and set, MCP maps them into generic representations and applies a unified preprocessing workflow (e.g., equivalence-class partitioning and bit-vector encoding). This design makes MCP easily extensible to other variable types.
  - The repository is organized into separate Maven modules, specifically BDD (used by MCP for bit-vector representation), MCP, AccessRefinery (MCP-based intent mining), and Access Analyzer (baseline). Running `mvn package` at the repository root automatically generates the corresponding JAR files: `mcp-1.0.jar` (for reuse in other projects), `accessrefinery-1.0.jar` (intent mining), and `accessanalyzer-1.0.jar` (baseline for intent mining).

- **Carefully documented**:
  - MCP, AccessRefinery, and Access Analyzer are documented with usage explanations and concrete examples.
  - AccessRefinery and Access Analyzer provide scripts that automatically generate organized experimental results.
  - Experimental logs are systematically archived.
  - The artifact explains how experimental results correspond to the figures reported in the paper.
  - Automated plotting scripts are provided.
  - Auto-generated Maven documentation explains MCP functions.
  
## Available

We believe this artifact satisfies the Available criteria (*A DOI or link to this repository, along with a unique identifier for the object, is provided*).

We make the artifact publicly available through the following channels:

- Archival release: [Zenodo]() snapshot with DOI 1111:1111.
- Development release: public [GitHub repository]().
