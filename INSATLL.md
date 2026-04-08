# Installation Overview

**AccessRefinery** is an intent-mining tool for IAM policies. In our experiments, we compare it with **AWS Access Analyzer**.


## Set Up the Experimental Environment

- Prepare a Linux system (recommended: Ubuntu 22.04.5):

ubuntu-22.04.5-desktop-amd64.iso  
https://releases.ubuntu.com/jammy/ubuntu-22.04.5-desktop-amd64.iso

- Install JDK 17:

```bash
sudo apt install openjdk-17-jdk
```

Add Java to environment variables (recommended):

```bash
export 'JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
export 'PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
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

Then run:

```bash
jq --version
```

Expected output:

```shell
jq-1.6
```

- Install `Z3`

```bash
echo 'export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:'$(pwd)'/baselines/accessanalyzer-reimpl/lib/z3-4.14.1/bin' >> ~/.bashrc
source ~/.bashrc
```

Then run:

```bash
z3 -version
```

Expected output:

```shell
Z3 version 4.12.2 - 64 bit
```

> Note: CVC5 is automatically installed by Maven package.

## Compile AccessRefinery and Access Analyzer

```bash
mvn clean package
```

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

Then you will find `target/mcp-1.0.jar`, `target/accessrefinery-1.0.jar`, `target/accessanalyzer-1.0.jar`.

This step automatically runs `mvn test`. If you can see these JAR files, the AccessRefinery and Access Analyzer environment is set up correctly, and the project has been compiled successfully.
