package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import writer.SVMWriter;
/**
 * This class calculates MetaData features like if asker answered in a comment, comment position or acknowledgement in comments
 * @author titas
 *
 */
public class MetaFeatures                                      //Meta Data Features
{
	
	static String input;
	static String output;
	public MetaFeatures(String inp, String out)
	{
		input = inp;	
		output = out;
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		System.out.println("MetaData Features computation starts......");
		MetaFeaturesRun(input+"/train_clean.txt",output+"/train/meta_train.txt");
		//MetaFeaturesRun(input+"/dev_clean.txt", output+"/dev/meta_dev.txt");
		MetaFeaturesRun(input+"/test_clean.txt", output+"/test/meta_test.txt");
	}
	public static void MetaFeaturesRun(String input, String output1)
	{
		double[] f = new double[6];
		File file = new File(input);
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(output1, false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
			try {
				reader = new BufferedReader(new FileReader(file));
				String line;
				String[] ack = {"thankyou","thanks","thank","thanku"};          //Acknowledgement words
				String[] ack2 = {"appreciate", "appreciated"};
				try {
					while((line = reader.readLine()) != null)
					{
						String splited[] = line.split("\\s+", 4);
						int num = Integer.parseInt(splited[1]);
						String quser = splited[2].toLowerCase();
						//String qusername = splited[3].toLowerCase();
						line = reader.readLine();
						for(int i=0; i<num; i++)
						{
							line = reader.readLine();
							splited = line.split("\\s+", 4);
							String cuser = splited[2].toLowerCase();
							String label = splited[1];
							String comment = reader.readLine();
							String[] spl = comment.split("\\s+");
							f[0] = (num-i)*1.0/num;
							f[1] = check_same(quser, cuser);
							f[2] = special_word_matcher(ack, comment);
							f[3] = special_word_matcher(ack2, comment);
							f[4] = check_same(spl[0],"yes");
							f[5] = spl.length;
							SVMWriter w = new SVMWriter(writer, label, 1, f);
							w.write();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	/**
	 * This method simply calculates if two strings are same
	 * @param s1: first string
	 * @param s2: second string
	 * @return 1.0 or 0.0
	 */
	public static double check_same(String s1, String s2)
	{
		if(s1.equals(s2))
			return 1.0;
		else
			return 0.0;
	}
	/**
	 * Matches words from a list in a target sentence
	 * @param to_match: List of words to match
	 * @param comment: comment in which we find the matched words
	 * @return number of matches
	 */
	public static double special_word_matcher(String[] to_match, String comment)           //match special words
	{
		String[] str = comment.replaceAll("[^a-zA-Z0-9 ]", " ").toLowerCase().split("\\s+");
		double val = 0.0;
		for(int i=0; i<to_match.length; i++)
		{
			for(String comm : str)
			{
				if(to_match[i].equals(comm))
				{
					val+=0.1;
				}
			}
		}
		return val;
	}
}
