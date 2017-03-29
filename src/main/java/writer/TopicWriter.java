package writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
/**
 * This class calculates various features from topic vectors and words indicating topic classes
 * @author titas
 *
 */
public class TopicWriter 
{
	static double[] f = new double[27]; 
	static double[] weights = new double[20];
	static String[] topic_words = new String[20];
	static String input;
	static String output;
	public TopicWriter(String inp, String out)
	{
		input = inp;	
		output = out;
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		System.out.println("Computing topic features......");
		TopicWriterRun(input+"/parsed_files/train_clean.txt", input+"/topic_files/train_vectors.txt", input+"/topic_files/top_words.txt", output+"/train/topic_train.txt");
		TopicWriterRun(input+"/parsed_files/test_clean.txt", input+"/topic_files/test_vectors.txt", input+"/topic_files/top_words.txt", output+"/test/topic_test.txt");
	}
	public static void TopicWriterRun(String input, String input1, String input2, String output)
	{
		File file = new File(input);
		File file2 = new File(input1);
		File file3 = new File(input2);
		BufferedReader reader = null;
		BufferedReader reader2 = null;
		BufferedReader reader3 = null;
		String line;
		int k=0;
		try {
			reader3 = new BufferedReader(new FileReader(file3));
			try {
				while((line = reader3.readLine()) != null)
				{
					String[] spl = line.split("\t", 3);
					weights[k] = Double.parseDouble(spl[1]);
					topic_words[k] = spl[2];
					k++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(output, false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader = new BufferedReader(new FileReader(file));
			reader2 = new BufferedReader(new FileReader(file2));
			
			try {
				line = reader2.readLine();
				while((line = reader.readLine()) != null)
				{
					line = reader2.readLine();
					String[] spl = line.split("\t", 3);
					topic_vector que_vec = new topic_vector(spl[2]);
					double posq = findKthLargest(que_vec.vec, 10);
					int maxq = get_topmax(que_vec.vec);
					String words = "";
					String non = "";
					for(int i=0; i<que_vec.vec.length; i++)
					{
						if(que_vec.vec[i] >= posq)
						{
							words += topic_words[i]+" ";
						}
						else
						{
							non += topic_words[i]+" ";
						}
					}
					String l = deDup(words);
					String l2 = deDup(non);
					String question = reader.readLine();
					for(int i=0; i<100; i++)
					{
						line = reader.readLine();
						String[] splited = line.split("\\s+");
						String c_id = splited[0];
						String label = splited[1];
						String comment = reader.readLine();
						line = reader2.readLine();
						spl = line.split("\t", 3);
						topic_vector ans_vec = new topic_vector(spl[2]);
//						if(ans_vec.vec.length < 20)
//						{
//							spl = line.split("\t", 2);
//							ans_vec = new topic_vector(spl[1]);
//						}
						int maxc = get_topmax(ans_vec.vec);
						double posa = findKthLargest(ans_vec.vec, 10);
						words = "";
						non = "";
						for(int j=0; j<ans_vec.vec.length; j++)
						{
							if(ans_vec.vec[j] >= posa)
							{
								words += topic_words[j]+" ";
							}
							else
							{
								non += topic_words[j]+" ";
							}
						}
						String al = deDup(words);
						String al2 = deDup(non);
						System.out.println(spl[0]+" "+ans_vec.vec.length);
						f[0] = que_vec.vector_cos(que_vec, ans_vec);                         //cosine similarity of topic vectors                    
						f[1] = que_vec.vec_manhattan(que_vec, ans_vec);                      //manhattan distance of topic vectors
						f[2] = que_vec.Euclidean(que_vec, ans_vec);                          //euclidean distance of topic vectors
						f[3] = word_matcher(l, comment);
						f[4] = word_matcher(l2, comment);
						f[5] = word_matcher(l,al);
						f[6] = word_matcher(l2,al2);
						double[] sub = que_vec.vector_sub(que_vec, ans_vec);
						for(int x=0; x<sub.length; x++)
						{
							f[x+7] = sub[x];
						}
						SVMWriter w = new SVMWriter(writer, label, 1, f);
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
	/**
	 * This method removes duplicates from a string
	 * @param s: input string
	 * @return the string with distinct words
	 */
	public static String deDup(String s) 
	{
	    return Arrays.stream(s.split("\\s+")).distinct().collect(Collectors.joining(" "));
	}
	public static int get_topmax(double[] vec)
	{
		double max = 0.0;
		int pos = 0;
		for(int i=0; i<vec.length; i++)
		{
			if(vec[i] > max)
			{
				max = vec[i];
				pos = i;
			}
		}
		return pos;
	}
	/**
	 * This method finds word matchings in two strings
	 * @param s1: first string
	 * @param s2: second string
	 * @return the number of matches
	 */
	public static double word_matcher(String s1, String s2)
	{
		double val=0.0;
		String[] sp1 = s1.split("\\s+");
		String[] sp2 = s2.split("\\s+");
		for(String words: sp1)
		{
			for(String top: sp2)
			{
				if(words.equals(top))
				{
					val += 1.0;
				}
			}
		}
		return val;
	}
	/**
	 * Find the kth largest number in a list
	 * @param nums: the input array
	 * @param k: the number k
	 * @return the kth largest number
	 */
	public static double findKthLargest(double[] nums, int k) {
	    PriorityQueue<Double> q = new PriorityQueue<Double>(k);
	    for(double i: nums){
	        q.offer(i);
	 
	        if(q.size()>k){
	            q.poll();
	        }
	    }
	 
	    return q.peek();
	}
}

class topic_vector                                //find scoring vectors and cosine of question and comment vectors
{
	double[] vec;
	public topic_vector(String s)
	{
		String[] str = s.split("\t");
		vec = new double[str.length];
		for(int i=0; i<str.length; i++)
		{
			if(str[i].length() != 0)
				vec[i] = Double.parseDouble(str[i]);
		}
	}
	public double[] vector_sub(topic_vector v1, topic_vector v2)
	{
		double[] sub = new double[v1.vec.length];
		for(int i=0; i<v1.vec.length; i++)
		{
			sub[i] = v1.vec[i] - v2.vec[i];
		}
		return sub;
	}
	public double[] vector_mul(topic_vector v1, topic_vector v2)
	{
		double[] mul = new double[v1.vec.length];
		for(int i=0; i<v1.vec.length; i++)
		{
			mul[i] = v1.vec[i] * v2.vec[i];
		}
		return mul;
	}
	public double vector_cos(topic_vector v1, topic_vector v2)
	{
		double cos = vector_dot(v1, v2);
		if(cos != 0.0)
			cos = (vector_dot(v1, v2))/(Math.sqrt(vector_dot(v1, v1) * vector_dot(v2, v2)));
		return cos;
	}
	public double vec_manhattan(topic_vector v1, topic_vector v2)
	{
		double sum = 0.0;
		for(int i=0; i<v1.vec.length; i++)
		{
			sum+= Math.abs(v1.vec[i] - v2.vec[i]);
		}
		return sum;
	}
	public double Euclidean(topic_vector v1, topic_vector v2)
	{
		double result = 0.0;
		for(int i=0; i<v1.vec.length; i++)
		{
			result+= (v1.vec[i] - v2.vec[i])*(v1.vec[i] - v2.vec[i]);
		}
		return Math.sqrt(result);
	}
	public double vector_dot(topic_vector v1, topic_vector v2)
	{
		double sum = 0.0;
		for(int i=0; i<v1.vec.length; i++)
		{
			sum += v1.vec[i] * v2.vec[i];
		}
		return sum;
	}
}
