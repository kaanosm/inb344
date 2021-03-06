#!/bin/bash

workspace="/Users/team3/PROJECT/"

#DFI0 and DFRWeightingModel removed, not recognized by terrier.
models=(BB2 TF_IDF DFR_BM25 IFB2 In_expB2 In_expC2 InL2 LemurTF_IDF LGD ) #Models with default term frequency parameters
models2=(PL2 BM25 DirichletLM Hiemstra_LM DFRee DPH DLH DLH13 )  # Models with varying parameters
models3=(DirichletLM Hiemstra_LM) # Models with special parameters

# Queries / Topics file to set with trec_terrier.sh 
queries=($workspace'queries/queries.txt' $workspace'queries/expands.0.txt' $workspace'queries/expands.1.txt' $workspace'queries/expands.2.txt')

indexes=("0" "0-stopwords" "0-stopwords-stem")   # Indexes

# Secondary c parameters for term frequency normalisation
# PL2 	=> c 	0.01 ... 20
# BM25	=> b 	0.01, 0.1 ....  0.75 .... 1.75  ( N.D. - exponential after 1.75 )
# normal TF 
# IFB2 => c  0.1 ... 1 ... 50
# LGD =>  	as above
# TF-DF 			as above
# LemurTF_IDF as above
# In_expB2	as above
# In_expC2	as above

# default 'c' parameters (term frequency normalisation)
c=(-5.0 -2.0 0.05 0.1 0.15 0.25 0.5 1.0 1.5 1.75 1.9 3.0 5.0 10.0 20.0 35.0)


hiemstra=(0.03 0.1 0.15 0.2 0.5 0.75 0.99) # Hiemstra parameters - Default = 0.15
dirichlet=(25000 5000 2500 1000 500 1 -1 -500 -1000 -2500 -5000 -25000) # Dirichlet c (mu) parameters

touch $workspace'scriptlog'
counter=1   # Query files counter - 1) Baseline queries 2) Expansions - all 3) Expansions - medical 4) Expansions - wiki
ignoramus=(false true) # Toggle setting to ignore low idf terms

function foo {
	for queryfile in "${queries[@]}"; do
		for i in "${indexes[@]}"; do
			cd /Users/team3/PROJECT/"${i}"
			
			for j in "${models[@]}"; do
				for k in "${c[@]}"; do 		# Models with default term frequency normalisation parameter.
					case $PWD/ in
						$workspace) echo "...In team project folder (Y)";;
						*) cd $workspace; echo "...Changed to team project folder (Y)";;
					esac
					[[ -d /Users/team3/PROJECT/${i}/results/ ]] || mkdir /Users/team3/PROJECT/${i}/results && cd /Users/team3/PROJECT/${i}/results
					if [ "$i" = 0 ]; then tmp=""; elif [ "$i" = 1 ]; then tmp="Stopwords";
					elif [ "$i" = 2 ]; then tmp="PorterStemmer,Stopwords"; fi

					for z in "${ignoramus[@]}"; do  # BM25 etc ignore low document frequencies of query terms
						result="result_${counter}_${j}_${k}_lowidf-$z"
						echo "Result file is $result"
						echo "Saving as $workspace'results/'$result"
						touch "$result" # Create retrieval results file
						/tools/terrier-4.0/bin/trec_terrier.sh "-r -Dterrier.index.path=/Users/team3/PROJECT/$i/ -Dterrier.index.prefix=data -Dtermpipelines=$tmp -Dtrec.model=$j -c $k -Dtrec.results.file=$workspace/$i/results/$result -Dtrec.topics=$queryfile -Dtrec.topics.parser=SingleLineTRECQuery -Dignore.low.idf.terms=$z -Dtrec.querying.outputformat.docno.meta.key=filename"
					done
				done
			done
		done
		counter=$((counter + 1))
	done
} 


foo 2>&1 | tee ${workspace}scriptlog



# First 5 results comparison
cmp="head -n5" # $(cmp+0/results/*.res), then get +/- between top5
