#!/bin/bash

set -e

# generate data
#python3 helper.py

# draw
for file in `ls -l gnuplot`
do
    if [ "${file##*.}"x = "plt"x ]; then
        echo "draw figure of $file..."
        gnuplot "gnuplot/$file"
    fi
done