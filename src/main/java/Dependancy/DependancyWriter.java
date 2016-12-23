package Dependancy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import writer.SVMWriter;

public class DependancyWriter 
{
	static double[] f = new double[4];
	static String input;
	public DependancyWriter(String inp)
	{
		input = inp;	
	}
	public static void initialize()
	{
		DependancyFeatures(input+"/parsed_files/train_clean.txt", input+"/Dependency_files/train/cov_features.txt", input+"/svm_files/train/dep_train.txt");
		//DependancyFeatures(input+"/parsed_files/dev_clean.txt", input+"/Dependency_files/dev/cov_features.txt", input+"/svm_files/dev/dep_dev.txt");
		DependancyFeatures(input+"/parsed_files/test_clean.txt", input+"/Dependency_files/test/cov_features.txt", input+"/svm_files/test/dep_test.txt");
	}
	
	public static void DependancyFeatures(String input, String input1, String output)
	{
		File file = new File(input);
		File file_2 = new File(input1);
		BufferedReader reader = null;
		BufferedReader reader_2 = null;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(output, false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
			try {
				reader = new BufferedReader(new FileReader(file));
				reader_2 = new BufferedReader(new FileReader(file_2));
				String line;
				try {
					line = reader_2.readLine();
					while((line = reader.readLine()) != null)
					{
						line = reader.readLine();
						for(int i=0; i<10; i++)
						{
							line = reader.readLine();
							String[] splited = line.split("\\s+", 4);
							String label = splited[1];
							String comment = reader.readLine();
							String features = reader_2.readLine();
							String[] dep = features.split("\t");
							double d1 = Double.parseDouble(dep[2]);
							if(Double.isNaN(d1))
								f[0] = 0.0;
							else
								f[0] = d1;
							double d2 = Double.parseDouble(dep[3]);
							if(Double.isNaN(d2))
								f[1] = 0.0;
							else
								f[1] = d2;
							double d3 = Double.parseDouble(dep[8]);
							if(Double.isNaN(d3))
								f[2] = 0.0;
							else
								f[2] = d3;
							double d4 = Double.parseDouble(dep[9]);
							if(Double.isNaN(d4))
								f[3] = 0.0;
							else
								f[3] = d4;
//							for(int j=0; j<8; j++)
//							{
//								double d = Double.parseDouble(dep[j+2]);
//								if(Double.isNaN(d))
//									f[j] = 0.0;
//								else
//									f[j] = d;
//							}
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
}
