import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Indexer {
	
	public Indexer(String folder) throws IOException{
		this.index = parseDir(new File(folder));
	}
	/*
	 * terms:	All parsed terms in working directory
	 * docId:	Doc ID -> Doc name
	 * index:	Doc ID -> Binary vector of terms
	 */
	private HashMap<String, Integer> terms = new HashMap<String, Integer>();			
	private HashMap<String, Vector<String>> index = new HashMap<String, Vector<String>>();
	private HashMap<Integer, String> docId = new HashMap<Integer, String>();
	
	/*
	 * Parses the directory "fd" store the unique terms and their count in terms. 
	 * Stores each processed document ID and it's binary string into docId.
	 * Additionally saves every term and the documents (binary string IDs) that it has been
	 * parsed in, into a hashmap of term and the corresponding vector of indexed documents.
	 */
	HashMap<String, Vector<String>> parseDir(File fd) throws IOException{
		int doc = 0; // Document id to be converted into binary string.
		File[] files = fd.listFiles();
		String binStr;
		
		if(fd.isDirectory()){
			System.out.println(String.format("%-12s:\t%s", "Path", fd.getAbsoluteFile() ));
			System.out.println(String.format("%-12s:\t%d%s", "Total files", fd.list().length, " files") );
			
			
			for( File f : files ){
				docId.put(doc, f.getName());
				binStr = Integer.toBinaryString(doc);
				System.out.println(String.format("%-4s:\t%d - %s ( %s )", "Doc", docId.keySet().toArray()[doc], docId.get(doc), binStr));
				List <String> lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());
				for( String s : lines ){
					s = s.replaceAll("[-]?", "");
					s = s.replaceAll("<.*>", "");
					s = s.replaceAll("\\W", " ");
					s = s.toLowerCase();
					for( String w : s.split("\\s") ){
						if(w.length() > 0){
								if( index.containsKey(w) ){
									index.get(w).add(binStr);
									terms.put(w, terms.remove(w)+1);
								} else {
									Vector <String> tmp = new Vector<String>();
									tmp.add(binStr);
									index.put(w, tmp);
									terms.put(w, 1);
								}
						}
					}
				}
				doc++;
			}
		}
		
		System.out.println("Parsed terms:" + index.size());
		
		
		return index;
	}
	
	/*
	 * Gets the current index.
	 */
	public HashMap<String, Vector<String>> getIndex(){
		return index;
	}
	
	/* 
	 * Returns the name of the document with i as 
	 * parsed Integer from the stored binary string (document ID)
	 * in docId hashmap.
	 */
	public String getDocName(int i){
		return docId.get(i);
	}
	
	/*
	 * The unique terms / vocabulary size of the current indexer.
	 */
	public int vocabulary(){
		return terms.size();
	}
}