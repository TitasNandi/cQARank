package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class creates sentence vectors from trained word embeddings by Word2Vec
 * @author titas
 *
 */
public class EmbeddingTrainer 
{
	static int size = 100;              //word vector dimension
	static String input;
	static String input2;
	public EmbeddingTrainer(String inp, String inp2)
	{
		input = inp;	
		input2 = inp2;
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		System.out.println("Sentence Embedding Training starts......");
		File inputFile = new File(input);
		File parent = inputFile.getParentFile();                        //get parent directory
		String pathgp = parent.getAbsolutePath();
		File dir = new File(pathgp+"/word2vec_files/");
		boolean success = dir.mkdirs();
		dir.setExecutable(true);
		dir.setReadable(true);
		dir.setWritable(true);
		EmbeddingTrainerRun(input2+"/vectors_unannotated.txt", pathgp+"/parsed_files/train_clean.txt", pathgp+"/word2vec_files/train_vectors.txt");
		//EmbeddingTrainerRun(pathgp+"/word2vec_files/vectors_unannotated.txt", pathgp+"/parsed_files/dev_clean.txt", pathgp+"/Dependency_files/dev/wtfidf/", pathgp+"/word2vec_files/dev_idf_vectors.txt");
		EmbeddingTrainerRun(input2+"/vectors_unannotated.txt", pathgp+"/parsed_files/test_clean.txt", pathgp+"/word2vec_files/test_vectors.txt");
	}
	public static void EmbeddingTrainerRun(String input1, String input2, String output1)
	{
		File file = new File(input1);
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String l;
			try {
				HashMap<String, vector> map = new HashMap<>();    //HashMap to store vectors for each word
				while((l = reader.readLine())!= null)
				{
					String splited[] = l.split("\\s+", 2);
					String word = splited[0];
					vector v = new vector(splited[1]);
					map.put(word, v);
				}
				sentence_vector(map, input2, output1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	/**
	 * This class creates sentence vectors from word vectors by averaging word vectors per sentence
	 * @param map: HashMap of word and its word vector
	 * @param fread: input file to read
	 * @param fwrite: file to write
	 */
	public static void sentence_vector(HashMap<String, vector> map, String fread, String fwrite1)
	{
		File file = new File(fread);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(fwrite1, false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while(true)
			{
				String qid = reader.readLine();
				if(qid == null)
				{
					break;
				}
				String rquestion = reader.readLine();
				String[] str = rquestion.split("\\s+");
				String[] splited = qid.split("\\s+");
				String q_id = splited[0];
				System.out.println(q_id);
				calculate_avg(writer, str, map, q_id);
				for(int j=0; j<10; j++)
				{
					String cid = reader.readLine();
					splited = cid.split("\\s+");
					String c_id = splited[0];
					String rlabel = splited[1];
					String comment = reader.readLine();
					str = comment.split("\\s+");
					calculate_avg(writer, str, map, c_id+" "+rlabel);
				}
			}
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void calculate_avg(PrintWriter writer, String[] str, HashMap<String, vector> map, String init)         //calculate average of word vectors
	{
		writer.print(init+" ");
		int count = 0;
		double[] vec = new double[size];
		for(int i=0; i<vec.length; i++)
		{
			vec[i] = 0.0;
		}
		for(int i=0; i<str.length; i++)
		{
			vector v = map.get(str[i]);
			if( v!= null )
			{
				count++;
				
				for(int j=0; j<v.vec.length; j++)
				{
					vec[j]+=v.vec[j];
				}
			}
		}
		
		for(int i=0; i<vec.length; i++)
		{
			if(count != 0)
				vec[i]/= count;
			writer.print(vec[i]+" ");
		}
		writer.println();
	}
	public static void calculate_weighted_avg(PrintWriter writer, String[] str, HashMap<String, vector> map, HashMap<String, Double> idf_map, String init)         //calculate average of word vectors
	{
		writer.print(init+" ");
		double weight_count = 0.0;
		double[] vec = new double[size];
		for(int i=0; i<vec.length; i++)
		{
			vec[i] = 0.0;
		}
		for(int i=0; i<str.length; i++)
		{
			String word = str[i].replaceAll("\\s*\\p{Punct}+\\s*$", "");
			vector v = map.get(word);
			if( v!= null && idf_map.get(word) != null)
			{
				double weight = idf_map.get(word);
				System.out.println(word+" "+weight);
				weight_count += weight;
				
				for(int j=0; j<v.vec.length; j++)
				{
					vec[j]+=(v.vec[j]*weight);
				}
			}
		}
		System.out.println();
		for(int i=0; i<vec.length; i++)
		{
			if(weight_count != 0.0)
				vec[i]/= weight_count;
			writer.print(vec[i]+" ");
		}
		writer.println();
	}
	public static void weighted_average(HashMap<String, vector> map, String fread, String idf_dir, String fwrite1)
	{
		File file = new File(fread);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(fwrite1, false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while(true)
			{
				String qid = reader.readLine();
				if(qid == null)
				{
					break;
				}
				String rquestion = reader.readLine();
				String[] str = rquestion.split("\\s+");
				String[] splited = qid.split("\\s+");
				String q_id = splited[0];
				System.out.println(q_id);
				//System.out.println(listOfFiles[file_index].toString());
				HashMap<String, Double> idf_map_que = get_idf_map(idf_dir+"TFIDF_"+q_id+".txt");
				for(String key: idf_map_que.keySet())
				{
					System.out.println(key+" "+idf_map_que.get(key));
				}
				System.out.println();
				calculate_weighted_avg(writer, str, map, idf_map_que, q_id);
				for(int j=0; j<10; j++)
				{
					String cid = reader.readLine();
					splited = cid.split("\\s+");
					String c_id = splited[0];
					String rlabel = splited[1];
					String comment = reader.readLine();
					str = comment.split("\\s+");
					//System.out.println(listOfFiles[file_index].toString());
					HashMap<String, Double> idf_map_com = get_idf_map(idf_dir+"TFIDF_"+c_id+".txt");
					calculate_weighted_avg(writer, str, map, idf_map_com, c_id+" "+rlabel);
				}
			}
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public static HashMap<String, Double> get_idf_map(String filename)
	{
		File file = new File(filename);
		HashMap<String, Double> idf_map = new HashMap<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null)
			{
				String[] spl = line.split("\t");
				idf_map.put(spl[0], Double.parseDouble(spl[1]));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idf_map;
	}
}
/**
 * This class creates a vector object from word embedding values
 * @author titas
 *
 */
class vector                                                                 //vector class
{
	double[] vec;
	public vector(String s)
	{
		String[] str = s.split("\\s+");
		vec = new double[str.length];
		for(int i=0; i<str.length; i++)
		{
			if(str[i].length() != 0)
				vec[i] = Double.parseDouble(str[i]);
		}
	}
	public vector(double[] inp_arr) {
		vec = inp_arr;
	}
	/**
	 * This method finds the cosine of two given vectors
	 * @param v1: question vector
	 * @param v2: comment vector
	 * @return The cosine value
	 */
	public double vector_cos(vector v1, vector v2)                          //cosine score of vectors
	{
		double cos = vector_dot(v1, v2);
		if(cos != 0.0)
			cos = (vector_dot(v1, v2))/(Math.sqrt(vector_dot(v1, v1) * vector_dot(v2, v2)));
		return cos;
	}
	/**
	 * This method calculates the dot product of two vectors
	 * @param v1: question vector
	 * @param v2: comment vector
	 * @return Dot product of vectors
	 */
	public double vector_dot(vector v1, vector v2)                           //vector dot product
	{
		double sum = 0.0;
		for(int i=0; i<v1.vec.length; i++)
		{
			sum += v1.vec[i] * v2.vec[i];
		}
		return sum;
	}
	public double vec_manhattan(vector v1, vector v2)
	{
		double sum = 0.0;
		for(int i=0; i<v1.vec.length; i++)
		{
			sum+= Math.abs(v1.vec[i] - v2.vec[i]);
		}
		return sum;
	}
	public double Euclidean(vector v1, vector v2)
	{
		double result = 0.0;
		for(int i=0; i<v1.vec.length; i++)
		{
			result+= (v1.vec[i] - v2.vec[i])*(v1.vec[i] - v2.vec[i]);
		}
		return Math.sqrt(result);
	}
}