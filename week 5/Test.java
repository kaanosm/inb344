public class Test {
	public static void main(String[] args) throws Exception {
//		Indexer p = new Indexer("/home/sdfgsdfg/week 1/Resources/");
		Indexer p = new Indexer("/home/sdfgsdfg/week 5/AP8889/");

		String query;
		System.out.println("===== QUERY READER TEST =====");
		
		QueryReader v = new QueryReader(p);
		v.newQuery(query="translators");
		v.display(query);
//		v.newQuery(query="algorithm");
//		v.display(query);
//		v.newQuery(query="rootfinder");
//		v.display(query);
//		v.newQuery(query="algorithm AND rootfinder");
//		v.display(query);
//		v.newQuery(query="algorithm NOT rootfinder");
//		v.display(query);
	}
}