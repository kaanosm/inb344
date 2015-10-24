# INB344 Project - CLEF 2015    Documentation

## Introduction
This is the documentation for CLEF 2015 and INB344 project aimed to develop a web query expansion method to improve and evaluate the information retrieval for (medical) search engine.
It is part of a research community evaluation campaign. Our project takes advantage of Terrier retrieval platform and proposes a unique solution by implementing a web query expansion interface
that combines at least 5 different medical databases querying and post-processing a mainstream search engine ( Google ).
The google queries are further extended and post-processed into 3 different categories that correspond to;1
* Wikipedia ( 3 )
* Medical websites - Webmd, Wikipedia, pubmed ( 2 )
* Medical and health sites - Webmd, government health domains, livestrong, healthline, Wikipedia, pubmed ( 1 )

## Project structure
The project consists of evaluations/ that include all of the evaluations for all indexes and corresponding retrieval runs with various parameters,
queries/ that includes baseline queries and 3 query expansions by 3 different methods further detailed below, scripts/ folder including the 
script files that were used to generate all retrievals from 3 indexes (Stemmer+Stopwords, Only stopwords and none), 19 retrieval models, 16 parameters, 4 query files and 
2 iterations for low idf term ignore option toggle.

qrel.txt and qrel2.txt (backup) files are used as qrel topic files for the evaluations for retrieval runs.
The following post-processing were used to alter the result files that were created with retrieval runs. 
* sed -i.bak 's/\/Volumes\/ext\/data\/clef2015\///g' /Volumes/ext/experiments/clef2015.results
* sed -i.bak 's/.html//g' /Volumes/ext/experiments/clef2015.results

Following files include only the P10 evaluations in descendingo order for the retrieval files that reside on the server.
* 0-P10-evaluations 					- P10 evaluations of index with no stemming or stopwords processing.
* 0-stopwords-P10-evaluations			- P10 evaluations of index with only stopwords processing.
* 0-stopwords-stem-P10-evaluations		- P10 evaluations of index with stopword and PorterStemmer processing.

The P10 evaluations above are created by:
* (grep "P_10 " * | awk '{ print $3 "  " $1 }' | sort) >> ../../0-stopwords-stem-P10-evaluations

## Project Solution ( Query Expansion )
##### Solution.java
Included query expansion java class used for query expansions. Run by a java IDE with required dependencies such as Jsoup. 
Below is a brief description of the methods;

# Evaluations
Below are the top5 results for the base queries provided, together with 3 different types of query expansions that filter expansions
as:
* Base queries
* Expansions from: Webmd, government health domains, livestrong, healthline, Wikipedia, pubmed
* Expansions from: Webmd, Wikipedia, pubmed
* Expansions from: Wikipedia

|                    |           Precision Score          |                                                                                                                                                               Evaluated results file : Precision Type |
|--------------------|:----------------------------------:|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| Base Queries Top 5 | 0.8136<br> 0.8152<br> 0.8167<br> 0.8182<br> 0.8364 | result_1_LGD_0.5_lowidf-false.ev:P_10 <br> result_1_LGD_1.0_lowidf-false.ev:P_10<br> result_1_TF_IDF_0.5_lowidf-true.ev:P_10<br> result_1_InL2_1.75_lowidf-false.ev:P_10<br> result_1_TF_IDF_0.5_lowidf-false.ev:P_10 |
| Expansions 1 Top 5 | 0.1031<br> 0.0985<br> 0.0923<br> 0.0800<br> 0.0800 | result_2_LGD_35.0_lowidf-true.ev:P_10<br> result_2_LGD_35.0_lowidf-false.ev:P_10<br> result_2_LGD_20.0_lowidf-false.ev:P_10<br> result_2_LGD_5.0_lowidf-true.ev:P_10<br> result_2_LGD_5.0_lowidf-false.ev:P_10        |
| Expansions 2 Top 5 | 0.0877<br> 0.0815<br> 0.0785<br> 0.0785<br> 0.0754 | result_3_LGD_35.0_lowidf-false.ev:P_10<br> result_3_LGD_20.0_lowidf-false.ev:P_10<br> result_3_LGD_10.0_lowidf-true.ev:P_10<br> result_3_LGD_5.0_lowidf-false.ev:P_10<br> result_3_LGD_5.0_lowidf-true.ev:P_10        |
| Expansions 3 Top 5 | 0.1121<br> 0.1103<br> 0.1086<br> 0.1086<br> 0.1069 | result_4_LGD_0.25_lowidf-true.ev:P_10<br> result_4_LGD_0.15_lowidf-true.ev:P_10<br> result_4_LGD_1.9_lowidf-true.ev:P_10<br> result_4_LGD_0.1_lowidf-true.ev:P_10<br> result_4_LGD_3.0_lowidf-true.ev:P_10            |


Below are the charts for P10 precision values taken from the evaluations of all retrieval runs for only the first queries and expanded queries with stemming and stopwords indexes using the following filter:
* (grep "P_10 " * | awk '{ print $3 "  " $1 }' | sort) >> ../../0-stopwords-stem-P10-evaluations

* Some of the retrieval models accept negative values so parameters include 3 negative values for comparisons. First chart includes only the models that accept such parameters, second one includes infinite and not defined values ( about 1000 more runs with 0 precision )
![alt text](https://github.com/kaanosm/inb344/blob/master/project/p10-1.png "All P10 Evaluations of Baseline Query Retrievals ( 1040 Runs )")
![alt text](https://github.com/kaanosm/inb344/blob/master/project/p10-2.png "All P10 Evaluations of Expanded Queries ( ~2000 Runs )")


## Project Tree
Included is the structure of the project and list of files.

 /team3/PROJECT   38G	total without index1<br>
 11G	./0<br>
 9.1M	./0/evaluations<br>
 1000K	./0/comparisons<br>
 8.2G	./0/results<br>
 3.4M	./0/results/settings<br>
<br>
 11G	./0-stopwords<br>
 9.0M	./0-stopwords/evaluations<br>
 1000K	./0-stopwords/comparisons<br>
 8.1G	./0-stopwords/results<br> 
 3.4M	./0-stopwords/results/settings<br>
<br>
 16G	./0-stopwords-stem<br>
 9.0M	./0-stopwords-stem/evaluations<br>
 1000K	./0-stopwords-stem/comparisons<br>
 7.8G	./0-stopwords-stem/results<br>
 4.5M	./0-stopwords-stem/results/settings<br>
<br>
 32K	./queries<br>


## evaluations/
Evaluation files for each of the 3 indexes
- evaluations-none/  				: Evaluations of retrievals for index 0 ( No stopwords / stemming )
- evaluations-stopwords/ 			: Evaluations of retrievals for index 0-stopwords ( Only stopwords ) 
- evaluations-stopwords-stemming/	: Evaluations of retrievals for index 0-stopwords-stem ( PorterStemming and stopwords )

## expands/
- expands.0.txt	- First query expansions that includes all methods
- expands.1.txt	- Se1cond query expansions that include only some mainstream medical datasets (Pubmed, MDhealth and wiki)
- expands.2.txt	- Third query expansions that includes wikipedia dataset only

## scripts/
### retrievals.sh
* Currently toggles only 9 of the 19 included retrieval models for retrieval but all of them can be activated. Other 10 uses different parameters that have been included within the file.
* Creates a scriptlog for the whole process in the project folder.
* Retrieves the results for each index in corresponding folder as shown below;

* 0/results/...
* 0-stopwords/results/...
* 0-stopwords-stem/results/...

##### The default 'c' parameters ( term frequency normalisation )
* c=(-5.0 -2.0 0.05 0.1 0.15 0.25 0.5 1.0 1.5 1.75 1.9 3.0 5.0 10.0 20.0 35.0)

##### The secondary parameters ( mu for Dirichlet Smoothing, b for BM25 etc... )
* BM25 => b - 0.01, 0.1 ....  0.75 .... 1.75  (exponential after 1.75)
* Hiemstra parameters - Default = 0.15
- hiemstra=(0.03 0.1 0.15 0.2 0.5 0.75 0.99) 
* Dirichlet c (mu) parameters
- dirichlet=(25000 5000 2500 1000 500 1 -1 -500 -1000 -2500 -5000 -25000)

### evaluations.sh 
* Evaluates all the retrieval using trec_eval tool under /tools/trec_eval.9.0/trec_eval
* Organizes results under 3 different indexes as indexFolder/evaluations/...
* Uses qrels.txt file taken from the CLEF 2015 github repository.

### comparisons.sh 
* Organizes the results/ folders under each index/ folder by moving all the corresponding settings files of the result file in a new settings/ folder before post-processing with sed and evaluation runs.
* Creates first and last 5 retrievals for comparing incremental results.

### projecttree.sh
* Creates the projecttree file with sizes of each component that was also included in this document.

### comparisons.sh
* Creates first and last 5 retrieval scores for each results file for detailed incremental comparison with unaccurate retrieval runs.

### scriptlog
* Logs of the entire retrieval process for each of the 3 corresponding indexes.
