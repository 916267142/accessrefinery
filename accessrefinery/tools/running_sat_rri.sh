#!/bin/bash

# the mining process is quite slow, so we run it with 2 rounds.
java -jar target/miner-1.0-SNAPSHOT-jar-with-dependencies.jar -m -s -r2 -r 3 -f data/RW/
java -jar target/miner-1.0-SNAPSHOT-jar-with-dependencies.jar -m -s -r2 -r 3 -f data/Correctness/
java -jar target/miner-1.0-SNAPSHOT-jar-with-dependencies.jar -m -s -r2 -r 3 -f data/Scalability_05Keys/
java -jar target/miner-1.0-SNAPSHOT-jar-with-dependencies.jar -m -s -r2 -r 3 -f data/Scalability_06Keys/

mv result/ accessminer_sat_rri_10rs/
