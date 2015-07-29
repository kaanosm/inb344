set title 'Term Frequencies'
set ylabel 'Rank'
set xlabel 'Count'

set autoscale

fmax = `head -n1 parse.txt | awk '{print $2}'`


plot "parse.txt" u 2:($2/fmax) w linespoints linestyle 3

pause -1
