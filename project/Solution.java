import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class Solution {
	static String charset = "UTF-8";

	private static List<String> readQueries() throws IOException{
		List<String> lines = Files.readAllLines(Paths.get("/home/sdfgsdfg/Desktop/clef2015.test.queries.txt"), Charset.defaultCharset());
		return lines;
	}
	
	/* Writes the expanded queries into an expand file under Desktop, with iteration as format for the title. 
	 * 1-) Webmd, government health domains, livestrong, healthline, Wikipedia, pubmed
	 * 2-) Webmd, Wikipedia, pubmed
	 * 3-) Wikipedia
	 */
	private static void writeExpandQueries(List<String> queries) throws IOException, InterruptedException{
		String[] queryLines = queries.toArray(new String[queries.size()]),
				queryWords;
		List<String> expands;				// All lines of expansions of the input queries

		int queryCounter = 1;
		
		for(int j=0; j<3; j++){
			PrintWriter pw = new PrintWriter("/home/sdfgsdfg/Desktop/expands." + j + ".txt");
			for(String line : queryLines){ 				
				line = line.substring(line.indexOf(" ")+1);	// Skip first word, which is the query number
				queryWords = line.split(" ");

				System.out.println("Queries: " + Arrays.toString(queryWords));

				try{
					expands = expand1(queryWords, j);
					if(expands.isEmpty())
						expands.add(line);		// Add original query if no expansions were found
					pw.println(queryCounter + " " + expands.toString().replaceAll("[\\[\\],]", ""));
					pw.flush();
					Thread.sleep(15000);

				} catch(SocketTimeoutException s){
					System.out.println("Read timeout, retrying query expansion.");
					expands = expand1(queryWords, j);
				}
				queryCounter++;
			}
			pw.flush();
			pw.close();
		}
	}

	/* Expands the query inputs with Google according to results' titles.
	 * The most relevant query result from Google search is then searched within Emedicinehealth
	 * database. ( INPUT: 2 relevant words of first result OUTPUT: Top 5 results of emedicinehealth )
	 * 
	 * int option: 
	 * 				- 0 for all current result associations (webmd, livestrong, government domains and MeSH database, wiki..
	 * 				- 1 for WebMD and Wikipedia
	 * 				- 2 for Wikipedia
	 */
	private static List<String> expand1(String[] queries, int option) throws IOException {
		String google = "https://www.google.com.au/search?q=";
		String search = Arrays.toString(queries);
		if(!(option == 2))
			 search+= "+wiki+pubmed";
		else if(option == 2)
			search+= "+wiki";
			
		int index = 0, tmp; // Document title 'String end index' of wiki, livestrong, healthline and others.
		String userAgent = "Mozilla/5.0";
		List<String> results = new ArrayList<String>();

		System.out.println("Request URL: " + google + URLEncoder.encode(search, charset).concat("&num=100"));

		Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset).concat("&client=ubuntu&hl=en&ie=UTF-8").concat("&num=100")).timeout(10000).userAgent(userAgent).get().select("a[href]");

		if(links.size() > 0)
			for (int i=0; i<Math.max(links.size(), 20); i++) {
				String webmd = links.get(i).getElementsContainingText("webmd").text(),				// Webmd / MeSH Database
						gov = links.get(i).getElementsContainingText("gov").text(), 				// Government medical databases like MeSH
						livestrong = links.get(i).getElementsContainingText("livestrong").text(),		// Livestrong
						healthline = links.get(i).getElementsContainingText("healthline").text(),		// Healthline medical
						wiki = links.get(i).getElementsContainingText("wikipedia").text(),			// Wikipedia articles
						pubmed = links.get(i).getElementsContainingText("pubmed").text();			// Pubmed records

				String[] res = null;
				if(option == 0)
					res = new String[]{webmd, gov, livestrong, healthline, wiki, pubmed};
				else if(option == 1)
					res = new String[]{webmd, wiki, pubmed};
				else if(option == 2)
					res = new String[]{wiki};
				for(String s : res){
					s = s.toLowerCase().replaceAll(".*blog.*", " "); 								// Remove all blog titles
					s = s.replaceAll("-.*", "").replaceAll("user:.*", "").replaceAll("pubmed.*", "")// Further title processing
							.replaceAll("pub.*", "").replaceAll("pdf", "").replaceAll("wiki.*", "")
							.replaceAll("related arti.*", "").replaceAll("\\s*treatment\\s*", "")
							.replaceAll("talk:", "");

					/* Resulting response document titles and further string processing */
					if(!s.matches("\\s*")){			// Remove processed, empty or unnecessary results 					
						/* Check for title delimiters in common document titles */
						if( (index = s.indexOf("|")) > 0 ){		// Remove | seperator from titles
							if( (tmp = s.indexOf("-")) > 0 && tmp < index){
								index = tmp;
							}
						} else if ((tmp = s.indexOf("-")) > 0)	// Remove - seperator from titles
							index = tmp;

						if( index > 0 && tmp > 0 )			// Get first occurance of "-" or "|" if there was
							index = Math.min(index, tmp);
						if( ((tmp = s.indexOf("wikipedia")) > 0))	// Or to beginning of wiki in any case
							index = Math.min(index, tmp);

						if( index > 0){						// If processed index exists, add until the beginning
							results.add(s=s.substring(0, index));
						}
						else{
							results.add(s);					// Add unprocessed results 
						}
					}

				}
			}
		else 
			System.out.println("NO LINKS !");


		/* Remove duplicates from results */
		Set<String> results2 = new HashSet<String>();  
		results2.addAll(results);
		results.clear();
		results.addAll(results2);

		for(String s : results)
			System.out.println(s);
		System.out.println("\n\n");

		return results;
	}

	/* Expands up to 10 queries per query via web expansion methods.
	 * Uses emedicinehealth database.
	 */
	private static String expand2(String[] queries) throws IOException {
		URLConnection conn = null;
		InputStreamReader r = null;
		StringBuilder sb = new StringBuilder();
		String theurl = "";
		URL url;

		theurl += "http://www.emedicinehealth.com/script/main/srchcont_em.asp?src=";
		for (String s : queries) {
			theurl += "+" + s;
		} theurl += "&cat=emss";

		url =  new URL(theurl);

		try{
			conn = url.openConnection();
			if(conn != null)
				conn.setReadTimeout(10*1000);
			if(conn != null && conn.getInputStream() != null){
				r = new InputStreamReader(conn.getInputStream(), "UTF-8");
				BufferedReader br = new BufferedReader(r);
				if(br != null){
					int cp;
					while((cp = br.read()) != -1)
						sb.append((char) cp);
					br.close();
				}

			}
		} 
		catch(Exception e){
			throw new RuntimeException("Error while calling URL: " + url, e);
		}

		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		writeExpandQueries(readQueries());
	}
}
