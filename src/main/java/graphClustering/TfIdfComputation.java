package graphClustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

public class TfIdfComputation {

	public static void main(String[] args) throws IOException {
	 String path =args[0]+"/";
	// System.out.println(args[0]);
	 //"C:/Users/skohail/Desktop/spox/qa/qa2/ids.txt"
   	 File dir = new File(path+args[1]);
     Set<String> ids = new HashSet <String>(FileUtils.readLines(new File (path+args[2])));
     File[] directoryListing = dir.listFiles();
    
   	 Map<String, Integer> word_freq_in_doc = new HashMap<String, Integer>();
   	 Map<String, Integer> idf_freq = new HashMap<String, Integer>();
   	 int size=0;
   	 if (directoryListing != null) {
   		 for (File child : directoryListing) {
   		 size++;
   		 String id=child.getName();
   		 Set<String> uniq= new HashSet<String>();
   		   	Scanner document = new Scanner(child);
   		   	document.useDelimiter("\n");
   		 while (document.hasNext()){
		   		String []fields=document.next().split("\t");
		   		
		 		if(fields.length>=3){
		 			String w1=fields[1];
 		   		String w2=fields[2];
 		   	//	System.out.println(w2);
 		   		//w2= w2.substring(0, w2.lastIndexOf("#"));
 		   		String relation=fields[0];
 		   		String[] strings = { w1,w2 };
 		   		Arrays.sort(strings);
 		   		String toCompute= Arrays.toString(strings);
 		   		toCompute=toCompute.substring(1, toCompute.length()-1);
 		   		
		 			uniq.add(toCompute);
		 		System.out.println(id +"\t"+ size);
		 		if (word_freq_in_doc.containsKey(toCompute+"_@_"+id)){
							word_freq_in_doc.put(toCompute+"_@_"+id, word_freq_in_doc.get(toCompute+"_@_"+id)+1);
						}
						else{
							word_freq_in_doc.put(toCompute+"_@_"+id, 1);
						}
		 		}	
		    }
		   	for (String s : uniq) {
		   		if (idf_freq.containsKey(s)){
		   			idf_freq.put(s, idf_freq.get(s)+1);
					
				}
				else{
					idf_freq.put(s, 1);
				}
		   	}
		   			document.close();
		  }
		 
	 }
		 

		    System.out.println(size);
		    File n_dir = new File(path+"/ptfidf");
		    n_dir.mkdir();
		    if (directoryListing != null ) {
		    	for (File child : directoryListing){
		    	LinkedHashMap<String, Double> tosort = new LinkedHashMap<String, Double>();
		    	Scanner document = new Scanner(child);
		    	document.useDelimiter("\n");
		    	while(document.hasNext()){
		    		String current = document.next();
		    		String []current_records= current.split("\t");
		    		if(current_records.length>=3){
		    			String w1=current_records[1];
		    			String w2=current_records[2];
		    		String[] strings = { w1,w2 };
     		   		Arrays.sort(strings);
     		   		String toCompute= Arrays.toString(strings);
     		   		toCompute=toCompute.substring(1, toCompute.length()-1);
		    		//	String second_word = w2;
		    			String idd=child.getName();
		    			System.out.println(child.getName());
		    			double idf1= Math.log((double)size/idf_freq.get(toCompute));
		    			int tf1= word_freq_in_doc.get(toCompute+"_@_"+idd);
		    			double tfidf1= tf1*idf1;
		    			tosort.put( toCompute, tfidf1);
		    		}
		    		}
		    	 
		    	    
		    	    // attempt to create the directory here
		    	    
   		    	if(ids.contains(child.getName()))
   		    	writeHashToFile(sortByComparator(tosort,false),n_dir+"/TFIDF_"+child.getName());
   		    	document.close();
   		    	}  		
   	 
   }
   		    System.out.println(size);

   		if (directoryListing != null) {
      		 for (File child : directoryListing) {
      		 size++;
      		 String id=child.getName();
      		 Set<String> uniq= new HashSet<String>();
      		   	Scanner document = new Scanner(child);
      		   	document.useDelimiter("\n");
      		   	while (document.hasNext()){
      		   		String []fields=document.next().split("\t");
      		   		
      		 		if(fields.length>=3){
      		 			String w1=fields[1];
          		   		String w2=fields[2];
          		   		String toCompute= w1;
          		   		
      		 			uniq.add(toCompute);
      		 		//System.out.println(toCompute);
      		 			uniq.add(w2);
      		 		if (word_freq_in_doc.containsKey(toCompute+"_@_"+id)){
      							word_freq_in_doc.put(toCompute+"_@_"+id, word_freq_in_doc.get(toCompute+"_@_"+id)+1);
      						}
      						else{
      							word_freq_in_doc.put(toCompute+"_@_"+id, 1);
      						}
      		    			if (word_freq_in_doc.containsKey(w2+"_@_"+id)){
      							word_freq_in_doc.put(w2+"_@_"+id, word_freq_in_doc.get(w2+"_@_"+id)+1);
      						}
      						else{
      							word_freq_in_doc.put(w2+"_@_"+id, 1);
      						}
      		 		}	
      		    }
      		   	for (String s : uniq) {
      		   		if (idf_freq.containsKey(s)){
      		   			idf_freq.put(s, idf_freq.get(s)+1);
   						
   					}
   					else{
   						idf_freq.put(s, 1);
   					}
      		   	}
      		   			document.close();
      		  }
      		 
      	 }
      		 

      		    System.out.println(size);
      		   n_dir = new File(path+"/wtfidf");
      		 n_dir.mkdir();
      		    if (directoryListing != null) {
      		    	for (File child : directoryListing){
      		    	LinkedHashMap<String, Double> tosort = new LinkedHashMap<String, Double>();
      		    	Scanner document = new Scanner(child);
      		    	document.useDelimiter("\n");
      		    	while(document.hasNext()){
      		    		String current = document.next();
      		    		String []current_records= current.split("\t");
      		    		if(current_records.length>=3){
      		    			String w1=current_records[1];
      	       		   		String w2=current_records[2];
      	       		   	//	System.out.println(w2);
      	       		   		//w2= w2.substring(0, w2.lastIndexOf("#"));
      	       		   		String toCompute= w1;
      		    			String second_word = w2;
      		    			String idd=child.getName();
      		    			System.out.println(child.getName());
      		    			double idf1= Math.log((double)size/idf_freq.get(toCompute));
      		    			double idf2= Math.log((double)size/idf_freq.get(second_word));
      		    			int tf1= word_freq_in_doc.get(toCompute+"_@_"+idd);
      		    			int tf2= word_freq_in_doc.get(second_word+"_@_"+idd);
      		    			double tfidf1= tf1*idf1;
      		    			double tfidf2= tf2*idf2;
      		    			tosort.put( toCompute, tfidf1);
      		    			tosort.put( second_word , tfidf2);
      		    		}
      		    		}
      		    	if(ids.contains(child.getName()))
      		    		writeHashToFile(sortByComparator(tosort,false),n_dir+"/TFIDF_"+child.getName());
      		    	document.close();
      		    	}  		
      	 
      }
      		    System.out.println(size);
   	
      	 if (directoryListing != null) {
       		 for (File child : directoryListing) {
       		 size++;
       		 String id=child.getName();
       		 Set<String> uniq= new HashSet<String>();
       		   	Scanner document = new Scanner(child);
       		   	document.useDelimiter("\n");
       		 while (document.hasNext()){
    		   		String []fields=document.next().split("\t");
    		   		
    		   		if(fields.length>=3){
    		 			String w1=fields[1];
        		   		String w2=fields[2];
        		   		String relation=fields[0];
        		   		String toCompute= relation+" "+w1+" "+w2;
        		   		if(relation.startsWith("-")){
        		   			relation=relation.substring(1);
        		   			toCompute=relation+" "+w2+" "+w1;
        		   		}
    		 			uniq.add(toCompute);
    		 		if (word_freq_in_doc.containsKey(toCompute+"_@_"+id)){
    							word_freq_in_doc.put(toCompute+"_@_"+id, word_freq_in_doc.get(toCompute+"_@_"+id)+1);
    						}
    						else{
    							word_freq_in_doc.put(toCompute+"_@_"+id, 1);
    						}
    		 		}	
    		    }
    		   	for (String s : uniq) {
    		   		if (idf_freq.containsKey(s)){
    		   			idf_freq.put(s, idf_freq.get(s)+1);
    					
    				}
    				else{
    					idf_freq.put(s, 1);
    				}
    		   	}
    		   			document.close();
    		  }
    		 
    	 }
    		 

    		    System.out.println(size);
    		     n_dir = new File(path+"/ttfidf");
    		    n_dir.mkdir();
    		    if (directoryListing != null) {
    		    	for (File child : directoryListing){
    		    	LinkedHashMap<String, Double> tosort = new LinkedHashMap<String, Double>();
    		    	Scanner document = new Scanner(child);
    		    	document.useDelimiter("\n");
    		    	while(document.hasNext()){
    		    		String current = document.next();
    		    		String []current_records= current.split("\t");
    		    		if(current_records.length>=3){
    		    			String w1=current_records[1];
    		    			String w2=current_records[2];
    		    			String relation=current_records[0];
        		 			String toCompute= relation+" "+w1+" "+w2;
        		 			if(relation.startsWith("-")){
            		   			relation=relation.substring(1);
            		   			toCompute=relation+" "+w2+" "+w1;
            		   		}
    		    			String idd=child.getName();
    		    			System.out.println(child.getName());
    		    			double idf1= Math.log((double)size/idf_freq.get(toCompute));
    		    			int tf1= word_freq_in_doc.get(toCompute+"_@_"+idd);
    		    			double tfidf1= tf1*idf1;
    		    			tosort.put( toCompute, tfidf1);
    		    		}
    		    		}
       		    	if(ids.contains(child.getName()))
       		    		writeHashToFile(sortByComparator(tosort,false),n_dir+"/TFIDF_"+child.getName());
       		    	document.close();
       		    	}  		
       	 
       }
       		    System.out.println(size);
  	 }
	static void writeHashToFile(Map<String, Double> hashToWrite, String fileName) throws IOException{
		Writer writer = new OutputStreamWriter(
				new FileOutputStream(fileName), "UTF-8");
		BufferedWriter filtered_tfidf = new BufferedWriter(writer); 
	   
			for (HashMap.Entry <String,Double>entry : hashToWrite.entrySet()) {
				filtered_tfidf.write(entry.getKey() + "\t" + entry.getValue()+"\n");
			}
			filtered_tfidf.close();
	   	 
	   }
	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order)
	{

	   List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

	   // Sorting the list based on values
	   Collections.sort(list, new Comparator<Entry<String, Double>>()
	   {
	       public int compare(Entry<String, Double> o1,
	               Entry<String, Double> o2)
	       {
	           if (order)
	           {
	               return o1.getValue().compareTo(o2.getValue());
	           }
	           else
	           {
	               return o2.getValue().compareTo(o1.getValue());

	           }
	       }
	   });

	   // Maintaining insertion order with the help of LinkedList
	   Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
	   for (Entry<String, Double> entry : list)
	   {
	       sortedMap.put(entry.getKey(), entry.getValue());
	   }

	   return sortedMap;
	} 
}
