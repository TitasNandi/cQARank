package writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ThresholdFixer 
{
	static String input;
	public ThresholdFixer(String inp)
	{
		input = inp;	
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		File inputFile = new File(input);
		String pathgp = inputFile.getAbsolutePath();
		File dir = new File(pathgp+"/threshold_files/");
		boolean success = dir.mkdirs();                       //create directory for storing new files
		dir.setExecutable(true);                             //set file permissions for files in new directory
		dir.setReadable(true);
		dir.setWritable(true);
		ThresholdFixerRun(input+"/out_oq_dev.txt", input+"/threshold_files", 0.25, 0.75);
	}
	public static void ThresholdFixerRun(String input, String output, double start, double end)
	{
		File file = new File(input);
		PrintWriter writer = null;
		try {			
			while(start <= end)
			{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				String formattedString = String.format("%.2f", start);
				writer = new PrintWriter(new BufferedWriter(new FileWriter(output+"/out@"+formattedString+".txt", false)));
				writer.println(reader.readLine());
				while((line = reader.readLine())!= null)
				{
					String[] spl = line.split("\\s+");
					if(Double.parseDouble(spl[1]) < start)
						writer.println("0.00000 "+spl[1]+" "+spl[2]);
					else
						writer.println("1.00000 "+spl[1]+" "+spl[2]);
				}
				writer.close();
				start += 0.01;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
	}
}
