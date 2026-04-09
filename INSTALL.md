# Installation

<!-- **AccessRefinery** is an intent-mining tool for IAM policies. In our experiments, we compare it with **AWS Access Analyzer**. -->

This guide describes how to set up the experimental environment for **MCP**， **AccessRefinery**, and our reimplemented **Access Analyzer** baseline.

## Environment Setup

- Prepare a Linux system (recommended: Ubuntu 22.04.5):

ubuntu-22.04.5-desktop-amd64.iso
<https://releases.ubuntu.com/jammy/ubuntu-22.04.5-desktop-amd64.iso>

- Install JDK 17:

```bash
sudo apt install openjdk-17-jdk
```

Add Java to the environment variables (recommended):

```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

javac -version
java -version
```

Expected output:

```shell
javac 17.0.17

openjdk version "17.0.17" 2025-10-21
OpenJDK Runtime Environment (build 17.0.17+10-Ubuntu-122.04)
OpenJDK 64-Bit Server VM (build 17.0.17+10-Ubuntu-122.04, mixed mode, sharing)
```

- Install Maven:

```bash
sudo apt install maven
mvn -v
```

Expected output:

```shell
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 17.0.17, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "6.8.0-90-generic", arch: "amd64", family: "unix"
```

- Install `jq` for JSON processing:

```bash
sudo apt install jq
jq --version
```

Expected output:

```shell
jq-1.6
```

- Install `Z3`:

`Z3` is already precompiled. Run the following script to automatically copy the `Z3` executable and libraries to the required directories.

```bash
sh tools/install_z3.sh
```

Expected output:

```shell
Copied Z3 files to:
- /usr/lib
- /usr/bin
- /home/nkang/.local/bin
Added LD_LIBRARY_PATH to ~/.bashrc
Z3 version 4.14.1 - 64 bit
Z3 installation is correct.
```

> Note: `CVC5` is installed automatically during project compilation.

<!-- ## Compile **AccessRefinery** and **Access Analyzer** -->
