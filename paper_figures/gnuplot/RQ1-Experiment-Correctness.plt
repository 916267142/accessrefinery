set terminal pdfcairo font "Times New Roman,13" linewidth 1 rounded fontscale 1.35 size 26cm, 7cm

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
set style line 1 lt rgb "#A00000" lw 3 pt 1 ps 1.5
set style line 2 lt rgb "#74c476" lw 3 pt 6 ps 1.5
set style line 3 lt rgb "#2b8cbe" lw 3 pt 2 ps 1.5
set style line 4 lt rgb "#00A000" lw 3 pt 9 ps 1.5
set style line 5 lt rgb "#253494" lw 3 pt 12 ps 1.5
set style line 6 lt rgb "#4F4F4F" lw 3

# Set axis and font properties
set xtics font ", 13"
set ytics font ", 13"
set boxwidth 0.1

set bmargin screen 0.33
set tmargin at screen 0.9
set rmargin screen 0.87

# Output settings
set output 'results/RQ1-Experiment-Correctness.pdf'

set style fill pattern
set boxwidth 0.9

# Set labels
set label "The ID of datasets" at screen 0.54, 0.06 center font ",13"
set label "Real-world" at screen 0.185, 0.90 center font ",13"
set label "Synthetic" at screen 0.655, 0.90 center font ",13"

# Multiplot layout and axis settings
set multiplot layout 1,2 margins 0.10, 0.96, 0.35, 0.94 spacing 0.09
set ylabel "# of Intents" offset 1.5,0
set xrange [0: 520]
set yrange [0: 90]
set ytics 20
plot 'data/Experiment-Effectiveness-RealWorld.dat' using 2 w l ls 3 t''

unset ylabel
set boxwidth 0.5
set xrange [0: 13]
set yrange [0: 5]
set ytics 1
plot 'data/Experiment-Correctness-Synthetic.dat' using ($0+1):2 with boxes ls 3 fs pattern 7 notitle

unset multiplot