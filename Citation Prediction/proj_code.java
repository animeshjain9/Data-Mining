import java.util.*;
import java.io.*;

public class proj_code {


	public static void main (String [] args) {

		try {
			
			final HashMap<Integer, Fields_Info> FPmap = new HashMap<Integer, Fields_Info>();
			HashMap<String, Integer> Vocabulary = new HashMap<String, Integer>();
			HashMap<Integer, Fields_Info> testmap = new HashMap<Integer, Fields_Info>();
			HashSet<String> stopwords = new HashSet<String>();

			File file = new File("stopwords.txt");
			Scanner sc = new Scanner(file);
			FileReader fileR = new FileReader("c:/datamining/AP_train.txt");
			BufferedReader br = new BufferedReader(fileR);
			FileReader fileR2 = new FileReader("c:/datamining/AP_test_par.txt");
			BufferedReader br2 = new BufferedReader(fileR2);
			FileWriter fileW2 = new FileWriter("kaggle_try.txt", true);
			PrintWriter PStextfile = new PrintWriter(fileW2); 
			String curr_Line = null;
			Integer index = 0;
			String title = null;
			String summary = null;
			String year = null;
			String [] lineArray;
			Fields_Info FieldsObject;
			HashMap<String, Integer> ldt_count;

			while (sc.hasNextLine()) {
				String str = sc.nextLine();
				stopwords.add(str);
			}
			sc.close();

			while ((curr_Line = br.readLine()) != null) {
				if (curr_Line.contains(Constants.INDEX)) {
					lineArray = curr_Line.split(" ");
					index = Integer.valueOf(lineArray[1]);
				}

				if (index > 0 && curr_Line.contains(Constants.PAPER_TITLE)) {
					title = curr_Line.substring(3, curr_Line.length());
				}

				if (index > 0 && curr_Line.contains(Constants.YEAR)) {
					lineArray = curr_Line.split(" ");
					if (lineArray.length > 1) {
						year = lineArray[1];
					} else {
						year = "";
					}
				}

				if (index > 0 && curr_Line.contains(Constants.REFR)) {

					lineArray = curr_Line.split(" ");
					if (isInteger(lineArray[1])) {
						Integer citationIndex = Integer.valueOf(lineArray[1]);
						if (FPmap.containsKey(citationIndex)) {
							FieldsObject = FPmap.get(citationIndex);
						} else {
							FieldsObject = new Fields_Info(citationIndex);	
						}

						FieldsObject.addCitCount();

						FPmap.put(citationIndex, FieldsObject);


						if (FPmap.containsKey(index)) {
							FieldsObject = FPmap.get(index);
						} else {
							FieldsObject = new Fields_Info(index);	
						}

						FieldsObject.addReference(citationIndex);

						FPmap.put(index, FieldsObject);

					}
				}


				if (index > 0 && curr_Line.contains(Constants.ABSTRACT)) {
					if (curr_Line.length() >= 3) {
						summary = curr_Line.substring(3, curr_Line.length());

						summary = summary.toLowerCase();
						int y_num = 0;
						if (isInteger(year)) {
							y_num = Integer.parseInt(year);
						} else {
							y_num = 0;
						}
						if (y_num >= 1995 && y_num <= 2013) {
							FieldsObject = new Fields_Info(index);
							FieldsObject.setYear(y_num);

							title = title.replaceAll("[^a-zA-Z0-9\\s]", "");
							title = title.toLowerCase();
							FieldsObject.setTitle(title);

							summary = summary.replaceAll("[^a-zA-Z0-9\\s]", "");
							summary = summary.toLowerCase();
							FieldsObject.setAbstract(summary);
							FPmap.put(index, FieldsObject);

						}
					}
				}

			}

			index = 0; 

			while ((curr_Line = br2.readLine()) != null) {
				if (curr_Line.contains(Constants.INDEX)) {
					lineArray = curr_Line.split(" ");
					index = Integer.valueOf(lineArray[1]);
				}

				if (index > 0 && curr_Line.contains(Constants.PAPER_TITLE)) {
					title = curr_Line.substring(3, curr_Line.length());
				}

				if (index > 0 && curr_Line.contains(Constants.ABSTRACT)) {
					if (curr_Line.length() >= 5) {
						summary = curr_Line.substring(3, curr_Line.length());

						summary = summary.toLowerCase();
						FieldsObject = new Fields_Info(index);
						summary = summary.replaceAll("[^a-zA-Z0-9\\s]", "");
						summary = summary.toLowerCase();
						lineArray = summary.split(" ");
						for (String s: lineArray) {
							if(!stopwords.contains(s)) {
								FieldsObject.addWord(s);
							}
						}

						title = title.replaceAll("[^a-zA-Z0-9\\s]", "");
						title = title.toLowerCase();

						lineArray = title.split(" ");
						for (String s: lineArray) {
							if(!stopwords.contains(s)) {
								FieldsObject.addWord(s);
							}
						}

						ldt_count = FieldsObject.getBucket();
						for (String s : ldt_count.keySet()) {
							if(!Vocabulary.containsKey(s)){
								Vocabulary.put(s, 1);
							} else {
								Vocabulary.put(s, Vocabulary.get(s) + 1);
							}
						}						

						testmap.put(index, FieldsObject);
					}
				}
			}

			Iterator iter = FPmap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry pairs = (Map.Entry)iter.next();

				FieldsObject = (Fields_Info)pairs.getValue();

				if (FieldsObject.getCitCount() <= 5 || (FieldsObject.getAbstract() == null && FieldsObject.getTitle() == null)) {
					iter.remove();
				} else {
					if (FieldsObject.getAbstract() != null) {
						lineArray = FieldsObject.getAbstract().split(" ");
						for (String s : lineArray) {
							if(!stopwords.contains(s)) {
								FieldsObject.addWord(s);
							}
						}
					}
					FieldsObject.setAbstract("");
					if (FieldsObject.getTitle() != null) {
						lineArray = FieldsObject.getTitle().split(" ");
						for (String s : lineArray) {
							if(!stopwords.contains(s)) {
								FieldsObject.addWord(s);
							}
						}
					}
					FieldsObject.setTitle("");
					ldt_count = FieldsObject.getBucket();
					for (String s : ldt_count.keySet()) {
						if(!Vocabulary.containsKey(s)){
							Vocabulary.put(s, 1);
						} else {
							Vocabulary.put(s, Vocabulary.get(s) + 1);
						}
					}						

				}
			}
			HashMap<String, Integer> tmap = new HashMap<String, Integer>();
			HashMap<String, Integer> tmap2 = new HashMap<String, Integer>();
			double DOC_NUM = (double)FPmap.size();
			Iterator iter2 = testmap.entrySet().iterator();

			final HashMap<Integer, Integer> comb = new HashMap<Integer, Integer>();
			while(iter2.hasNext()) {
				Map.Entry testpairs = (Map.Entry)iter2.next();
				FieldsObject = (Fields_Info)testpairs.getValue();
				tmap = FieldsObject.getBucket();
				int w_count1 = FieldsObject.getWordCount();
				boolean flag = false; 
				int num_saved = 0; //reset value
				HashMap<Integer, Double> topsims = new HashMap<Integer, Double>();
				iter = FPmap.entrySet().iterator();
				while (iter.hasNext()) {
					double dotProduct = 0.0;
					double magnitude1 = 0.0;
					double magnitude2 = 0.0;
					double similarity = 0.0;
					double a = 0.0, b = 0.0;
					Map.Entry pairs = (Map.Entry)iter.next();
					Fields_Info tempObj = (Fields_Info)pairs.getValue();
					tmap2 = tempObj.getBucket();
					int w_count2 = tempObj.getWordCount();
					for (String s : tmap.keySet()) {
						if (tmap2.containsKey(s)) {
							a = (double)tmap.get(s)/w_count1;
							a = a * (1 + Math.log(DOC_NUM/Vocabulary.get(s)));
							b = (double)tmap2.get(s)/w_count2;
							b = b * (1 + Math.log(DOC_NUM/Vocabulary.get(s)));
							dotProduct += (double)(a*b);
						}
					}

					for (String s : tmap.keySet()) {
						a = (double)tmap.get(s)/w_count1;
						a = a * (1 + Math.log(DOC_NUM/Vocabulary.get(s)));
						magnitude1 += Math.pow(a, 2);
					}

					for (String s : tmap2.keySet()) {
						b = (double)tmap2.get(s)/w_count2;
						b = b * (1 + Math.log(DOC_NUM/Vocabulary.get(s)));
						magnitude2 += Math.pow(b, 2);
					}

					magnitude1 = Math.sqrt(magnitude1);
					magnitude2 = Math.sqrt(magnitude2);

					if ( magnitude1 != 0.0 || magnitude2 != 0.0) {
						similarity = dotProduct/(magnitude1 * magnitude2);
					}	 

					if (similarity > 0 && num_saved < 10) {
						num_saved++;
						topsims.put((Integer)pairs.getKey(), similarity);
					} else if (similarity > 0) {
						Iterator iter3 = topsims.entrySet().iterator();
						while(iter3.hasNext()) {
							Map.Entry spairs = (Map.Entry)iter3.next();
							Double svalue = (Double)spairs.getValue();
							if (similarity > svalue.doubleValue()) {
								iter3.remove();
								flag = true;
								break;
							}
						}
					}

					if (flag) {
						topsims.put((Integer)pairs.getKey(), similarity);
						flag = false;
					}
				}

				/*********Print Line of Similarity*************/
				/**********************************************/
				/**********************************************/
				comb.clear();
				for (Integer i: topsims.keySet()) {
					if (FPmap.containsKey(i)) {
						ArrayList<Integer> ref = FPmap.get(i).getReferences();
						for(Integer z: ref) {
							if (comb.containsKey(z)) {
								comb.put(z,comb.get(z)+1);
							} else {
								comb.put(z,1);
							}
						}
					}
				}
				List<Integer> tlist = new ArrayList<Integer>(topsims.keySet());
				List<Integer> flist = new ArrayList<Integer>(comb.keySet());

				Collections.sort(flist, new Comparator<Integer>(){
					@Override
					public int compare(Integer a, Integer b) {
						return (comb.get(b) - comb.get(a));
					}
				});

				if (flist.size() < 10) {
					int sz = flist.size();
					while(sz < 10) {
						flist.add(sz, tlist.get(sz%10));
						sz++;
					}

				}
				StringBuilder buff = new StringBuilder();
				buff.append(testpairs.getKey());
				buff.append(",");
				int count = 0;
				for (Integer i : flist) {
					if (count++ < 10) {
						buff.append(i);
						buff.append(" ");
					} else {
						break;
					}
				}
				PStextfile.println(buff);
				PStextfile.flush();
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}	
	}

	
	public static boolean isInteger(String s) {
		try { 
			Integer.parseInt(s); 
		} catch(NumberFormatException e) { 
			return false; 
		}
		// only got here if we didn't return false
		return true;
	}
}













