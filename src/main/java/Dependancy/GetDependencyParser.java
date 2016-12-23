package Dependancy;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Demonstrates how to first use the tagger, then use the NN dependency parser.
 * Note that the parser will not work on untagged text.
 *
 * @author Jon Gauthier
 */
public class GetDependencyParser {

    static String modelPath = DependencyParser.DEFAULT_MODEL;
    static MaxentTagger tagger;
    static DependencyParser parser;
    static String taggerPath = "/mnt/Titas/1_QA_MODEL/BTP1-2016/TaskA/TaskA-Data/models/english-left3words-distsim.tagger";

    public static void loadDependencyParser() {
        tagger = new MaxentTagger(taggerPath);
        parser = DependencyParser.loadFromModelFile(modelPath);
    }

    public static void main(String[] args) {
        loadDependencyParser();
        String text = "I can almost always tell when movies use fake dinosaurs.";
        ArrayList<String> dp = getDP(text);
        Iterator itr = dp.iterator();//getting Iterator from arraylist to traverse elements  
            while (itr.hasNext()) {
                System.out.println(itr.next().toString());
            }
        
    }

    public static ArrayList<String> getDP(String text) {
        ArrayList<String> ll = new ArrayList<String>();
         ArrayList<String> al = new ArrayList<String>();
         DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
        for (List<HasWord> sentence : tokenizer) {
            List<TaggedWord> tagged = tagger.tagSentence(sentence);
            GrammaticalStructure gs = parser.predict(tagged);
            Collection tdl = gs.typedDependenciesCollapsed();
            ll = (ArrayList<String>) tdl;
            String dp = tdl.toString();
            Iterator itr = ll.iterator();//getting Iterator from arraylist to traverse elements  
            while (itr.hasNext()) {
                String dr=itr.next().toString();
                String[] split = dr.split(" ");
                String rel=split[0].split("\\(")[0];
                String gov1=split[0].split("\\(")[1];
                String gov=gov1.substring(0,gov1.length()-1).split("-")[0];
                String dep=split[1].substring(0,split[1].length()-1).split("-")[0];
                al.add(rel+"\t"+gov+"\t"+dep);
            }
           
        }
        return al;

    }
}
