
# Status

We are claiming 3 badges (Functional, Reusable, and Available) as the following:.

## Evaluated - Functional

We believe this artifact satisfies the Functional criteria based on the following evidence:

- **Documented:** The artifact is well-documented and includes:
  - System requirements
  - Installation instructions
  - Project structure overview
  - Usage examples
  - Reproduction scripts and step-by-step instructions
  - API documentation generated via Javadoc
  - Developer instructions (develop AccessRefinery  in VS Code)

- **Exercisable:** The system can be built and executed from source using standard Maven workflows with JDK 17.

- **Complete:** The artifact includes all necessary components to reproduce the experimental results reported in the paper:
  - Source code of *AccessRefinery*
  - Source code of the reimplemented *Access Analyzer* (used as a baseline, since *AWS Access Analyzer* is not open source and only exposes a CLI interface)
  - Scripts for invoking *AWS Access Analyzer* via CLI
  - Three synthetic datasets (real-world datasets are not publicly available due to commercial restrictions)
  - Reproduction and plotting scripts

- **Consistent with the paper:** The artifact includes archived experimental results and provides instructions to reproduce all key claims reported in the paper.

## Evaluated - Reusable

We believe this artifact satisfies the Reusable criteria for the following reasons:

- **Modular and extensible design:**
  AccessRefinery is designed with clear modular boundaries. In particular, it separates the low-level data structure (*MCP*) from the higher-level analysis tool. This separation enables users to reuse or extend individual components independently.

- **Reusable library (MCP):**
*MCP* is implemented as a standalone Java library. Running `mvn package` generates a reusable JAR file (`mcp-1.0.jar`) that can be directly integrated into other projects.
  - It provides a concise and expressive API (e.g., `policy.not().and(intent1.or(intent2))`).
  - It supports multiple variable types (e.g., regex, prefix, range, and set) through a unified abstraction, making it straightforward to extend to new types.

- **Reusable tool (AccessRefinery):**
  AccessRefinery provides a command-line interface with flexible options for different analysis tasks.
  - Users can easily run experiments or adapt workflows via configurable parameters.
  - The tool supports interchangeable backends (using BDD or SAT solver to represent bitvector constraints), enabling support for additional backends.
  
- **Reusable baseline implementation (Access Analyzer):**
  We provide a reimplementation of *Access Analyzer* as a baseline system for evaluation, since *AWS Access Analyzer* is not open source and only exposes a CLI interface.
  - The reimplementation enables  comparison with *AccessRefinery* under a unified experimental framework.
  - The tool supports interchangeable backends (e.g., Z3 and CVC5 solvers), enabling support for additional backends.

- **Ease of extension:**
  The system is designed to facilitate incremental extensions. For example, adding support for new policy languages only requires extending specific components without modifying the entire system.

- **Documentation and examples:**
  The artifact includes API documentation (via Javadoc) and usage examples..

## Available

We believe this artifact satisfies the Available criteria because it is publicly available on Zenodo and GitHub.
