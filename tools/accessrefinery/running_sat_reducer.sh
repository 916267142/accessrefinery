#!/bin/bash

# the mining process is quite slow, so we run it with 3 rounds.
java -jar target/accessrefinery-1.0.jar -m -s -r --round 3 -f data/Correctness/
java -jar target/accessrefinery-1.0.jar -m -s -r --round 3 -f data/Scalability_05Keys/
java -jar target/accessrefinery-1.0.jar -m -s -r --round 3 -f data/Scalability_06Keys/

mv result/ accessrefinery_sat_reducer_10rs/
