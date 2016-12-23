package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

import writer.SVMWriter;


public class UserGraphFeatures 
{
	static String input;
	static double[] f = new double[1];
	static HashMap<String, vector> embedding_map = new HashMap<>();    //HashMap to store vectors for each word
	public UserGraphFeatures(String inp)
	{
		input = inp;
	}
	
	public void initialize()
	{
		//GraphFeatures(input+"/parsed_files/dev_clean.txt", input+"/topic_files/keywords_rel_com_dev.txt", input+"/svm_files/train/dialog_train.txt", input+"/word2vec_files/vectors_unannotated.txt", input+"/svm_files/dev/user_dev.txt");
		GraphFeatures(input+"/parsed_files/train_clean.txt", input+"/topic_files/keywords_rel_com.txt", input+"/svm_files/train/dialog_train.txt", input+"/word2vec_files/vectors_unannotated.txt", input+"/svm_files/train/user_train.txt");
		GraphFeatures(input+"/parsed_files/test_clean.txt", input+"/topic_files/keywords_rel_com.txt", input+"/svm_files/test/dialog_test.txt", input+"/word2vec_files/vectors_unannotated.txt", input+"/svm_files/test/user_test.txt");
	}
	
	public static void GraphFeatures(String input1, String input2, String input3, String input4, String output)
	{
		File file = new File(input1);
		File file_2 = new File(input2);
		File file_3 = new File(input3);
		BufferedReader reader = null;
		BufferedReader reader_2 = null;
		BufferedReader reader_3 = null;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(output, false)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			reader = new BufferedReader(new FileReader(file));
			reader_2 = new BufferedReader(new FileReader(file_2));
			reader_3 = new BufferedReader(new FileReader(file_3));
			embedding_map(input4);
			String line;
			try {
				while((line = reader.readLine()) != null)
				{
					HashMap<String, HashMap<String, Double>> map = new HashMap<>();
					ArrayList<String> id_list = new ArrayList<>();
					ArrayList<String> comments = new ArrayList<>();
					ArrayList<String> usernames = new ArrayList<>();
					String[] splited = line.split("\\s+", 3);
					String qid = splited[0];
					String user = splited[1];
					id_list.add(qid);
					String username = splited[2];
					String question = reader.readLine();
					usernames.add(username);
					String kque = reader_2.readLine();
					comments.add(kque);
					for(int i=0; i<10; i++)
					{
						String cid = reader.readLine();
						splited = cid.split("\\s+", 4);
						String comment = reader.readLine();
						int exp = name_dialog(usernames, comment);
						usernames.add(splited[3]);
						String kcom = reader_2.readLine();
						HashMap<String, Double> submap = new HashMap<>();
						for(int j=0; j<id_list.size(); j++)
						{
							double exp_score = 0.0;
							double tr_score = translation(comments.get(j), kcom);
							if(exp == j)
								exp_score = 1.0;
							//writer.println(splited[0]+" "+id_list.get(j)+" "+(tr_score+exp_score));
							submap.put(id_list.get(j), tr_score+exp_score);
						}
						map.put(splited[0], submap);
						//f[0] = submap.get(qid);
						double maxValueInMap=(Collections.max(submap.values()));  // This will return max value in the Hashmap
				        for (Entry<String, Double> entry : submap.entrySet()) {  // Iterate through hashmap
				            if (entry.getValue() == maxValueInMap) {
				                System.out.println(entry.getKey()+" "+entry.getValue());     // Print the key with max value
				                if(entry.getKey().equals(qid))
				                	f[0] = 1.0;
				                else
				                	f[0] = 0.0;
				                break;
				            }
				        }
						id_list.add(splited[0]);
						comments.add(kcom);
						SVMWriter w = new SVMWriter(writer, splited[1], 1, f);
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
	
	public static double translation(String aux, String main)
	{
		String[] aux_split = aux.split(",\\s+");
		String[] main_split = main.split(",\\s+");
		ArrayList<String> aux_words = new ArrayList<>();
		ArrayList<String> main_words = new ArrayList<>();
		double score = 0.0;
		if(main_split[0].length() > 0 && aux_split[0].length() > 0)
		{
			for(String word:aux_split)
			{
				String[] aux_get = word.substring(0, word.lastIndexOf(" ")).split("\\s+");
				for(String words:aux_get)
				{
					aux_words.add(words);
				}
				
			}
			for(String word:main_split)
			{
				String[] main_get = word.substring(0, word.lastIndexOf(" ")).split("\\s+");
				for(String words:main_get)
				{
					main_words.add(words);
				}
				
			}
			double[] cos = new double[main_words.size()];
			int i=0;
			for(String main_word: main_words)
			{
				vector v1 = embedding_map.get(main_word);
				double max_cos = 0.0;
				for(String aux_word: aux_words)
				{
					vector v2 = embedding_map.get(aux_word);
					if(v1 != null && v2 != null)
					{
						if(vector_cos(v1, v2) > max_cos)
						{
							max_cos = vector_cos(v1, v2);
						}
					}
				}
				cos[i] = max_cos;
				i++;
			}
			for(double d: cos)
			{
				score += d;
			}
		}
		if(main_words.size() > 0)
			return score/main_words.size();
		return 0.0;
	}
	
	
	
	public static void embedding_map(String input)
	{
		File file = new File(input);
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String l;
			try {
				
				while((l = reader.readLine())!= null)
				{
					String splited[] = l.split("\\s+", 2);
					String word = splited[0];
					vector v = new vector(splited[1]);
					embedding_map.put(word, v);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static int name_dialog(ArrayList<String> usernames, String comment)  //find users in dialogue by name
	{
		int val = -1;
		String[] spl = comment.split("\\s+");
		for(int i=0; i<usernames.size(); i++)
		{
			String[] splited;
			if(usernames.get(i).contains("-") || usernames.get(i).contains("_") || usernames.get(i).contains(" "))
				splited = usernames.get(i).split("[-_\\s+]");
			else
				splited = usernames.get(i).split("((?<=[a-zA-Z])(?=[0-9]))|((?<=[0-9])(?=[a-zA-Z]))");
			for(int j=0; j<splited.length; j++)
			{
				System.out.println(splited[j]);
			}
			for(String words: spl)
			{
				for(String phrases: splited)
				{
					if(words.equalsIgnoreCase(phrases))
					{
						val = i;
						break;
					}
				}
				if(val >= 0)
					break;
				if(words.equalsIgnoreCase(usernames.get(i)))
				{
					val = i;
					break;
				}
			}
			if(val >= 0)
				break;
		}
		return val;
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
}
