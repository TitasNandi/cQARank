package core;
import java.io.File;

import core.DialogueFeatures;
import core.EmbeddingTrainer;
import core.MetaFeatures;
import writer.Writer;
import reader.MultiFileReader;
import writer.EmbeddingWriter;
import writer.ThresholdFixer;
import writer.TopicWriter;
import reader.XmlReader;
public class QAMain 
{
	public static void main(String[] args)
	{
		int num_features = 156;
		String resource_path = args[1]+"/scripts/";                               //path to resources directory
		if(args.length == 2)
		{
			parsed_files(args[0], 0);
			get_clean_files(get_parent(args[0])+"/parsed_files/", resource_path);
			string_similarity(get_parent(args[0])+"/parsed_files/");
			//dialogue_features(get_parent(args[0])+"/parsed_files/",get_parent(args[0])+"/svm_files/");
			embedding_trainer(args[0], resource_path);
			topic_file_creator(get_parent(args[0]), resource_path);
			topic_trainer(get_parent(args[0])+"/topic_files/", resource_path);
			topic_writer(get_parent(args[0]), get_parent(args[0])+"/svm_files/");
			embedding_writer(get_parent(args[0])+"/word2vec_files/", get_parent(args[0])+"/svm_files/");
			//thread_testing(get_parent(args[0])+"/parsed_files/",get_parent(args[0])+"/svm_files/");
			meta_features(get_parent(args[0])+"/parsed_files/",get_parent(args[0])+"/svm_files/");				//metadata features computation	
			keyword_generator(get_parent(args[0]), resource_path);
			//dependancy_features(get_parent(args[0]));
		}
		else
		{
			stacking_features(get_parent(args[0]), args[2], args[3]);
			num_features = 176;
		}
		multi_file_reader(get_parent(args[0])+"/svm_files/", get_parent(args[0])+"/parsed_files/", num_features);
		run_svm(get_parent(args[0])+"/svm_files/", get_parent(resource_path), 6, args.length);
		//threshold_fixer(get_parent(args[0])+"/result_files/");
		compute_scorer(get_parent(args[0]), resource_path);
		writer(get_parent(args[0]));
		get_scores(get_parent(args[0])+"/result_files/", resource_path);
	}
	/**
     * This method runs LIBLinear 
     * @param inp: The input SVM files directory
     * @param resource_path: The path to resources folder
     */
    public static void run_svm(String inp, String resource_path, int param, int num_args)
    {
    	System.out.println("SVM computation starts......");
    	System.out.println("SVM parameters: -s "+param+" (L2-regularized logistic regression)");
    	File inputFile = new File(inp);
    	File parent = inputFile.getParentFile();
    	String pathgp = parent.getAbsolutePath();
    	File dir = new File(pathgp+"/result_files/");
    	boolean success = dir.mkdirs();
    	dir.setExecutable(true);
    	dir.setReadable(true);
    	dir.setWritable(true);
    	try {
    		ProcessBuilder builder = new ProcessBuilder("java","-cp",resource_path+"/lib/liblinear-java-1.95.jar","de.bwaldvogel.liblinear.Train","-s",param+"",inp+"/train/SVM_train.txt");
    		builder.directory(new File(inp+"train/"));
    		Process p = builder.start();
    		p.waitFor();
    		ProcessBuilder builder2 = new ProcessBuilder("java","-cp",resource_path+"/lib/liblinear-java-1.95.jar","de.bwaldvogel.liblinear.Predict","-b","1",inp+"/train/SVM_train.txt", inp+"/train/SVM_train.txt.model", pathgp+"/result_files/out_train.txt");
    		Process p2 = builder2.start();
    		p2.waitFor();
    		ProcessBuilder builder3 = new ProcessBuilder("java","-cp",resource_path+"/lib/liblinear-java-1.95.jar","de.bwaldvogel.liblinear.Predict","-b","1",inp+"/test/SVM_test.txt", inp+"/train/SVM_train.txt.model", pathgp+"/result_files/out_test.txt");
    		Process p3 = builder3.start();
    		p3.waitFor();
    		if(num_args == 2)
    		{
	    		ProcessBuilder builder4 = new ProcessBuilder("rm","-r",inp+"/train/SVM_train.txt");
	    		Process p4 = builder4.start();
	    		p4.waitFor();
	    		ProcessBuilder builder5 = new ProcessBuilder("rm","-r",inp+"/train/SVM_train.txt.model");
	    		Process p5 = builder5.start();
	    		p5.waitFor();
	    		ProcessBuilder builder6 = new ProcessBuilder("rm","-r",inp+"/test/SVM_test.txt");
	    		Process p6 = builder6.start();
	    		p6.waitFor();
    		}
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * This method computes scorer script for MAP computation
     * @param inp: The input directory
     * @param resource_path: The path to resources folder
     */
    public static void compute_scorer(String inp, String resource_path)
    {
    	System.out.println("Computing scorer scripts......");
    	try {
    			Process p = (new ProcessBuilder("python",resource_path+"scorer_format.py",inp+"/parsed_files/test_clean.txt",inp+"/result_files/scores_gold.txt")).start();
    			p.waitFor();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    }
    public static void topic_file_creator(String inp, String resource_path)
    {
    	//System.out.println("Computing scorer scripts......");
    	File inputFile = new File(inp);
    	String pathgp = inputFile.getAbsolutePath();
    	File dir = new File(pathgp+"/topic_files/");
    	boolean success = dir.mkdirs();
    	try {
    		Process p = (new ProcessBuilder("python",resource_path+"file_writer.py",inp+"/parsed_files/train_clean.txt",inp+"/topic_files/topic_train.txt")).start();
    		p.waitFor();
    		Process p2 = (new ProcessBuilder("python",resource_path+"file_writer.py",inp+"/parsed_files/test_clean.txt",inp+"/topic_files/topic_test.txt")).start();
    		p2.waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void keyword_generator(String inp, String resource_path)
    {
    	System.out.println("Computing keyword features......");
    	try {
			Process p = (new ProcessBuilder("python",resource_path+"keyword_extractor.py",inp+"/topic_files/topic_train.txt",inp+"/topic_files/keywords_train.txt",resource_path+"SmartStoplist.txt")).start();
			p.waitFor();
			Process p2 = (new ProcessBuilder("python",resource_path+"keyword_writer.py",inp+"/topic_files/keywords_train.txt",inp+"/parsed_files/train_clean.txt", inp+"/svm_files/train/keyword_train.txt")).start();
			p2.waitFor();
			Process p3 = (new ProcessBuilder("python",resource_path+"keyword_extractor.py",inp+"/topic_files/topic_test.txt",inp+"/topic_files/keywords_test.txt",resource_path+"SmartStoplist.txt")).start();
			p3.waitFor();
			Process p4 = (new ProcessBuilder("python",resource_path+"keyword_writer.py",inp+"/topic_files/keywords_test.txt",inp+"/parsed_files/test_clean.txt", inp+"/svm_files/test/keyword_test.txt")).start();
			p4.waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void parsed_files(String inp, int flag)
    {
    	System.out.println("Loading XML data......");
    	XmlReader xml = new XmlReader(inp, flag);
    	xml.initialize();	
    }
    public static void string_similarity(String inp)
    {
    	StringSimilarity g = new StringSimilarity(inp);
    	g.initialize();
    }
    public static void topic_writer(String inp, String out)
    {
    	TopicWriter t = new TopicWriter(inp, out);
    	t.initialize();
    }
    public static void dialogue_features(String inp, String out)
    {
    	DialogueFeatures f = new DialogueFeatures(inp, out);
    	f.initialize();
    }
    public static void embedding_trainer(String inp, String inp2)
    {
    	EmbeddingTrainer e = new EmbeddingTrainer(inp, inp2);
    	e.initialize();
    }
    public static void threshold_fixer(String inp)
    {
    	ThresholdFixer tf = new ThresholdFixer(inp);
    	tf.initialize();
    }
	public static void stacking_features(String inp, String inp2, String inp3)
    {
    	StackingFeatures sf = new StackingFeatures(inp, inp2, inp3);
    	sf.initialize();
    }
	public static void topic_trainer(String inp, String inp2)
    {
    	System.out.println("Topic training started......");
    	TopicModel lda = new TopicModel(inp, inp2);
    	lda.initialize();	
    }
	/**
     * This method computes final scores and writes it to the file results.txt
     * @param inp: The input directory
     * @param resource_path: The path to resources folder
     */
    public static void get_scores(String inp, String resource_path)
    {
    	System.out.println("Scores computation about to end......");
    	ProcessBuilder builder = new ProcessBuilder("python", resource_path+"ev.py", inp+"scores_gold.txt", inp+"results.txt");
    	File outputFile = new File(inp+"final_scores.txt");
    	builder.redirectOutput(outputFile);
    	Process p;
    	try {
			p = builder.start();
			p.waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	/**
     * This method combines several SVM feature files
     * @param inp1: The input directory
     * @param inp2: Another input directory
     */
    public static void multi_file_reader(String inp, String inp2, int num_features)
    {
    	MultiFileReader mfr = new MultiFileReader(inp, inp2, num_features);
    	mfr.initialize();
    }
	/**
     * This method writes embeddings to a file
     * @param inp: The input directory
     * @param out: The output directory
     */
    public static void embedding_writer(String inp, String out)
    {
    	EmbeddingWriter e = new EmbeddingWriter(inp, out);
    	e.initialize();
    }
    /**
     * This method computes metadata features
     * @param inp: The input directory
     * @param out: The output directory
     */
    public static void meta_features(String inp, String out)
    {
    	MetaFeatures m = new MetaFeatures(inp, out);
    	m.initialize();
    }
	public static void get_clean_files(String inp, String resource_path)
    {
		System.out.println("Producing clean data......");
		try {
    		Process p = (new ProcessBuilder("python",resource_path+"check.py",inp+"train.txt",inp+"train_clean.txt")).start();
    		p.waitFor();
    		Process p1 = (new ProcessBuilder("python",resource_path+"check.py",inp+"test.txt",inp+"test_clean.txt")).start();
    		p1.waitFor();
    	//	Process p4 = (new ProcessBuilder("python",resource_path+"check_unannotated.py",inp+"unannotated.txt",inp+"unannotated_clean.txt")).start();
    	//	p4.waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public static String get_parent(String inp)
	{
    	File f = new File(inp);
    	String parent = f.getParent();
    	return parent;
	}
	/**
     * This method computes final scores
     * @param inp: The input directory
     */
    public static void writer(String inp)
    {
    	Writer w = new Writer(inp);
    	w.initialize();
    }
    public static void thread_testing(String inp, String out)
    {
    	ThreadTesting tt = new ThreadTesting(inp, out);
    	tt.initialize();
    }
}
