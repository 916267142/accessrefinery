



1. Linux system

ubuntu-22.04.5-desktop-amd64.iso
https://releases.ubuntu.com/jammy/ubuntu-22.04.5-desktop-amd64.iso

2. install maven

```
sudo apt install maven
```
```
mvn -v
```
You will see this 
```
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 11.0.29, vendor: Ubuntu, runtime: /usr/lib/jvm/java-11-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "6.8.0-90-generic", arch: "amd64", family: "unix"
```
3. install jdk 11
```
sudo apt install openjdk-17-jdk
```

check 

```
javac -v 
```
you will see 
```
javac 17.0.17
```
apt install will automaticlly set the java running environment variable path.
```
java -v 
```
you will see 

```
openjdk version "17.0.17" 2025-10-21
OpenJDK Runtime Environment (build 17.0.17+10-Ubuntu-122.04)
OpenJDK 64-Bit Server VM (build 17.0.17+10-Ubuntu-122.04, mixed mode, sharing)
```

then use 

```
mvn -v
```
you will see 
```shell
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 17.0.17, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "6.8.0-90-generic", arch: "amd64", family: "unix"
```


4. install jq
json files process tools

```shell
sudo apt install jq
```

then

```shell
jq --version
```
you will see 

```
jq-1.6
```


5. 

you will see

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

