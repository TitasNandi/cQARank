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
	static ArrayList<String>oisz_arr = new ArrayList<>();
	static ArrayList<String>ziso_arr = new ArrayList<>();
	static String input;
	static String input1;
	static String input2;
	public Writer(String inp, String inp1, String inp2)
	{
		input = inp;	
		input1 = inp1;
		input2 = inp2;
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		System.out.println("Computing final scores......");
//		File dir = new File(input+"/result_files/threshold_files/");
//		File[] out_files = dir.listFiles();
//		for(int i=0; i<out_files.length; i++)
//		{
//			System.out.println(out_files[i].getName());
//			WriterRun(input+"/parsed_files/dev_clean.txt", out_files[i].getAbsolutePath(), input1, input2, input+"/result_files/threshold_files/results"+out_files[i].getName(), input+"/result_files/threshold_files/error"+out_files[i].getName());
//			oiso = 0;
//			zisz = 0;
//			oisz = 0;
//			ziso = 0;
//			oisz_arr = new ArrayList<>();
//			ziso_arr = new ArrayList<>();
//		}
		//WriterRun(input+"/parsed_files/dev_clean.txt", input+"/result_files/out_dev.txt", input+"/result_files/results.txt", input+"/result_files/error.txt");
		//WriterRun(input+"/parsed_files/dev_clean.txt", input+"/result_files/out.txt", input+"/result_files/results_org_com.txt", input+"/result_files/error_org_com.txt", 1);
		//WriterRun(input+"/parsed_files/dev_clean.txt", input+"/result_files/out_dev_embed.txt", input1+"/result_files/results.txt", input2+"/result_files/results.txt", input2+"/parsed_files/dev_clean.txt", input+"/result_files/results.txt", input+"/result_files/error.txt");
		WriterRun(input+"/parsed_files/test_clean.txt", input+"/result_files/out_test.txt", input1+"/result_files/results.txt", input2+"/result_files/results.txt", input2+"/parsed_files/test_clean.txt", input+"/result_files/results.txt", input+"/result_files/error.txt");
	}
	public static void WriterRun(String inp1, String inp2, String inp3, String inp4, String inp5, String out1, String out2)
	{
		PrintWriter writer = null;
		BufferedReader reader = null;
		BufferedReader reader_2 = null;
		BufferedReader reader_3 = null;
		BufferedReader reader_4 = null;
		BufferedReader reader_5 = null;
		PrintWriter writer2 = null; 
		File file = new File(inp1);
		File file_2 = new File(inp2);
		File file_3 = new File(inp3);
		File file_4 = new File(inp4);
		File file_5 = new File(inp5);
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
			reader_4 = new BufferedReader(new FileReader(file_4));
			reader_5 = new BufferedReader(new FileReader(file_5));
			try {
				String str = reader.readLine();
				String str2 = reader_2.readLine();
				double rel_rank = 0.0;
				do
				{
					String[] qs = str.split("\\s+");
					String q_id = qs[0];
					String question = reader.readLine();
					String ls = reader_5.readLine();
					ls = reader_5.readLine();
					int rank = 0;
					for(int i=0; i<100; i++)
					{
						str = reader.readLine();
						String[] splited = str.split("\\s+");
						String c_id = splited[0];
						String label = splited[1];
						String comment = reader.readLine();
						String score_line = reader_2.readLine();
						splited = score_line.split("\\s+");
						double score = Double.parseDouble(splited[1]);
						String l = splited[0];
						if(Double.parseDouble(l) != binary_class(label, 0))                  //error analysis
						{
							writer2.println(str);
							writer2.println(comment);
						}
						String bin_class = get_class(l);
						String score_com = reader_3.readLine();
						splited = score_com.split("\t");
						double com_rank = Double.parseDouble(splited[3]);
						//System.out.println(com_rank+"**");
						
						if(i%10 == 0)
						{
							ls = reader_5.readLine();
							splited = ls.split("\\s+");
							rank = Integer.parseInt(splited[2]);
							ls = reader_5.readLine();
							String score_rel = reader_4.readLine();
							splited = score_rel.split("\t");
							rel_rank = Double.parseDouble(splited[3]);
							//System.out.println(rel_rank+"$$");
						}
						comp_class(label, Double.parseDouble(l), c_id);
						writer.println(q_id+"\t"+c_id+"\t"+((rank*100)+(i%10)+1)+"\t"+(Math.log(score)+Math.log(rel_rank)+Math.log(com_rank))+"\t"+bin_class);        //scorer script format
					}
				}
				while((str = reader.readLine())!=null);
				writer.close();
				writer2.close();
				System.out.println("Good classified as Good: "+oiso);
				System.out.println("Good classified as Bad: "+oisz);
				System.out.println("Bad classified as Good: "+ziso);
				System.out.println("Bad classified as Bad: "+zisz);
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
			if(binary_class(gold, 0) == 0.0)
				zisz++;
			else
			{
				oisz++;
				oisz_arr.add(c_id);
			}
				
		}
		if(predict == 1.0)
		{
			if(binary_class(gold, 0) == 1.0)
				oiso++;
			else
			{
				ziso++;
				ziso_arr.add(c_id);
			}
		}
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
				return 0;
			}
			else
			{
				return 1;
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

