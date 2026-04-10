#!/bin/bash

java -jar target/accessrefinery-1.0.jar -m --round 10 -f data/Correctness/
java -jar target/accessrefinery-1.0.jar -m --round 10 -f data/Scalability_05Keys/
java -jar target/accessrefinery-1.0.jar -m --round 10 -f data/Scalability_06Keys/

mkdir -p results
mv result/ results/accessrefinery_bdd_miner_10rs/
