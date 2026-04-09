#!/bin/bash

java -jar target/accessrefinery-1.0.jar -m -s --round 10 -f data/Correctness/
java -jar target/accessrefinery-1.0.jar -m -s --round 10 -f data/Scalability_05Keys/
java -jar target/accessrefinery-1.0.jar -m -s --round 10 -f data/Scalability_06Keys/

mv result/ accessrefinery_sat_miner_10rs/
