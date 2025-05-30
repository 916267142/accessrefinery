set terminal pdfcairo font "Times New Roman,13" linewidth 1 rounded fontscale 1.35 size 26cm, 9cm

# Line style for axes
set style line 80 lt rgb "#808080"
set style line 81 lt 0 # dashed
set style line 81 lt rgb "#808080" # grey

# Remove border on top and right. 
set grid back linestyle 81
set border 3 back linestyle 80
set xtics nomirror
set ytics nomirror

# Set line styles with distinct colors and thickness
set style line 1 lt rgb "#253494" lw 3 pt 8 ps 1.5
set style line 2 lt rgb "#2b8cbe" lw 3 pt 6 ps 1.5
set style line 3 lt rgb "#74c476" lw 3 pt 2 ps 1.5
set style line 4 lt rgb "#00A000" lw 3 pt 9 ps 1.5
set style line 5 lt rgb "#d4b9da" lw 3 pt 12 ps 1.5
set style line 6 lt rgb "#4F4F4F" lw 3

# set image properties
set xtics font ", 11"
set ytics font ", 11"
set boxwidth 0.9

# Set key and margin properties
set key width 1.5 Left vertical maxrows 1 reverse samplen 1 at screen 0.97, 1.03 font ',13' spacing 2
set bmargin screen 0.33
set tmargin at screen 0.9
set rmargin screen 0.87

# Output settings
set output 'results/Experiment-Scalability-Reducing-RQ2.pdf'

set label "# of Allow statements" at screen 0.54, 0.06 center font ",13"
set label "5-Keys" at screen 0.183, 0.75 center font ",13"
set label "6-Keys" at screen 0.651, 0.75 center font ",13"

set multiplot layout 1,2 margins 0.13, 0.96, 0.31, 0.80 spacing 0.09

set log y
set format y "10^{%L}"
set ylabel "Time (ms)"
set xrange[-1: 16]
set xtics 3
set size 1, 0.9
set offsets 0.5,0.5,0,0
plot 'data/Experiment-Scalability-RRI-K2.dat' using ($0+1):1 w lp ls 1 title 'Baseline(Z3)', \
     '' using ($0+1):2  w lp ls 2 title 'Baseline(CVC5)', \
     '' using ($0+1):4  w lp ls 3 title 'AccessRefinary'

unset ylabel
set log y
set format y "10^{%L}"
set xrange[-1: 16]
set xtics 3
set size 1, 0.9
set offsets 0.5,0.5,0,0
plot 'data/Experiment-Scalability-RRI-K3.dat' using ($0+1):1 w lp ls 1 title 'Baseline(Z3)', \
     '' using ($0+1):2  w lp ls 2 title 'Baseline(CVC5)', \
     '' using ($0+1):4  w lp ls 3 title 'AccessRefinary'
