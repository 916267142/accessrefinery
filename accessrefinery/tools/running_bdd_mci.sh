#!/bin/bash

java -jar target/miner-1.0-SNAPSHOT-jar-with-dependencies.jar -m -r 10 -f data/RW/
java -jar target/miner-1.0-SNAPSHOT-jar-with-dependencies.jar -m -r 10 -f data/Correctness/
java -jar target/miner-1.0-SNAPSHOT-jar-with-dependencies.jar -m -r 10 -f data/Scalability_05Keys/
java -jar target/miner-1.0-SNAPSHOT-jar-with-dependencies.jar -m -r 10 -f data/Scalability_06Keys/

mv result/ accessminer_bdd_mci_10rs/
