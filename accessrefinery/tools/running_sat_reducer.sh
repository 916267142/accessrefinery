#!/bin/bash

# the mining process is quite slow, so we run it with 3 rounds.
java -jar target/refinery-1.0-SNAPSHOT-jar-with-dependencies.jar -m -s -r --round 3 -f data/Correctness/
java -jar target/refinery-1.0-SNAPSHOT-jar-with-dependencies.jar -m -s -r --round 3 -f data/Scalability_05Keys/
java -jar target/refinery-1.0-SNAPSHOT-jar-with-dependencies.jar -m -s -r --round 3 -f data/Scalability_06Keys/

mv result/ accessrefinery_sat_reducer_10rs/
