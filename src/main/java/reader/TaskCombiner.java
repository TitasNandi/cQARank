package reader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
public class TaskCombiner 
{
	static String input1;
	static String input2;
	static String input3;
	public TaskCombiner(String inp1, String inp2, String inp3)
	{
		input1 = inp1;	
		input2 = inp2;
		input3 = inp3;
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		System.out.println("Combining SVM feature files......");
		//MultiFileReaderRun(input1+"/train/", input1+"/dev/", input2+"/train_clean.txt", input2+"/dev_clean.txt");
		//TaskCombinerRun(input1+"/train/SVM_train.txt", input2+"/train/SVM_train.txt", input3+"/train/SVM_train.txt", input3+"/train/SVM_train_combined.txt");
		TaskCombinerRun(input1+"/dev/SVM_dev.txt", input2+"/dev/SVM_dev.txt", input3+"/dev/SVM_dev.txt", input3+"/dev/SVM_dev_combined.txt");
	}
	public static void TaskCombinerRun(String input1, String input2, String input3, String output)
	{
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(input1));
			BufferedReader reader2 = new BufferedReader(new FileReader(input2));
			BufferedReader reader3 = new BufferedReader(new FileReader(input3));
			PrintWriter writer = null;
			String line;
			writer = new PrintWriter(new BufferedWriter(new FileWriter(output, false)));
			int count = 0;
			String que_line = "";
			while((line = reader.readLine()) != null)
			{
				String[] spl = line.split("\\s+");
				int len = spl.length;
				writer.print(line.trim()+" ");
				if(count%10 == 0)
				{
					que_line = reader2.readLine();
				}
				spl = que_line.split("\\s+");
				for(int i=1; i<spl.length; i++)
				{
					if(i<10)
					{
						writer.print(len+":"+spl[i].substring(2)+" ");
					}
					else if(i < 100)
					{
						writer.print(len+":"+spl[i].substring(3)+" ");
					}
					else
					{
						writer.print(len+":"+spl[i].substring(4)+" ");
					}
					len++;
				}
				String com_line = reader3.readLine();
				spl = com_line.split("\\s+");
				for(int i=1; i<spl.length; i++)
				{
					if(i<10)
					{
						writer.print(len+":"+spl[i].substring(2)+" ");
					}
					else if(i < 100)
					{
						writer.print(len+":"+spl[i].substring(3)+" ");
					}
					else
					{
						writer.print(len+":"+spl[i].substring(4)+" ");
					}
					len++;
				}
				writer.println();
				count++;
			}
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
