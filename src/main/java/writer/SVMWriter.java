package writer;

import java.io.PrintWriter;

public class SVMWriter 
{
	PrintWriter writer;
	String label;
	int flag;
	double[] f;
	static int good = 0;
	static int bad = 0;
	/**
	 * This constructor creates an SVM feature file from given feature values
	 * @param writer: Writer object to write data to SVM file
	 * @param label: Label of the comment  
	 * @param flag: Flag indicating multiclass or binary classification
	 * @param f: The array of feature values
	 */
	public SVMWriter(PrintWriter writer, String label, int flag, double[] f)
	{
		this.writer = writer;
		this.label = label;
		this.flag = flag;
		this.f = f;
	}
	public void write()                                    //write SVM data file
	{
		if(flag == 0)
		{
			writer.print(get_Label_value(label)+" ");
		}
		else
		{
			writer.print(binary_class(label)+" ");	
		}
		for(int i=0; i<f.length; i++)
		{
			writer.print((i+1)+":"+f[i]+" ");
		}
		writer.println();
	}
	/**
	 * This method calculates class labels for SVM
	 * @param s: input string indicating class name
	 * @return: returns an integer
	 */
	public static int get_Label_value(String s)                      //Generate multiclass labels
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
	/**
	 * This method generates binary labels from binary classnames 
	 * @param s: input string indicating classname
	 * @return: a binary value
	 */
	public static int binary_class(String s)                     //Generate binary labels
	{
		if(s.equals("Good"))
		{
			good++;
			return 1;
		}
		else
		{
			bad++;
			return 0;
		}
	}
}