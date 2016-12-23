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
	public EmbeddingTrainer(String inp)
	{
		input = inp;	
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
		EmbeddingTrainerRun(pathgp+"/word2vec_files/vectors_unannotated.txt", pathgp+"/parsed_files/train_clean.txt", pathgp+"/word2vec_files/train_vectors.txt");
		//EmbeddingTrainerRun(pathgp+"/word2vec_files/vectors_unannotated.txt", pathgp+"/parsed_files/dev_clean.txt", pathgp+"/word2vec_files/dev_vectors.txt");
		EmbeddingTrainerRun(pathgp+"/word2vec_files/vectors_unannotated.txt", pathgp+"/parsed_files/test_clean.txt", pathgp+"/word2vec_files/test_vectors.txt");
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
				sentence_vector(map, input2, output1);          // generate sentence vectors from word vectors
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
				for(int j=0; j<100; j++)
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
	/**
	 * This method finds the cosine of two given vectors
	 * @param v1: question vector
	 * @param v2: comment vector
	 * @return The cosine value
	 */
	public static double vector_cos(vector v1, vector v2)                          //cosine score of vectors
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
	public static double vector_dot(vector v1, vector v2)                           //vector dot product
	{
		double sum = 0.0;
		for(int i=0; i<v1.vec.length; i++)
		{
			sum += v1.vec[i] * v2.vec[i];
		}
		return sum;
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
}