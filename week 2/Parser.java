import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Parser {
	private static HashMap<String, Integer> words = new HashMap<String, Integer>();
	
	
	
	/*
	 * Parse the directory "fd" and write results into "parse.txt" .
	 * 
	 * @return 0 on success, -1 on failure.
	 */
	public static int parseDir(File fd) throws Exception {
		int fcnt = 0;
		File[] files = fd.listFiles();
		PrintWriter wr;
		
		if(fd.isDirectory()){
			System.out.println(String.format("%-12s:\t%s", "Path", fd.getAbsoluteFile() ));
			System.out.println(String.format("%-12s:\t%d%s", "Total files", fd.list().length, " files") );
			
			for( File f : files ){
				List <String> lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());
				for( String s : lines ){
					s = s.replaceAll("[-]?", "");	// Combine dash words.	
					s = s.replaceAll("<.*>", "");	// Remove tags.
					s = s.replaceAll("\\W", " ");	// Replace ni on-alphanumericals.
					s = s.toLowerCase();
					for( String w : s.split("\\s") ){
						if(w.length() > 0){
								if( words.containsKey(w) ){ 	// Increment word count.
									int tmp = words.get(w) + 1;
									words.remove(w);
									words.put(w, tmp);
								} else {
									words.put(w, 1);
								}
						}
					}
				}
			}
		}
		else
			fcnt = -1;
		
		
		System.out.println("Parsed words:" + words.size());
		
		 
		/*
		 * Sort the results in descending order and save as "parse.txt" .
		 */
		List<Entry<String, Integer>> sorted = new ArrayList<Entry<String, Integer>>(words.entrySet());
		Collections.sort(sorted, 
				new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> w1, Entry<String, Integer> w2){
				return w2.getValue().compareTo(w1.getValue());
			}
		});
		
		wr = new PrintWriter("/home/sdfgsdfg/Desktop/parse.txt");
		// ----------------
		for( Object s : sorted ) {
			System.out.println(s=s.toString().replace("=", " "));
			wr.println(s);
		}
		wr.close();
		
		
		return fcnt;
	}

	
	
}










/* 
 * 
 * 	ARCHIVE 
 * 
 *  For later use as easy access sorted pool of parsed words.
 *  
 * private static SortedSet<Integer> values;
 * values = new TreeSet<Integer>(words.values());
 * 
 */