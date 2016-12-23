package writer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
/**
 * This class writes the SVM classification scores and labels in the format of the scorer script for SemEval 2016
 * @author titas
 *
 */
public class Writer 
{
	static int oiso = 0;
	static int zisz = 0;
	static int oisz = 0;
	static int ziso = 0;
	static int good_count = 0;
	static int bad_count = 0;
	static ArrayList<String>oisz_arr = new ArrayList<>();
	static ArrayList<String>ziso_arr = new ArrayList<>();
	static String input;
	public Writer(String inp)
	{
		input = inp;	
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		System.out.println("Computing final scores......");
		//WriterRun(input+"/parsed_files/dev_clean.txt", input+"/result_files/out_rel_com.txt", input+"/result_files/results_rel_com.txt", input+"/result_files/error_rel_com.txt", 0);
		//WriterRun(input+"/parsed_files/dev_clean.txt", input+"/result_files/out@0.5.txt", input+"/result_files/results.txt", input+"/result_files/error.txt");
		File dir = new File(input+"/result_files/threshold_files/");
		File[] out_files = dir.listFiles();
		for(int i=0; i<out_files.length; i++)
		{
			System.out.println(out_files[i].getName());
			WriterRun(input+"/parsed_files/dev_clean.txt", out_files[i].getAbsolutePath(), input+"/result_files/threshold_files/results"+out_files[i].getName(), input+"/result_files/threshold_files/error"+out_files[i].getName());
			oiso = 0;
			zisz = 0;
			oisz = 0;
			ziso = 0;
			good_count = 0;
			bad_count = 0;
			oisz_arr = new ArrayList<>();
			ziso_arr = new ArrayList<>();
		}
		//WriterCustom(input+"/parsed_files/test_clean.txt", input+"/result_files/out_new_qq_test.txt", input+"/result_files/weka_result_qq.txt", input+"/result_files/results.txt", input+"/result_files/error.txt");
	}
	public static void WriterRun(String inp1, String inp2, String out1, String out2)
	{
		
		PrintWriter writer = null;
		BufferedReader reader = null;
		BufferedReader reader_2 = null;
		//BufferedReader reader_3 = null;
		PrintWriter writer2 = null; 
		File file = new File(inp1);
		File file_2 = new File(inp2);
		//File file_3 = new File(inp3);
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(out1, false)));
			writer2 = new PrintWriter(new BufferedWriter(new FileWriter(out2, false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader = new BufferedReader(new FileReader(file));
			reader_2 = new BufferedReader(new FileReader(file_2));
			//reader_3 = new BufferedReader(new FileReader(file_3));
			try {
				String str = reader.readLine();
				String str2 = reader_2.readLine();
				do
				{
					String[] qs = str.split("\\s+");
					String q_id = qs[0];
					String question = reader.readLine();
					for(int i=0; i<10; i++)
					{
						str = reader.readLine();
						String[] splited = str.split("\\s+");
						String c_id = splited[0];
						String label = splited[1];
						get_count(label);
						int rel_rank = Integer.parseInt(splited[2]);
						String comment = reader.readLine();
						String score_line = reader_2.readLine();
						splited = score_line.split("\\s+");
						String l = splited[0];
						if(Double.parseDouble(l) != binary_class(label, 1))                  //error analysis
						{
							writer2.println(str);
							writer2.println(comment);
						}
						double score1 = Double.parseDouble(splited[1]);
						String bin_class = get_class(l);
//						String tax_score = reader_3.readLine();
//						String[] spl = tax_score.split("\t");
						double score = score1/(rel_rank);
						comp_class(label, Double.parseDouble(l), c_id);
						writer.println(q_id+" "+c_id+" 0 "+score+" "+bin_class);        //scorer script format
					}
				}
				while((str = reader.readLine())!=null);
				writer.close();
				writer2.close();
				System.out.println("Good classified as Good: "+oiso);
				System.out.println("Good classified as Bad: "+oisz);
				System.out.println("Bad classified as Good: "+ziso);
				System.out.println("Bad classified as Bad: "+zisz);
				//System.out.println("good_count: "+good_count);
				//System.out.println("bad_count: "+bad_count);
				System.out.println("Misclassification: "+(oisz+ziso));
				double acc = (oiso+zisz)*1.0/(oiso+oisz+ziso+zisz);
				double prec = oiso*1.0/(oiso+ziso);
				double rec = oiso*1.0/(oiso+oisz);
				double f1 = 2*prec*rec/(prec+rec);
				System.out.println("Accuracy: "+acc);
				System.out.println("Precision: "+prec);
				System.out.println("Recall: "+rec);
				System.out.println("F1: "+f1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void WriterCustom(String inp1, String inp2, String inp3, String out1, String out2)
	{
		PrintWriter writer = null;
		BufferedReader reader = null;
		BufferedReader reader_2 = null;
		BufferedReader reader_3 = null;
		PrintWriter writer2 = null; 
		File file = new File(inp1);
		File file_2 = new File(inp2);
		File file_3 = new File(inp3);
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(out1, false)));
			writer2 = new PrintWriter(new BufferedWriter(new FileWriter(out2, false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader = new BufferedReader(new FileReader(file));
			reader_2 = new BufferedReader(new FileReader(file_2));
			reader_3 = new BufferedReader(new FileReader(file_3));
			try {
				String str = reader.readLine();
				String str2 = reader_2.readLine();
				str2 = reader_3.readLine();
				do
				{
					String[] qs = str.split("\\s+");
					String q_id = qs[0];
					String question = reader.readLine();
					for(int i=0; i<10; i++)
					{
						str = reader.readLine();
						String[] splited = str.split("\\s+");
						String c_id = splited[0];
						String label = splited[1];
						get_count(label);
						int rel_rank = Integer.parseInt(splited[2]);
						String comment = reader.readLine();
						String score_line = reader_2.readLine();
						splited = score_line.split("\\s+");
						double score1 = Double.parseDouble(splited[1]);
						String classification_line = reader_3.readLine();
						String[] spl = classification_line.split("\\s+");
						System.out.println(spl[0]);
						String l = spl[2].substring(2).trim();
						if((Integer.parseInt(l)*1.0) != binary_class(label, 0))                  //error analysis
						{
							writer2.println(str);
							writer2.println(comment);
						}
						String bin_class = get_class(l);
						double score = score1/(rel_rank);
						comp_class(label, Integer.parseInt(l)*1.0, c_id);
						writer.println(q_id+" "+c_id+" 0 "+score+" "+bin_class);        //scorer script format
					}
				}
				while((str = reader.readLine())!=null);
				writer.close();
				writer2.close();
				System.out.println("Good classified as Good: "+oiso);
				System.out.println("Good classified as Bad: "+oisz);
				System.out.println("Bad classified as Good: "+ziso);
				System.out.println("Bad classified as Bad: "+zisz);
				System.out.println("good_count: "+good_count);
				System.out.println("bad_count: "+bad_count);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * This method calculates misclassifications by the classifier
	 * @param gold: the gold label
	 * @param predict: the predicted label
	 * @param c_id: comment id
	 */
	public static void comp_class(String gold, double predict, String c_id)      //find misclassified comments
	{
		if(predict == 0.0)
		{
			if(binary_class(gold, 1) == 0.0)
				zisz++;
			else
			{
				oisz++;
				oisz_arr.add(c_id);
			}
				
		}
		if(predict == 1.0)
		{
			if(binary_class(gold, 1) == 1.0)
				oiso++;
			else
			{
				ziso++;
				ziso_arr.add(c_id);
			}
		}
	}
	public static void get_count(String label)
	{
		if(label.equals("Irrelevant"))
			bad_count++;
		else
			good_count++;
	}
	public static double binary_class(String s, int flag)
	{
		if(flag == 0)
		{
			if(s.equals("Good"))
			{
				return 1.0;
			}
			else
			{
				return 0.0;
			}
		}
		else
		{
			if(s.equals("Irrelevant"))
			{
				return 0.0;
			}
			else
			{
				return 1.0;
			}
		}
	}
	/**
	 * This method returns "true" if classification label was 1.0 else "false"
	 * @param s: the input label
	 * @return a String "true" or "false"
	 */
	public static String get_class(String s)
	{
		if(Double.parseDouble(s) == 1.0)
			return "true";
		else
			return "false";
	}
}

