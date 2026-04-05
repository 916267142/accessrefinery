#!/bin/bash

java -jar target/refinery-1.0.jar -m --round 10 -f data/Correctness/
java -jar target/refinery-1.0.jar -m --round 10 -f data/Scalability_05Keys/
java -jar target/refinery-1.0.jar -m --round 10 -f data/Scalability_06Keys/

mv result/ accessrefinery_bdd_miner_10rs/
