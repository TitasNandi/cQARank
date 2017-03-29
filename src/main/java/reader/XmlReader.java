package reader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class XmlReader 
{
	static String input;
	static int flag;
	public XmlReader(String inp, int flag)
	{
		input = inp;
		this.flag = flag;
	}
	/**
	 * This method initializes computation
	 */
	public static void initialize()
	{
		File inputFile = new File(input);
		File parent = inputFile.getParentFile();
		String pathgp = parent.getAbsolutePath();
		System.out.println(pathgp);
		System.out.println("XML parsing starts......");
		if(flag == 0)
		{
			File dir = new File(pathgp+"/parsed_files/");
			boolean success = dir.mkdirs();
			dir.setExecutable(true);
			dir.setReadable(true);
			dir.setWritable(true);
			parse(input+"/train.xml", pathgp+"/parsed_files/train.txt");
			//parse(input+"/dev.xml", pathgp+"/parsed_files/dev.txt");
			parse(input+"/test.xml", pathgp+"/parsed_files/test.txt");
		}
	}
	/**
	 * This method does additional parsing having user information
	 * @param input: input file
	 * @param output: output file
	 */
	public static void parse(String input, String output)                     //get user information from XML file          
    {
		File inputFile = new File(input);
		SAXReader reader = new SAXReader();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(output, false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Document document = reader.read(inputFile);
			List<Node> nodes = document.selectNodes("xml/OrgQuestion");          //Get users involved in Question-comment thread
			for(int i=0; i< nodes.size(); i++)
			{
				if(i % 10 == 0)
				{
					String oq_id = nodes.get(i).valueOf("@ORGQ_ID");
					String oq = nodes.get(i).selectSingleNode("OrgQSubject").getText().trim().replaceAll("\\s+", " ");
					String ob = nodes.get(i).selectSingleNode("OrgQBody").getText().trim().replaceAll("\\s+", " ");
					writer.println(oq_id);
					writer.println(oq+". "+ob);
				}
				List<Node> comment = nodes.get(i).selectNodes("Thread/RelComment");
			    for(int j=0; j<comment.size(); j++)
			    {
			    	String l = comment.get(j).selectSingleNode("RelCText").getText().trim().replaceAll("\\s+", " ");                        // Extract Comment
			    	String c_id = comment.get(j).valueOf("@RELC_ID");
			    	String qlabel = comment.get(j).valueOf("@RELC_RELEVANCE2ORGQ");
			    	String cuser = comment.get(j).valueOf("@RELC_USERID");
					String cusername = comment.get(j).valueOf("@RELC_USERNAME");
			    	writer.println(c_id+" "+qlabel+" "+cuser+" "+cusername);
		    		writer.println(l);
			    }
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.close();
    }
}
