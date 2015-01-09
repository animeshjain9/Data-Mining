import java.util.*;
public class Fields_Info {

	private int index;
	private int year;
	private int citations = 0;
	private String title = null;
	private String content = null;	
	private HashMap<String, Integer> bucket = new HashMap<String, Integer>();
	private String authors;
	private int w_count = 0;
	private ArrayList<Integer> references;

	public Fields_Info (int index) {
		this.index = index;
		references = new ArrayList<Integer>();
	}

	public void setYear (int year) {
		this.year = year;
	}

	public void setTitle (String title) {
		this.title = title;
	}

	public void setAbstract (String content) {
		this.content = content; 
	}

	public void addReference (int ref) {
		references.add(ref);
	}

	public void addWord (String str) {
		if (str != null) {
			if (bucket.containsKey(str)) {
				bucket.put(str,bucket.get(str) + 1);
			} else {
				bucket.put(str, 1);
			}
			w_count++;
		}
	}

	public void addCitCount() {
		this.citations += 1;
	}

	public void addAuthors(String auth) {
		authors = auth;
	}


	public int getWordCount() {
		return this.w_count;
	}

	public int getYear () {
		return year;
	}

	public String getTitle () {
		return title;
	}

	public String getAbstract () {
		return content;
	}

	public HashMap<String, Integer> getBucket() {
		return bucket;
	}

	public int getCitCount() {
		return this.citations;
	}

	public String getAuthors( ) {
		return authors;
	}

	public ArrayList<Integer> getReferences ( ) {
		return references;
	}

	public int getRefNum ( ) {
		return references.size();
	}

	public String toString () {
		StringBuffer flds = new StringBuffer();
		flds.append(index);
		flds.append(";");
		flds.append(title);
		flds.append(";");
		flds.append(year);
		flds.append(";");
		flds.append(content);	

		return (new String(flds));
	}
}