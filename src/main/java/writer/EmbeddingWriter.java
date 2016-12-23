package writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * This class calculates embedding features from sentence vectors like cosine similarity, manhattan and euclidean distance of embeddings 
 * @author titas
 *
 */
public class EmbeddingWriter 
{
	static double[] f = new double[103];
	static String input;
	static String output;
	public EmbeddingWriter(String inp, String out)
	{
		input = inp;	
		output = out;
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		System.out.println("Writing word embeddings to files......");
		//EmbeddingWriterRun(input+"/train_vectors.txt", output+"/train/embedding_train.txt");
		//EmbeddingWriterRun(input+"/dev_vectors.txt", output+"/dev/embedding_dev.txt");
		//EmbeddingWriterRun(input+"/test_vectors.txt", output+"/test/embedding_test.txt");
		EmbeddingWriterRun(input+"/train_idf_vectors.txt", output+"/train/idfembedding_train.txt");
		EmbeddingWriterRun(input+"/dev_idf_vectors.txt", output+"/dev/idfembedding_dev.txt");
	}
	public static void EmbeddingWriterRun(String input, String output)
	{
		File file = new File(input);
		BufferedReader reader = null;
		PrintWriter writer = null;
		double cos = 0.0;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(output, false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			try {
				while((line = reader.readLine()) != null)
				{
					String splited[];
					vector que_vec = null;
					splited= line.split("\\s+", 2);
					que_vec = new vector(splited[1]);
					for(int i=0; i<10; i++)
					{
						line = reader.readLine();
						splited = line.split("\\s+", 3);
						String c_id = splited[0];
						String rlabel = splited[1];
						vector ans_vec = new vector(splited[2]);
						f[0] = que_vec.vector_cos(que_vec, ans_vec);                           // Cosine similarity of embedding vectors
						f[1] = que_vec.vec_manhattan(que_vec, ans_vec);                        // Manhattan distance of embedding vectors
						f[2] = que_vec.Euclidean(que_vec, ans_vec);                            // Euclidean distance of embedding vectors
						double[] sub = que_vec.vector_sub(que_vec, ans_vec);                   // vector subtraction of embedding vectors
						for(int j=0; j<sub.length; j++)
						{
							f[j+3] = sub[j];
						}
						SVMWriter w = new SVMWriter(writer, rlabel, 1, f);
						w.write();
					}
				}
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
/**
 * This class does all the vector operations on these embedding vectors like cosine, dot product, vector subtraction, and various distance metrics
 * @author titas
 *
 */
class vector                                //find scoring vectors and cosine of question and comment vectors
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
	public double[] vector_sub(vector v1, vector v2)
	{
		double[] sub = new double[v1.vec.length];
		for(int i=0; i<v1.vec.length; i++)
		{
			sub[i] = v1.vec[i] - v2.vec[i];
		}
		return sub;
	}
	public double[] vector_mul(vector v1, vector v2)
	{
		double[] mul = new double[v1.vec.length];
		for(int i=0; i<v1.vec.length; i++)
		{
			mul[i] = v1.vec[i] * v2.vec[i];
		}
		return mul;
	}
	public double vector_cos(vector v1, vector v2)
	{
		double cos = vector_dot(v1, v2);
		if(cos != 0.0)
			cos = (vector_dot(v1, v2))/(Math.sqrt(vector_dot(v1, v1) * vector_dot(v2, v2)));
		return cos;
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
	public double vector_dot(vector v1, vector v2)
	{
		double sum = 0.0;
		for(int i=0; i<v1.vec.length; i++)
		{
			sum += v1.vec[i] * v2.vec[i];
		}
		return sum;
	}
}