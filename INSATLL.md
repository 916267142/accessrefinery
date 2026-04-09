# Installation Overview

<!-- **AccessRefinery** is an intent-mining tool for IAM policies. In our experiments, we compare it with **AWS Access Analyzer**. -->

This installation includes the environment for **AccessRefinery** and our reimplemented **Access Analyzer** baseline.

## Environment Setup

- Prepare a Linux system (recommended: Ubuntu 22.04.5):

ubuntu-22.04.5-desktop-amd64.iso
<https://releases.ubuntu.com/jammy/ubuntu-22.04.5-desktop-amd64.iso>

- Install JDK 17:

```bash
sudo apt install openjdk-17-jdk
```

Add Java to environment variables (recommended):

```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

Verify the Java compiler:

```bash
javac -version
```

Expected output:

```shell
javac 17.0.17
```

Verify Java runtime:

```bash
java -version
```

Expected output:

```shell
openjdk version "17.0.17" 2025-10-21
OpenJDK Runtime Environment (build 17.0.17+10-Ubuntu-122.04)
OpenJDK 64-Bit Server VM (build 17.0.17+10-Ubuntu-122.04, mixed mode, sharing)
```

- Install Maven:

```bash
sudo apt install maven
```

Verify Maven:

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

- Install `jq` for JSON processing:

```bash
sudo apt install jq
```

Verify `jq`:

```bash
jq --version
```

Expected output:

```shell
jq-1.6
```

- Install `Z3`

Z3 is already precompiled. Run the following script to automatically copy the Z3 executable to the appropriate directories.

```bash
sh tools/install_z3.sh
```

Expected output:

```shell
Copied Z3 files to:
- /usr/lib
- /usr/bin
- /home/nkang/.local/bin
Z3 version 4.14.1 - 64 bit
Z3 installation is correct.
```

> Note: CVC5 will be installed automatically when compiling the project.

## Compile AccessRefinery and Access Analyzer

```bash
mvn clean package
```

Expected output:

```shell
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for accessrefinery 1.0:
[INFO] 
[INFO] accessrefinery ..................................... SUCCESS [  0.002 s]
[INFO] accessanalyzer ..................................... SUCCESS [ 17.548 s]
[INFO] bdd ................................................ SUCCESS [  5.056 s]
[INFO] mcp ................................................ SUCCESS [ 13.862 s]
[INFO] refinery ........................................... SUCCESS [ 15.490 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  52.040 s
[INFO] Finished at: 2026-04-09T16:08:12+08:00
[INFO] ------------------------------------------------------------------------
```

Then you will find `target/mcp-1.0.jar`, `target/accessrefinery-1.0.jar`, `target/accessanalyzer-1.0.jar`.

> This step automatically runs `mvn test`. If you can see these JAR files, the AccessRefinery and Access Analyzer environment is set up correctly, and the project has been compiled successfully.
