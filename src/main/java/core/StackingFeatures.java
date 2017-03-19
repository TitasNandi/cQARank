package core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import writer.SVMWriter;
public class StackingFeatures 
{
	static double[] f = new double[10];
	static String input;
	static String input2;
	static String input3;
	public StackingFeatures(String inp, String inp2, String inp3)
	{
		input = inp;
		input2 = inp2;
		input3 = inp3;
	}
	public static void initialize()
	{
		StackingFeaturesRun(input+"/parsed_files/test_clean.txt", input+"/result_files/out_test.txt", input2+"/out_test.txt", input3+"/out_test.txt", input+"/svm_files/test/stacking_test.txt");
		StackingFeaturesRun(input+"/parsed_files/train_clean.txt", input+"/result_files/out_train.txt", input2+"/out_train.txt", input3+"/out_train.txt", input+"/svm_files/train/stacking_train.txt");
	}
	public static void StackingFeaturesRun(String input, String input2, String input3, String input4, String output)
	{
		File file = new File(input);
		File file2 = new File(input2);
		File file3 = new File(input3);
		File file4 = new File(input4);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedReader reader2 = new BufferedReader(new FileReader(file2));
			BufferedReader reader3 = new BufferedReader(new FileReader(file3));
			BufferedReader reader4 = new BufferedReader(new FileReader(file4));
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output, false)));
			String line;
			line = reader2.readLine();
			line = reader3.readLine();
			line = reader4.readLine();
			while((line = reader.readLine()) != null)
			{
				String qid = reader.readLine();
				double score_oq = 0.0;
				for(int j=0; j<100; j++)
				{
					int count = 0;
					line = reader.readLine();
					String[] spl = line.split("\\s+");
					String label = spl[1];
					qid = reader.readLine();
					String out_line = reader2.readLine();
					spl = out_line.split("\\s+");
					double score_oc = Double.parseDouble(spl[1]);
					out_line = reader3.readLine();
					spl = out_line.split("\\s+");
					double score_qc = Double.parseDouble(spl[1]);
					if(j % 10 == 0)
					{
						out_line = reader4.readLine();
						spl = out_line.split("\\s+");
						score_oq = Double.parseDouble(spl[1]);
					}
					f[count++] = score_oc;
					f[count++] = score_qc;
					f[count++] = score_oq;
					if(score_oc >= 0.5)
						f[count++] = 1.0;
					else
						f[count++] = 0.0;
					if(score_qc >= 0.5)
						f[count++] = 1.0;
					else
						f[count++] = 0.0;
					if(score_oq >= 0.5)
						f[count++] = 1.0;
					else
						f[count++] = 0.0;
					if(score_qc >= 0.5 && score_oq >= 0.5)
						f[count++] = 1.0;
					else
						f[count++] = 0.0;
					f[count++] = Math.min(score_qc, score_oq);
					f[count++] = Math.max(score_qc, score_oq);
					f[count++] = (score_qc+score_oq)/2;
					SVMWriter w = new SVMWriter(writer, label, 1, f);
					w.write();
				}
			}
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
