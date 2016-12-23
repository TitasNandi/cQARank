package graphClustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class GraphStructureGenerator {

	public static void main(String[] args) throws IOException   {
		// TODO Auto-generated method stub
		String pure =args[0]+"/";
		String path =args[0]+"/wtfidf/";
		Set<String> swords = new HashSet <String>(FileUtils.readLines(new File (pure+"english_sw.txt")));
		  double min_s_tfidf = Double.parseDouble(args[1]);
	        double min_p_tfidf = Double.parseDouble(args[2]);
	        double min_t_tfidf = Double.parseDouble(args[3]);
	        
		File dir = new File(path);
		File graph_dir=new File (pure+"/graph_structure");
        graph_dir.mkdir();
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				String name=child.getName();
				 Set<String> single = new HashSet<String>();
			        Set<String> pair = new HashSet<String>();
			        Set<String> triple = new HashSet<String>();
			        Set<String> graph_list = new HashSet<String>();
			        
			        Scanner single_tfidf_file = new Scanner(child);
			        Scanner pair_tfidf_file = new Scanner(new File(pure+"ptfidf/"+name));
			        Scanner triple_tfidf_file = new Scanner(new File(pure+"ttfidf/"+name));
			        single_tfidf_file.useDelimiter("\n");
			        pair_tfidf_file.useDelimiter("\n");
			        triple_tfidf_file.useDelimiter("\n");
			            while (single_tfidf_file.hasNext()) {
			               String line = single_tfidf_file.next();
			               String [] word_tfidf = line.split("\t");
			                
			                if (word_tfidf.length >= 2 ) {
			               // 	System.out.println("y");
			                	if(Double.parseDouble(word_tfidf[1])>min_s_tfidf)
			                	single.add(word_tfidf[0]);
			                 }
			            }
			            single_tfidf_file.close();
			            while (pair_tfidf_file.hasNext()) {
			            //	System.out.println("y2");
			                String line = pair_tfidf_file.next();
			                String [] pair_tfidf = line.split("\t");
			                 
			                 if (pair_tfidf.length >= 2 ) {
			                	
			                 	if(Double.parseDouble(pair_tfidf[1])>min_p_tfidf)
			                 		pair.add(pair_tfidf[0]);
			                  }
			             }
			            pair_tfidf_file.close();
			            while (triple_tfidf_file.hasNext()) {
			            	
			                String line = triple_tfidf_file.next();
			                String [] triple_tfidf = line.split("\t");
			                 
			                 if (triple_tfidf.length >= 2 ) {
			                 	if(Double.parseDouble(triple_tfidf[1])>min_t_tfidf)
			                 		
			                 		triple.add(triple_tfidf[0]);
			                 
			                 		
			                 		
			                  }
			             }
			            triple_tfidf_file.close();
			       //     if(zeros.contains(name)){
			            
			            FileWriter graph_structure=new FileWriter (new File (pure+"graph_structure/graph_"+name));
			          //  graph_structure.write("digraph G_component_0 {\n");
			            for (String s : triple) {
			            	 System.out.println(s);
			               String relation=s.split(" ")[0];
			               String source=s.split(" ")[1];
			               String dist=s.split(" ")[2];
			              
			                     //   if (!source.equals(dist) && source.length() > 1 && dist.length() > 1 && (single.contains(source) || single.contains(dist))&& (sentiment.contains(source) || sentiment.contains(dist))) {
			               if ((!(swords.contains(source) && swords.contains(dist) )) && !source.equals(dist) && !relation.startsWith("-") &&(single.contains(source) || single.contains(dist))) {
			            	 if(!graph_list.contains(source +" "+ dist +" "+ relation)){  
			              // graph_structure.write("\""+source + "\" -> \"" + dist + "\"[label=\"" + relation  + "\"];\n");
					              graph_structure.write(source + "\t" + dist + "\t"+ relation  +"\n");

			               graph_list.add(source +" "+ dist +" "+ relation);
			            	 }
			                               
			}
			            }
			     //       graph_structure.write("}");
			            graph_structure.close();
			}
		}
	}

}
