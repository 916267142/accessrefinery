set terminal pdfcairo font "Times New Roman, 13" linewidth 1 rounded fontscale 1.35 size 35cm, 9cm

# Set background and axes styles
set style line 80 lt rgb "#808080"
set style line 81 lt 0
set style line 81 lt rgb "#808080"

# Remove top and right borders for clarity
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

# Set axis and font properties
set xtics font "Times New Roman, 13"
set ytics font "Times New Roman, 13"
set boxwidth 0.9

set key width -0.05 Left vertical maxrows 1 reverse samplen 1 at screen 0.74, 1.02 font ',13' spacing 2
set bmargin screen 0.33
set tmargin at screen 0.9
set rmargin screen 0.87

# Output settings
set output 'results/RQ2-Experiment-Effectiveness.pdf'

set label "The ID of datasets" at screen 0.54, 0.06 center font ",13"
set label "Real-world" at screen 0.185, 0.76 center font ",13"
set label "5-Keys" at screen 0.49, 0.76 center font ",13"
set label "6-Keys" at screen 0.81, 0.76 center font ",13"

set multiplot layout 1,3 margins 0.10, 0.96, 0.31, 0.80 spacing 0.13

set yrange[0: 90]
set ytics 20
set ylabel "# of Intents" offset 1.5,0
set xrange[0: 520]
set xtics 150
set size 1, 0.9
set offsets 0.5,0.5,0,0
plot 'data/Experiment-Effectiveness-RealWorld.dat' using 2 w l ls 2 t'Before Reducing', \
	'' using 3 w l ls 3 t'After Reducing'

set log y
set format y "10^{%L}"
unset ylabel
unset yrange
unset ylabel
set xrange[0: 15]
set xtics 3
set size 1, 0.9
set offsets 0.5,0.5,0,0
plot 'data/Experiment-Effectiveness-Synthetic-K2.dat' using ($0+1):2 w lp ls 2 t'Before Reducing', \
	'' using ($0+1):1 w lp ls 3 t'After Reducing'

set log y
unset ylabel
set format y "10^{%L}"
set xrange[0: 15]
set xtics 3
set size 1, 0.9
set offsets 0.5,0.5,0,0
plot 'data/Experiment-Effectiveness-Synthetic-K3.dat' using ($0+1):2 w lp ls 2 t'Before Reducing', \
	'' using ($0+1):1 w lp ls 3 t'After Reducing'

# End of output

