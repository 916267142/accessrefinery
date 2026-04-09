set terminal pdfcairo font "Times New Roman,13" linewidth 1 rounded fontscale 1.35 size 26cm, 11cm

set style line 80 lt rgb "#808080"
set style line 81 lt 0
set style line 81 lt rgb "#808080"
set grid back linestyle 81
set border 3 back linestyle 80
set xtics nomirror
set ytics nomirror

set style line 1 lt rgb "#253494" lw 7 pt 8 ps 1.5
set style line 2 lt rgb "#2b8cbe" lw 7 pt 6 ps 1.5
set style line 3 lt rgb "#74c476" lw 7 pt 2 ps 1.5
set style line 4 lt rgb "#00A000" lw 7 pt 9 ps 1.5
set style line 5 lt rgb "#d4b9da" lw 7 pt 12 ps 1.5
set style line 6 lt rgb "#4F4F4F" lw 7

set xtics font ", 13"
set xlabel "Dataset ID"
set yrange [0:*]
set xrange [0:520]
set xtics offset 0
set key width -0.9 Left vertical maxrows 1 reverse samplen 1 at screen 1.15, 0.99 font ',13' spacing 1.2
set bmargin screen 0.3
set tmargin at screen 0.82
set rmargin screen 0.96

set output 'results/RQ4-Experiment-Scalabiliy-RealWorld.pdf'
set size 1, 0.9

set multiplot layout 1,2 margins 0.15, 0.96, 0.26, 0.68 spacing 0.09

# Plot the first subplot: MCI experiment results
set ylabel "Time (s)"
set ytics font ", 13"
set key width -0.9 Left vertical maxrows 3 reverse samplen 1 at screen 0.52, 0.99 font ',13' spacing 1.2
plot 'data/Experiment-Scalability-MCI-RealWorld.dat' \
     using ($0+1):1 smooth cumulative with lines ls 1 title 'Access Analyzer(Z3)', \
     '' using ($0+1):2 smooth cumulative with lines ls 2 title 'Access Analyzer(CVC5)', \
     '' using ($0+1):4 smooth cumulative with lines ls 3 title 'AccessRefinery'

# Plot the second subplot: RRI experiment results
unset ylabel
set ytics 1500
set key width -0.9 Left vertical maxrows 3 reverse samplen 1 at screen 0.85, 0.99 font ',13' spacing 1.2
plot 'data/Experiment-Scalability-RRI-RealWorld.dat' \
     using ($0+1):1 smooth cumulative with lines ls 1 title 'Baseline(Z3)', \
     '' using ($0+1):2 smooth cumulative with lines ls 2 title 'Baseline(CVC5)', \
     '' using ($0+1):4 smooth cumulative with lines ls 3 title 'AccessRefinery'