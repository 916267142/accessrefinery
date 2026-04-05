# Installation Overview

**AccessRefinery** is a tool for mining IAM intents. In our experiments, we compare it with **AWS Access Analyzer**.

Since AWS Access Analyzer is not open-source and provides only a CLI, we also implemented a **reproduced version of Access Analyzer** for experimental comparison. We distinguish the two versions as follows:
- **Reproduced Access Analyzer** - our full reimplementation of Access Analyzer
- **AWS CLI Access Analyzer** – the original tool accessed via its remote CLI interface

> **Note:** Installing the [Reproduced Access Analyzer](#install-and-compile-reproduced-access-analyzer) and [AWS CLI Access Analyzer](#install-and-compile-aws-access-analyzer) is **optional**.
> All experimental results have already been archived, so these steps can be skipped if you only want to use AccessRefinery.

## Install Experimental Environment

1. Prepare a Linux system (recommended: Ubuntu 22.04.5):

ubuntu-22.04.5-desktop-amd64.iso  
https://releases.ubuntu.com/jammy/ubuntu-22.04.5-desktop-amd64.iso

2. Install Maven:

```bash
sudo apt install maven
```

Verify Maven:

```bash
mvn -v
```

Expected output (example):

```text
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 11.0.29, vendor: Ubuntu, runtime: /usr/lib/jvm/java-11-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "6.8.0-90-generic", arch: "amd64", family: "unix"
```

3. Install JDK 17:

```bash
sudo apt install openjdk-17-jdk
```

Add Java to environment variables (recommended):

```bash
vi ~/.bashrc

# Add the following lines into ~/.bashrc
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Apply changes
source ~/.bashrc
```

Verify the Java compiler:

```bash
javac -version
```

Expected output:

```text
javac 17.0.17
```

Verify Java runtime:

```bash
java -version
```

Expected output:

```text
openjdk version "17.0.17" 2025-10-21
OpenJDK Runtime Environment (build 17.0.17+10-Ubuntu-122.04)
OpenJDK 64-Bit Server VM (build 17.0.17+10-Ubuntu-122.04, mixed mode, sharing)
```

Then run:

```bash
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

4. Install `jq` for JSON processing and correctness-result comparison:

```shell
sudo apt install jq
```

Then run:

```shell
jq --version
```
Expected output:

```text
jq-1.6
```

## Compile AccessRefinery
 
```bash
mvn clean package
```

This step automatically runs `mvn test`. If you see output similar to the following, the AccessRefinery environment is set up correctly and the project has been compiled successfully.

Expected output:

```shell
[INFO] Reactor Summary for accessrefinery 1.0-SNAPSHOT:
[INFO] 
[INFO] accessrefinery ..................................... SUCCESS [  5.882 s]
[INFO] bdd ................................................ SUCCESS [01:05 min]
[INFO] mcp ................................................ SUCCESS [ 12.214 s]
[INFO] refinery ........................................... SUCCESS [03:38 min]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  05:01 min
[INFO] Finished at: 2026-01-29T19:54:43+08:00
[INFO] ------------------------------------------------------------------------
```

## Install and Compile Reproduced Access Analyzer

```bash
cd accessanalyzer
echo 'export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:'$(pwd)'/lib/z3-4.14.1/bin' >> ~/.bashrc
source ~/.bashrc
```

## Install and Compile AWS Access Analyzer
