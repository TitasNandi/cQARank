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

public class DialogueFeatures 
{
	static String input;
	static String output;
	public DialogueFeatures(String inp, String out)
	{
		input = inp;	
		output = out;
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		System.out.println("Dialogue Features computation starts......");
		DialogueFeaturesRun(input+"/train_clean.txt", output+"/train/dialog_train.txt");
		DialogueFeaturesRun(input+"/test_clean.txt", output+"/test/dialog_test.txt");
	}
	public static void DialogueFeaturesRun(String input, String output)
	{
		File file = new File(input);               //input file
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(output, false)));      //output file
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader = new BufferedReader(new FileReader(file));
			try {
				String line;
				while((line = reader.readLine()) != null)
				{
					//String splited[] = line.split("\\s+", 3);
					//String quser = splited[1];
					//String qusername = splited[2].toLowerCase();
					line = reader.readLine();
					for(int j=0; j<10; j++)
					{
						ArrayList<String> users = new ArrayList<>();
						ArrayList<String> labels = new ArrayList<>();
						ArrayList<String> cid = new ArrayList<>();
						ArrayList<String> comments = new ArrayList<>();
						//users.add(qusername);
						for(int i=0; i<10; i++)
						{
							line = reader.readLine();
							String[] splited = line.split("\\s+", 4);
							cid.add(splited[0]);
							labels.add(splited[1]);
							users.add(splited[3].toLowerCase());
							comments.add(reader.readLine());
						}
						ArrayList<Double> f1 = name_dialog(users, comments);
						ArrayList<Double> f2 = dialog(users, cid);
						SVM_writer(writer, labels, f1, f2);
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
	 * This method identifies comments where users explicitly mention another user by username
	 * @param map: ArrayList of usernames in the thread
	 * @param comments: List of comments in the thread
	 * @return Returns an ArrayList of values with 1.0 for comments with this property 
	 */
	public static ArrayList<Double> name_dialog(ArrayList<String> map, ArrayList<String> comments)  //find users in dialogue by name
	{
		ArrayList<Double> v1 = new ArrayList<Double>();
		for(int i=0; i<comments.size(); i++)
		{
			double val = 0.0;
			String[] spl = comments.get(i).split("\\s+");
			for(String value : map)
			{
				String[] splited;
				if(value.contains("-") || value.contains("_") || value.contains(" "))
					splited = value.split("[-_\\s+]");
				else
					splited = value.split("((?<=[a-zA-Z])(?=[0-9]))|((?<=[0-9])(?=[a-zA-Z]))");
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
							val = 1.0;
							break;
						}
					}
					if(val == 1.0)
						break;
					if(words.equalsIgnoreCase(value))
					{
						val = 1.0;
						break;
					}
				}
				if(val == 1.0)
					break;
			}
			v1.add(val);
		}
		return v1;
	}
	/**
	 * This method identifies dialogues considering repeated comments by the same user in a thread
	 * @param map: ArrayList of usernames in the thread
	 * @param cid: List of comment ids in the thread
	 * @return Returns an ArrayList of values with 1.0 for comments having this property
	 */
	public static ArrayList<Double> dialog(ArrayList <String> map, ArrayList<String> cid)               //find users in dialogues by multiplicity of their comments
	{
		ArrayList<Double> v2 = new ArrayList<>();
		ArrayList<String> h = new ArrayList<>();
		v2.add(0.0);
		h.add(map.get(0));
		for(int i=1; i<cid.size(); i++)
		{
			double val = 0.0;
			if(h.contains(map.get(i)))
			{
				val = 1.0;
			}
			else
			{
				h.add(map.get(i));
			}
			v2.add(val);
		}
		return v2;
	}
	/**
	 * This method writes an SVM file from the features
	 * @param writer: Writer object
	 * @param label: Labels of the class
	 * @param v1: List of feature values
	 * @param v2: List of feature values
	 */
	public static void SVM_writer(PrintWriter writer, ArrayList<String> label, ArrayList<Double> v1, ArrayList<Double> v2)       //SVM file writer
	{
		for(int i=0; i<label.size(); i++)
		{
			writer.println(binary_class(label.get(i))+" 1:"+v1.get(i)+" 2:"+v2.get(i));
		}
	}
	public static int binary_class(String s)                     //Generate binary labels
	{
		if(s.equals("Good"))
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	public static int get_Label_value(String s)                  //Generate multiclass labels
	{
		if(s.equals("Good"))
		{
			return 1;
		}
		else if(s.equals("PotentiallyUseful"))
		{
			return 3;
		}
		return 2;
	}
}
