package core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import writer.SVMWriter;
import info.debatty.java.stringsimilarity.Cosine;
import info.debatty.java.stringsimilarity.Damerau;
import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.Levenshtein;
import info.debatty.java.stringsimilarity.LongestCommonSubsequence;
import info.debatty.java.stringsimilarity.NGram;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import info.debatty.java.stringsimilarity.QGram;
import info.debatty.java.stringsimilarity.SorensenDice;

/**
 * This class calculates string similarity measures between question and comment
 * @author titas
 *
 */
public class StringSimilarity         //File generating various string features
{
	static double[] f = new double[20];
	static String input;
	public StringSimilarity(String inp)
	{
		input = inp;	
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		File inputFile = new File(input);
		File parent = inputFile.getParentFile();
		String pathgp = parent.getAbsolutePath();
		File dir = new File(pathgp+"/svm_files/");
		File dir2 = new File(pathgp+"/svm_files/train/");
		File dir3 = new File(pathgp+"/svm_files/test/");
		boolean success = dir.mkdirs();                       //create directory for storing new files
		boolean success2 = dir2.mkdirs();
		boolean success3 = dir3.mkdirs();
		dir.setExecutable(true);                             //set file permissions for files in new directory
		dir.setReadable(true);
		dir.setWritable(true);
		dir2.setExecutable(true);
		dir2.setReadable(true);
		dir2.setWritable(true);
		dir3.setExecutable(true);
		dir3.setReadable(true);
		dir3.setWritable(true);
		System.out.println("Similarity Features computation starts......");
		SimilarityFeatureGeneratorRun(input+"/train_clean.txt", pathgp+"/svm_files/train/string_train.txt");
		SimilarityFeatureGeneratorRun(input+"/test_clean.txt", pathgp+"/svm_files/test/string_test.txt");
		//SimilarityFeatureGeneratorRun(input+"/dev_clean.txt", pathgp+"/svm_files/dev/string_dev.txt");
	}
	public static void SimilarityFeatureGeneratorRun(String input, String output)
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
				String str = reader.readLine();				
				do
				{
					String question = reader.readLine();
					for(int i=0; i<10; i++)
					{
						str = reader.readLine();
						String[] splited = str.split("\\s+");
						String c_id = splited[0];
						String label = splited[1];
						String comment = reader.readLine();
						 f[0] = ngram(question, comment, 1);
						 f[1] = ngram(question, comment, 2);
						 f[2] = ngram(question, comment, 3);
						 f[3] = cosine(question, comment,1);
						 f[4] = cosine(question, comment, 2);
						 f[5] = cosine(question, comment, 3);
						 f[6] = Jaccard(question, comment, 1);
						 f[7] = Jaccard(question, comment, 2);
						 f[8] = Jaccard(question, comment, 3);
						 f[9] = QGram(question, comment, 1);
						 f[10] = QGram(question, comment, 2);
						 f[11] = QGram(question, comment, 3);
						 f[12] = Sorensen(question, comment, 1);
						 f[13] = Sorensen(question, comment, 2);
						 f[14] = Sorensen(question, comment, 3);
						 f[15] = JaroWinkler(question, comment);
						 f[16] = Damerau(question, comment);
						 f[17] = Levenshtein(question, comment);
						 f[18] = NormalizedLevenshtein(question, comment);
						 f[19] = LCS(question, comment);
						 SVMWriter w = new SVMWriter(writer, label, 1, f);
						 w.write();
					}					
				}
				while((str = reader.readLine())!=null);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
	/**
	 * All the subsequent methods create objects for string similarity computations 
	 * @param s1: first string (typically question)
	 * @param s2: second string (typically answer)
	 * @param n: for n-grams
	 * @return distance or similarity score
	 */
	public static double ngram(String s1, String s2, int n)        //ngram score
	{
		NGram ngram = new NGram(n);
		return ngram.distance(s1, s2);
	}
	public static double cosine(String s1, String s2, int n)       //cosine score
	{
		Cosine cos = new Cosine(n);
		if(Double.isNaN(cos.similarity(s1,s2)))
		{
			return 0.0;
		}
		return cos.similarity(s1, s2);
	}
	public static double Jaccard(String s1, String s2, int n)       //Jaccard score
	{
		Jaccard j2 = new Jaccard(n);
		if(Double.isNaN(j2.similarity(s1,s2)))
		{
			return 0.0;
		}
		return j2.similarity(s1, s2);
	}
	public static double QGram(String s1, String s2, int n)           //QGram score
	{
		QGram dig = new QGram(n);
		return dig.distance(s1, s2);
	}
	public static double Sorensen(String s1, String s2, int n)         //Sorensen score
	{
		SorensenDice sd = new SorensenDice(n);
		if(Double.isNaN(sd.similarity(s1,s2)))
		{
			return 0.0;
		}
		return sd.similarity(s1, s2);
	}
	public static double JaroWinkler(String s1, String s2)				//JaroWinkler score
	{
		JaroWinkler jw = new JaroWinkler();
		return jw.similarity(s1, s2);
	}
	public static double Damerau(String s1, String s2)					//Damerau score
	{
		Damerau damerau = new Damerau();
		return damerau.distance(s1, s2);
	}
	public static double Levenshtein(String s1, String s2)				//Levenshtein score
	{
		Levenshtein levenshtein = new Levenshtein();
		return levenshtein.distance(s1, s2);
	}
	public static double NormalizedLevenshtein(String s1, String s2)		//Normalized Levenshtein score
	{
		NormalizedLevenshtein l = new NormalizedLevenshtein();
		if(Double.isNaN(l.distance(s1,s2)))
		{
			return 0.0;
		}
		return l.distance(s1, s2);
	}
	public static double LCS(String s1, String s2)							// LCS score
	{
		LongestCommonSubsequence lcs = new LongestCommonSubsequence();
		if(Double.isNaN(lcs.distance(s1,s2)))
		{
			return 0.0;
		}
		return lcs.distance(s1, s2);
	}
}
