import java.util.HashMap;
import java.util.Vector;

/*
 * The QueryReader class defines results Vector that holds current results after each query word processed
 * and the index initialized with the constructor index.
 * 
 */
public class QueryReader {
	private Vector<String> results;
	private Indexer index;

	/*
	 * QueryReader is initialized with the index parameter. It parses 
	 * the results into current results vector with parseQuery() .
	 */
	public QueryReader(Indexer index){
		this.results = new Vector<String>();
		this.index = index;
	}

	/*
	 * Clears the current results and parses new query with current index.
	 */
	public void newQuery(String query){
		this.results.clear();
		parseQuery(query, index);
	}

	/*
	 * Displays processed individual queries and the basic query operations 
	 * that have been parsed.
	 */
	private void parseQuery(String query, Indexer index) {
		String qw[] = query.toLowerCase().split(" ");
		HashMap<String, Vector<String>> ind = index.getIndex();
		String qstr = "";

		if(qw.length > 2){
			for(int i=0; i<qw.length; i++){
				if(i<qw.length-1){
					if(qw[i].matches("and") && results.removeAll(ind.get(qw[i-1])) && 
							results.addAll(andDocs(ind.get(qw[i-1]), ind.get(qw[i+1]))))
						qstr=String.format("[%s] AND [%s]", qw[i-1], qw[i+1]);
					else if(qw[i].matches("or") && results.removeAll(ind.get(qw[i-1])) &&
							results.addAll(orDocs(ind.get(qw[i-1]), ind.get(qw[i+1])) ) )
						qstr=String.format("\n [%s] OR [%s]", qw[i-1], qw[i+1]);
					else if(qw[i].matches("not") && results.removeAll(ind.get(qw[i-1]))){
						if( results.addAll(notDocs(ind.get(qw[i-1]), ind.get(qw[i+1]))) )
							qstr=String.format("[%s] NOT [%s]",qw[i-1], qw[i+1]);
					}
					else {
						results.addAll(ind.get(qw[i]));
						qstr += "[" + qw[i] + "]";
					}
				}
			}
		} else if(qw.length == 1){
			qstr += "[" + qw[0] + "] ";
			results.addAll(ind.get(qw[0]));
		}
		System.out.println("\nParsed: " + qstr);
	}

	/* 
	 * "AND" operation on two index vectors for regarding query results.
	 */
	private Vector<String> andDocs(Vector<String> a, Vector<String> b){
		Vector<String> tmp = new Vector<String>(a);
		tmp.retainAll(b);
		return tmp;
	}

	/*
	 * "OR" operation on two index vectors for regarding query results.
	 */
	private Vector<String> orDocs(Vector<String> a, Vector<String> b){
		return new Vector<String>(a.addAll(b)?a:null);
	}

	/*
	 * "NOT" operation on two index vectors for regarding query results.
	 */
	private Vector<String> notDocs(Vector<String> a, Vector<String> b){
		Vector<String> tmp = new Vector<String>(a);
		tmp.removeAll(b);
		return tmp;
	}

	/*
	 * Displays the matched results and vocabulary size of the current index.
	 * Uses the docId hashmap of the Indexer to display the documents identified with
	 * binary strings of their ID.
	 */
	public void display(String query){
		System.out.println(String.format("\nResults\n==================,", query));
		System.out.println(String.format("IDF: \t\t[ %f ]\nTF-IDF: \t[ %f ]\nVocabulary:\t[ %d ]", Math.log(this.index.vocabulary()/results.size()), tfidf(query), this.index.vocabulary())); 
		int i=1;
		for(String doc : results)
			System.out.println(String.format("\tMatch %d: %s - %s", i++,  this.index.getDocName(Integer.parseInt(doc, 2)), doc));
	}

	/*
	 * Cosine similarity for query term and corresponding document vectors.
	 * Similarity is the sum of document vectors divided by normalisation 
	 * and a single query vector assumed 1.
	 */
	private double cos_similarity(String q){
		double tmp0 = 0.0, tmp1 = 0.0;
		for(Object s : index.getIndex().get(q).toArray()){
			String str = (String) s;
			tmp0 += Integer.parseInt(str, 2);
			tmp1 += Math.pow(Integer.parseInt(str, 2), 2);
		}
		return tmp0/Math.sqrt(tmp1);
	}
	
	/*
	 * TF-IDF
	 */
	private double tfidf(String q){
		Vector v = index.getIndex().get(q);
		double cs = cos_similarity(q), tmp0 = 0.0, tmp1 = 0.0,
				idf = index.vocabulary()/v.size();
		
		for(Object s: v.toArray()){
			String str = (String) s;
			tmp0 += (Math.log(cs)+1)*Math.log(idf);
			tmp1 += (Math.log(cs)+1)*Math.log(idf);
		}
		return tmp0/Math.sqrt(tmp1);
	}
}