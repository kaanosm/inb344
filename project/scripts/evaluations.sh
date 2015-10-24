#!/bin/bash

wrk="/Users/team3/PROJECT/"
indexfolders=("0/" "0-stopwords/" "0-stopwords-stem/")
e="/tools/trec_eval.9.0/trec_eval"

for i in ${indexfolders[@]}; do
	wrk="/Users/team3/PROJECT/${i}results"
	cd $wrk
    echo In folder "$wrk"
	
	[[ ! -d ../evaluations ]] && mkdir ../evaluations
	files=(./*)
	for j in ${files[@]}; do
		if [ -f $j ]; then
			$e ../../qrel.txt $j > ../evaluations/${j}.ev
		fi
	done
done

