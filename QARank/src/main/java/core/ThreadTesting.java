package core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import writer.SVMWriter;
/**
 * This class calculates many thread level features including presence of URLs, emails or HTML tags, length of comment, punctuations and words typically present in good and bad comments
 * @author titas
 *
 */
public class ThreadTesting
{
	static double[] f = new double[3];
	static String input;
	static String output;
	public ThreadTesting(String inp, String out)
	{
		input = inp;	
		output = out;
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		System.out.println("Thread Features computation starts......");
		ThreadTestingRun(input+"/train.txt", output+"/train/thread_train.txt");
		//ThreadTestingRun(input+"/dev.txt", output+"/dev/thread_dev.txt");
		ThreadTestingRun(input+"/test.txt", output+"/test/thread_test.txt");
		
	}
	public static void ThreadTestingRun(String input, String output)
	{
		File file = new File(input);
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(output, false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader = new BufferedReader(new FileReader(file));
			try {
				String q_id = reader.readLine();
				String[] punc = {"!", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "+", "=", ":", ";", ".", "/", "<", ">", "{", "}", "[", "]", "~", "\\"};									//punctuation list
				do
				{
					String[] qs = q_id.split("\\s+");
					int num = Integer.parseInt(qs[1]);
					String question = reader.readLine();
					for(int i=0; i<num; i++)
					{
						String str = reader.readLine();
						String[] splited = str.split("\\s+");
						String c_id = splited[0];
						String label = splited[1];
						String comment = reader.readLine();
						f[0] = URL_matcher(comment, c_id) + email_matcher(comment, c_id) + tag_matcher(comment, c_id);
						f[1] = special_character_matcher("?", comment) + special_character_matcher("@", comment)+punc_matcher(punc,comment);
						f[2] = length_matcher(comment);
						SVMWriter w = new SVMWriter(writer, label, 1, f);
						w.write();
					}
				}
				while((q_id = reader.readLine())!=null);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method matches URLs if any in the comments
	 * @param comment: comment string
	 * @param c_id: comment ids
	 * @return a value indicating number of URLs
	 */
	public static double URL_matcher(String comment, String c_id)                     //match URLs
	{
		Pattern p = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(comment);
        double val = 0.0;
        while(m.find())
        {
        	val+=0.1;
        }
        return val;
	}
	/**
	 * This method matches emails if any in the comments
	 * @param comment: comment string
	 * @param c_id: comment ids
	 * @return a value indicating number of emails
	 */
	public static double email_matcher(String comment, String c_id)                  //match emails
	{
		Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(comment);
		double val = 0.0;
        while(m.find())
        {
        	val+=0.1;
        }
        return val;
	}
	/**
	 * This method matches HTML tags if any in the comments
	 * @param comment: comment string
	 * @param c_id: comment ids
	 * @return a value indicating number of HTML tags
	 */
	public static double tag_matcher(String comment, String c_id)
	{
		
		Matcher m = Pattern.compile("<[^>]*>").matcher(comment);
		double val = 0.0;
        while(m.find())
        {
        	val+=0.1;
        }
        return val;
	}
	/**
	 * This method matches words from a list in a given target sentence
	 * @param to_match: The list to match 
	 * @param comment: comment string to match with
	 * @return the number of matches
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
	/**
	 * This method matches special characters from a list in a given comment
	 * @param to_match: List to match
	 * @param comment: comment string
	 * @return the number of matches
	 */
	public static double special_character_matcher(String to_match, String comment)       //match special characters
	{
		int count = comment.split(Pattern.quote(to_match), -1).length - 1;
		return 0.1*count;
	}
	/**
	 * This method matches punctuations from a punctuation list in a given comment
	 * @param to_match: list of punctuations to match
	 * @param comment: comment string
	 * @return number of matched punctuations
	 */
	public static double punc_matcher(String[] to_match, String comment)                 //match punctuations
	{
		int count = 0;
		for(String punc: to_match)
		{
			count+= comment.split(Pattern.quote(punc), -1).length - 1;
		}
		return 0.1*count;
	}
	public static int length_matcher(String comment)									//length of comment
	{
		return comment.length();
	}
}
