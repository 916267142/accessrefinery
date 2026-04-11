#!/bin/sh

# remove the history results
echo "Clearing paper_figures/data"
rm -rf paper_figures/data
mkdir -p paper_figures/data
cp paper_figures/archive_data/Experiment-Correctness-RealWorld.dat -r paper_figures/data
cp paper_figures/archive_data/Experiment-Effectiveness-RealWorld.dat -r paper_figures/data

# remove the history results
echo "Clearing paper_figures/results"
rm -rf paper_figures/results
mkdir -p paper_figures/results
