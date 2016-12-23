package core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import writer.SVMWriter;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

public class StackingFeatures 
{
	static double[] f = new double[20];
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
		StackingFeaturesRun(input+"/parsed_files/dev_clean.txt", input2+"/out.txt", input3+"/out.txt", input+"/svm_files/dev/stacking_dev.txt");
		StackingFeaturesRun(input+"/parsed_files/train_clean.txt", input2+"/out_train.txt", input3+"/out_train.txt", input+"/svm_files/train/stacking_train.txt");
	}
	public static void StackingFeaturesRun(String input, String input2, String input3, String output)
	{
		File file = new File(input);
		File file2 = new File(input2);
		File file3 = new File(input3);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedReader reader2 = new BufferedReader(new FileReader(file2));
			BufferedReader reader3 = new BufferedReader(new FileReader(file3));
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output, false)));
			String line;
			line = reader2.readLine();
			line = reader3.readLine();
			while((line = reader.readLine()) != null)
			{
				String qid = reader.readLine();
				for(int j=0; j<10; j++)
				{
					int i = 0;
					line = reader.readLine();
					String[] splited = line.split("\\s+");
					String label = splited[1];
					double mse = 0.0;
					double[] qc_arr = new double[10];
					double[] oc_arr = new double[10];
					int agree = 0;
					int pos = 0;
					double sum = 0.0;
					double max = Double.MIN_VALUE;
					for(i=0; i<10; i++)
					{
						line = reader2.readLine();
						String[] spl = line.split("\\s+");
						double score_qc = Double.parseDouble(spl[1]);
						line = reader3.readLine();
						spl = line.split("\\s+");
						double score_oc = Double.parseDouble(spl[1]);
						f[i] = score_qc - score_oc;
						mse += f[i]*f[i];
						qc_arr[i] = score_qc;
						oc_arr[i] = score_oc;
						if((score_qc >= 0.5 && score_oc >= 0.5) || (score_qc < 0.5 && score_oc < 0.5))
							agree++;
						if(score_oc > max)
							max = score_oc;
						if(score_oc >= 0.5 && score_qc >= 0.5)
							pos++;
						sum += score_oc;
					}
					f[i++] = mse;
					f[i++] = new PearsonsCorrelation().correlation(qc_arr,oc_arr);
					f[i++] = agree;
					f[i++] = pos;
					f[i++] = max;
					f[i++] = sum/10;
					f[i++] = new SpearmansCorrelation().correlation(qc_arr,oc_arr);
					f[i++] = new KendallsCorrelation().correlation(qc_arr,oc_arr);
					vector vec_qc = new vector(qc_arr);
					vector vec_oc = new vector(oc_arr);
					f[i++] = vec_qc.vector_cos(vec_qc, vec_oc);
					f[i++] = vec_qc.vec_manhattan(vec_qc, vec_oc);
					SVMWriter w = new SVMWriter(writer, label, 1, f);
					w.write();
					line = reader.readLine();
				}
			}
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}