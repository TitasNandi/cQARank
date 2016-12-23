package core;

import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class TopicModel {
	static String input;
	static String input2;
	public TopicModel(String inp, String inp2)
	{
		input = inp;
		input2 = inp2;
	}
	/**
	 * This method initializes computation
	 */
    public static void initialize()
    {
    	TopicModelRun(input, input2);
    }
    public static void TopicModelRun(String input, String input2)
    {
        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File(input2+"/en.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

        BufferedReader fileReader;
		try {
			fileReader = new BufferedReader(new FileReader(new File(input+"/topic_train.txt")));
	        String line;
	        ArrayList<String> ids = new ArrayList<>();
	        while((line = fileReader.readLine()) != null)
	        {
	        	//System.out.println(line);
	        	String[] spl = line.split("\t");
	        	if(spl.length == 1)
	        	{
	        		instances.addThruPipe(new Instance("", null, spl[0], null));
		        	ids.add(spl[0]);
		        	continue;
	        	}
	        	instances.addThruPipe(new Instance(spl[1], null, spl[0], null));
	        	ids.add(spl[0]);
	        }
	        //  Note that the first parameter is passed as the sum over topics, while
	        //  the second is the parameter for a single dimension of the Dirichlet prior.
	        int numTopics = 20;
	        ParallelTopicModel model = new ParallelTopicModel(numTopics);
	
	        model.addInstances(instances);
	
	        // Use two parallel samplers, which each look at one half the corpus and combine
	        //  statistics after every iteration.
	        model.setNumThreads(2);
	
	        // Run the model for 50 iterations and stop (this is for testing only, 
	        //  for real applications, use 1000 to 2000 iterations)
	        model.setNumIterations(1000);
	        model.estimate();
	
	        // Show the words and topics in the first instance
	        
	        // Estimate the topic distribution of the first instance, 
	        //  given the current Gibbs state.
	        File keys = new File(input+"/top_words.txt");
	        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(input+"/vectors.txt", false)));
	        File infer = new File(input+"/dev_vectors.txt");
	        
	        System.out.println(model.getData().size());
	        
	        model.printTopWords(keys, 50, false);
	        writer.println("Topic Proportions:");
	        for(int j=0; j<model.getData().size(); j++)
	        {
	        	writer.print(j+"\t"+ids.get(j)+"\t");
		        double[] topicDistribution = model.getTopicProbabilities(j);
		        for(int i=0; i<topicDistribution.length; i++)
		        {
		        	writer.print(topicDistribution[i]+"\t");
		        }
		        writer.println();
	        }
	        writer.close();
	        // Create a new instance named "test instance" with empty target and source fields.
	        InstanceList testing = new InstanceList(instances.getPipe());
	        BufferedReader fileReader2 = new BufferedReader(new FileReader(new File(input+"/topic_dev.txt")));
	        TopicInferencer inferencer = model.getInferencer();
	        while((line = fileReader2.readLine()) != null)
	        {
	        	//System.out.println(line);
	        	String[] spl = line.split("\t");
	        	if(spl.length == 1)
	        	{
	        		testing.addThruPipe(new Instance("", null, spl[0], null));
		        	continue;
	        	}
	        	testing.addThruPipe(new Instance(spl[1], null, spl[0], null));
	        }
	        inferencer.writeInferredDistributions(testing, infer, 100, 10, 10, 0.0, 20);
        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

}

