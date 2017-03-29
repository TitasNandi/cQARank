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
/**
 * This class combines all feature files into one file for SVM classification and z-score normalizes it
 * @author titas
 *
 */
public class MultiFileReader 
{
	static int num_features;
	static int train_count;
	static int dev_count;
	static String input1;
	static String input2;
	public MultiFileReader(String inp1, String inp2, int num)
	{
		input1 = inp1;	
		input2 = inp2;
		num_features = num;
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		System.out.println("Combining SVM feature files......");
		//MultiFileReaderRun(input1+"/train/", input1+"/dev/", input2+"/train_clean.txt", input2+"/dev_clean.txt");
		MultiFileReaderRun(input1+"/train/", input1+"/test/", input2+"/train_clean.txt", input2+"/test_clean.txt");
	}
	public static void MultiFileReaderRun(String train, String dev, String train2, String dev2)
	{
		File SVM_dir_train = new File(train);              //path to train files' directory
		File SVM_dir_dev = new File(dev);                  //path to test files' directory
		File[] files_train = SVM_dir_train.listFiles();
		File[] files_dev = SVM_dir_dev.listFiles();
		String[] SVM_files_train = new String[files_train.length];
		String[] SVM_files_dev = new String[files_dev.length];
		for(int i=0; i<files_train.length; i++)
		{
			SVM_files_train[i] = train+"/"+files_train[i].getName();
		}
		for(int i=0; i<files_dev.length; i++)
		{
			SVM_files_dev[i] = dev+"/"+files_dev[i].getName();
		}
		train_count = len_cal(train2);
		dev_count = len_cal(dev2);
		multireader(SVM_files_train, train+"/SVM_train.txt", num_features, 0, files_train.length);
		multireader(SVM_files_dev, dev+"/SVM_test.txt", num_features, 1, files_dev.length);
	}
	/**
	 * This method reads multiple train or test SVM data files and combines them in one
	 * @param input_dir: The path to input directory
	 * @param output_file: The path to output file
	 * @param n: The number of features
	 * @param flag: Flag for train or test files
	 * @param num: Number of files in the directory
	 */
	public static void multireader(String[] input_dir, String output_file, int n, int flag, int num)          //combine all data files and normalize
	{
		BufferedReader[] reader = new BufferedReader[num];
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(output_file, false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i=0; i<num; i++)
		{
			File file = new File(input_dir[i]);
			try {
				reader[i] = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int caller = 0;
		double[][] arr = new double[n][100]; 
		int[] labels = new int[100];
		String line;
		int breaker = 0;
		int q_id_rank = 0;
		try {
			while(true)
			{
				caller++;
				int count = 0;
				int start;
				for(int i=0; i<num; i++)
				{
					line = reader[i].readLine();
					if(line == null)
					{
						breaker = 1;
						break;
					}
					String[] splited = line.split("\\s+");
					
					start = 1;
					for(int j=start; j<splited.length; j++)
					{
						if(j<10)
						{
							arr[count][caller-1] = Double.parseDouble(splited[j].substring(2));
						}
						else if(j < 100)
						{
							arr[count][caller-1] = Double.parseDouble(splited[j].substring(3));
						}
						else
						{
							arr[count][caller-1] = Double.parseDouble(splited[j].substring(4));
						}
						count++;
					}
		
					if(i==0)
					{
						labels[caller-1] = Integer.parseInt(splited[0]);
					}
					
				}
				if(caller == 100)
				{
					normalize(arr, n, 100);         //normalize by z-score
					q_id_rank++;
					for(int i=0; i<100; i++)
					{
						writer.print(labels[i]+" ");
						for(int j=0; j<n; j++)
						{
							writer.print((j+1)+":"+arr[j][i]+" ");
						}
						writer.println();
					}
					if(flag == 0)
					{
						if(q_id_rank == train_count)
						{
							break;
						}
					}
					else
					{
						if(q_id_rank == dev_count)
						{
							break;
						}
					}
					arr = new double[n][100];
					labels = new int[100];
					caller = 0;
				}
				if(breaker == 1)
				{
					break;
				}
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * This method adds important data from train and test files to final file
	 * @param input1: input train file
	 * @param input2: input test file
	 */
	public static int len_cal(String input)
	{
		int count = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(input));
			String line;
			try {
				while(true)
				{
						String qid = reader.readLine();
						if(qid == null)
						{
							break;
						}
						String rquestion = reader.readLine();
						for(int j=0; j<100; j++)
						{
							String cid = reader.readLine();
							String comment = reader.readLine();
							count++;
						}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	/**
	 * This method z-score normalizes all data
	 * @param arr: array of values
	 * @param n: number of features
	 * @param len: number of threads
	 */
	public static void normalize(double[][] arr, int n, int len)                   //normalization code
	{
		for(int i=0; i<n; i++)
		{
			double[] data = new double[len];
			for(int j=0; j<len; j++)
			{
				data[j] = arr[i][j];
			}
			Statistics a = new Statistics(data);
			if(a.getStdDev() != 0.0)
			{
				for(int j=0; j<len; j++)
				{
					
						arr[i][j] = (arr[i][j] - a.getMean())/a.getStdDev();
				}
			}
		}
	}
}
/**
 * This class is used for calculating some statistical measures for z-score
 * @author titas
 *
 */
class Statistics                                                 //zscore normalization of data
{
    double[] data;
    int size;   

    public Statistics(double[] data) 
    {
        this.data = data;
        size = data.length;
    }   

    double getMean()
    {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/size;
    }

    double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        for(double a :data)
            temp += (mean-a)*(mean-a);
        return temp/size;
    }

    double getStdDev()
    {
        return Math.sqrt(getVariance());
    }
}
