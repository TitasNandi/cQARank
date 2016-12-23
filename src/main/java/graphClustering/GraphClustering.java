package graphClustering;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.itextpdf.text.log.SysoLogger;

public class GraphClustering {
// min percentage of stopwords of intersection
// shortest path threshold for sum
// shortest path threshold for text
	public static void main(String[] args) throws IOException, InterruptedException {
		//String dir=args[0];
	String dir=args[0]+"/";
		// stop words
        Set<String> swords = new HashSet <String>(FileUtils.readLines(new File (dir+"english_sw.txt")));
        //matching results table
        FileWriter results=new FileWriter (new File (dir+"cov_features.txt"));
        results.write("original_text"+"\t"+"candidate_text"+"\t"+"vocab_cov/candidate_text*100"+"\t"+"vocab_cov/original_text*100"+"\t"+"edge_cov/candidate_text*100"+"\t"+"edge_cov/original_text*100"+"\t"+"pair_cov/candidate_text*100"+"\t"+"pair_cov/original_text*100"+"\t"+"graph_cov/candidate_text*100"+"\t"+"graph_cov/original_text*100"+"\n");
   //    FileWriter resultt=new FileWriter (new File (dir+"training_set_text_sum_cs_computation/max_coverage_levels/text_vocab_cov_all.txt"));
		int threshold =3;
	   // Set<String> ids = new HashSet <String>(FileUtils.readLines(new File (dir+"ids.txt")));
	    
	    // loop into sum files
	   for(String name: FileUtils.readLines(new File (dir+"doubleID.txt"))){
	  //  for(String name: FileUtils.readLines(new File (dir+"ids.txt"))){
	    //	String second_
	   String sum_name = name.split("\t")[0].trim();
	   System.out.println(name);
	   Map <String, Double>sum_match_scores = new HashMap<String,Double>();
	   Map <String, Double>text_match_scores = new HashMap<String,Double>();
	    Scanner sum_graph = new Scanner(new File(dir+"graph_structure/graph_TFIDF_"+sum_name)); // reading the sum graph file
	    /*==================Sum Graph Maps for storing vertices, Edges and Pairs ========================*/
       // Map <String, Vertex>sum_vertex_map = new HashMap<String,Vertex>();
       // Map <String, Edge> sum_edge_map = new HashMap<String,Edge>();
        Set <String> sum_edges_set = new HashSet <String>();
        Set <String> sum_vertices_set = new HashSet <String>();
        Set <String> sum_pair_map = new HashSet<String>();
        /*===============================================================================================*/
        while(sum_graph.hasNextLine()){
        	String relation_line = sum_graph.nextLine().toLowerCase();
        	String []current = relation_line.split("\t");
        	String relation_rev = current[1]+"\t"+current[0]+"\t"+current[2];
        	sum_pair_map.add(current[1]+"\t"+current[0]);
        	sum_pair_map.add(current[0]+"\t"+current[1]);
        	if (!sum_vertices_set.contains(current[0]))
        		sum_vertices_set.add(current[0]);
        	if (!sum_vertices_set.contains(current[1]))
        		sum_vertices_set.add(current[1]);
        	if (!sum_edges_set.contains(relation_line))
        		sum_edges_set.add(relation_line);
            if (!sum_edges_set.contains(relation_rev))
            	sum_edges_set.add(relation_rev);
        	/*if (!sum_edges_set.contains(relation_line))
        		sum_edges_set.add(relation_line);
            if (!sum_edges_set.contains(relation_rev))
            	sum_edges_set.add(relation_rev);
        	 if (!sum_edge_map.containsKey(relation_line))
        		sum_edge_map.put(relation_line,new Edge(current[2],sum_vertex_map.get(current[0]),sum_vertex_map.get(current[1]),1));
            if (!sum_edge_map.containsKey(relation_rev))
            		sum_edge_map.put(relation_rev,new Edge(current[2],sum_vertex_map.get(current[1]),sum_vertex_map.get(current[0]),1));*/
        }
        sum_graph.close();
        // loop through text graphs
        for (String text_name: name.split("\t")[1].split(",")){
      //  for (String text_name:  FileUtils.readLines(new File (dir+"ids.txt"))){
        	System.out.println(text_name);
        	text_name=text_name.trim();
        Scanner text_graph = new Scanner(new File(dir + "graph_structure/graph_TFIDF_"+text_name));
        /*==================Text Graph Maps for storing vertices, Edges and Pairs ========================*/
        Map <String, Edge> text_edge_map = new HashMap<String,Edge>();
        Map <String, Vertex> text_vertex_map = new HashMap<String,Vertex>();
        Set <String> text_pair_map = new HashSet<String>();
	     List<Vertex> nodes= new ArrayList <Vertex>();
	     List<Edge> edges=new ArrayList<Edge>();
	    /*===============================================================================================*/
        while(text_graph.hasNextLine()){
        	String relation_line = text_graph.nextLine().toLowerCase();
        	String []current = relation_line.split("\t");
        	String relation_rev = current[1]+"\t"+current[0]+"\t"+current[2];
        	text_pair_map.add(current[1]+"\t"+current[0]);
        	text_pair_map.add(current[0]+"\t"+current[1]);
        	if (!text_vertex_map.containsKey(current[0]))
        		text_vertex_map.put(current[0], new Vertex (current[0]));
        	if (!text_vertex_map.containsKey(current[1]))
        		text_vertex_map.put(current[1], new Vertex (current[1]));
        	if (!text_edge_map.containsKey(relation_line))
        	text_edge_map.put(relation_line,new Edge(current[2],text_vertex_map.get(current[0]),text_vertex_map.get(current[1]),1));
        	if (!text_edge_map.containsKey(relation_rev))
        	text_edge_map.put(relation_rev,new Edge(current[2],text_vertex_map.get(current[1]),text_vertex_map.get(current[0]),1));
        }
        nodes = new ArrayList<Vertex>(text_vertex_map.values());
        edges = new ArrayList<Edge>(text_edge_map.values());
        Graph graph = new Graph(nodes,edges);
        Set <String>smaller_graph_nodes = new HashSet <String>();
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        Set<String> ver_intersection = new HashSet<String>(sum_vertices_set);
        Set<String> edge_intersection = new HashSet<String>(sum_edges_set);
        edge_intersection.retainAll(text_edge_map.keySet());
        ver_intersection.retainAll(text_vertex_map.keySet());
        Set<String> pair_intersection = new HashSet<String>(sum_pair_map);
        pair_intersection.retainAll(text_pair_map);
      for (String intersec_it: ver_intersection){
       	for (String intersec_it2: ver_intersection){
       		if (!intersec_it.equals(intersec_it2)){
       	    dijkstra.execute(text_vertex_map.get(intersec_it)); // set the source
       	    LinkedList<Vertex> path = dijkstra.getPath(text_vertex_map.get(intersec_it2)); // find the path from the defined source
       	    if (path != null){
       	    assertNotNull(path);
       	    assertTrue(path.size() > 0);
       	    int size = path.size()-1;
       	 //   System.out.println(intersec_it + "\t"+intersec_it2 + "   - >   "+ size);
       	    if (size <= threshold){
       	    for (Vertex vertex : path) {
       	     smaller_graph_nodes.add(vertex.getName());
       	    }
        	}
       	    }
       		}
       	}
        }
      ver_intersection.removeAll(swords);
     double cov_vocab_sum = (double) ver_intersection.size()/ (double)sum_vertices_set.size();
      double cov_vocab_text = (double) ver_intersection.size()/ (double)text_vertex_map.size();
      double graph_cov = (double) smaller_graph_nodes.size()/ (double)text_vertex_map.size();
     double graph_cov_sum = (double) smaller_graph_nodes.size()/ (double)sum_vertices_set.size();
     double structure_cov_sum = (double) edge_intersection.size()/ (double)sum_edges_set.size();
      double structure_cov_text = (double) edge_intersection.size()/ (double)text_edge_map.size();
      double pair_cov_sum = (double) pair_intersection.size()/ (double)sum_pair_map.size();
      double pair_cov_text = (double) pair_intersection.size()/ (double)text_pair_map.size();
      sum_match_scores.put(text_name,cov_vocab_sum*100);
      text_match_scores.put(text_name,cov_vocab_text*100);
     results.write(sum_name+"\t"+text_name+"\t"+cov_vocab_sum*100+"\t"+cov_vocab_text*100+"\t"+structure_cov_sum*100+"\t"+structure_cov_text*100+"\t"+pair_cov_sum*100+"\t"+pair_cov_text*100+"\t"+graph_cov_sum*100+"\t"+graph_cov*100+"\n");                   
    /*
     if (!ver_intersection.isEmpty()){
        text_graph = new Scanner(new File(dir + "answers_graph_structure/graph_TFIDF_"+text_name));
        FileWriter graph_structure=new FileWriter (new File (dir+"dot files/dot_"+sum_name+"_"+text_name+".dot"));
        
       graph_structure.write("digraph G_component_0 {\n");
        while (text_graph.hasNextLine()){
        	String []current = text_graph.nextLine().split("\t");
        	if (smaller_graph_nodes.contains(current[0].toLowerCase()) || smaller_graph_nodes.contains(current[1].toLowerCase() )){
        		if (swords.contains(current[0].toLowerCase()) || swords.contains(current[1].toLowerCase())){
        			if (smaller_graph_nodes.contains(current[0].toLowerCase()) && smaller_graph_nodes.contains(current[1].toLowerCase() )){
        		graph_structure.write("\""+current[0] + "\" -> \"" + current[1] + "\"[label=\"" + current[2]  + "\"];\n");
        			}
        		}
        		else {
        		graph_structure.write("\""+current[0] + "\" -> \"" + current[1] + "\"[label=\"" + current[2]  + "\"];\n");	
        		}
        		}
        }
        graph_structure.write("}");
        graph_structure.close();
       
        } */
        text_graph.close();
	    }
       
        //
/*       Double maxValueInMap=(Collections.max(sum_match_scores.values())); 
       Set <String>values = getMaxValue (sum_match_scores, maxValueInMap);
      results.write(name + "\t" + values+"\t" + values.contains(sum_name) + "\t" + maxValueInMap+ "\n");
      Double maxValueInMapt=(Collections.max(text_match_scores.values())); 
      Set <String>valuest = getMaxValue (text_match_scores, maxValueInMapt);
     resultt.write(name + "\t" + valuest+"\t" + valuest.contains(sum_name) + "\t" + maxValueInMapt+ "\n");*/
       
     //  System.out.println(name + "\t" + values+"\t" + values.contains(name) + "\t" + maxValueInMap+ "\n");
	    }
	    results.close();
	//    resultt.close();
	}
    public static Set<String> getMaxValue (Map <String, Double>m, Double max){
    	Set <String>values = new HashSet <String>();
    	 for (Entry<String, Double> entry : m.entrySet()) {
             if (entry.getValue().equals(max)) {
                 values.add(entry.getKey());
               
             }
         }
    	 return values;
    	
    }

}
