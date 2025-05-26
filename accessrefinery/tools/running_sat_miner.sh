#!/bin/bash

java -jar target/refinery-1.0-SNAPSHOT-jar-with-dependencies.jar -m -s --round 10 -f data/Correctness/
java -jar target/refinery-1.0-SNAPSHOT-jar-with-dependencies.jar -m -s --round 10 -f data/RW/
java -jar target/refinery-1.0-SNAPSHOT-jar-with-dependencies.jar -m -s --round 10 -f data/Scalability_05Keys/
java -jar target/refinery-1.0-SNAPSHOT-jar-with-dependencies.jar -m -s --round 10 -f data/Scalability_06Keys/

mv result/ accessrefinery_sat_miner_10rs/
