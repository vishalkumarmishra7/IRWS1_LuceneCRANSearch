set term png
set output "pr_graph.png"
set title "P/R from trec_eval test Analyzer=English"
set ylabel "Precision"
set xlabel "Recall"
set xrange [0:1]
set yrange [0:1]
set xtics 0,.2,1
set ytics 0,.2,1

plot 'data_result_English_BM25.dat' title "Similarity=BM25" with lines, 'data_result_English_LMDirichlet.dat' title "Similarity=LMDirichlet" with lines, 'data_result_English_TFIDF.dat' title "Similarity=TFIDF" with lines