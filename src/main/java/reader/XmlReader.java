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
		System.out.println("XML parsing starts......");
		File dir = new File(pathgp+"/parsed_files/");
		boolean success = dir.mkdirs();
		dir.setExecutable(true);
		dir.setReadable(true);
		dir.setWritable(true);
		if(flag == 0)
		{
			old_parse(input+"/train.xml", pathgp+"/parsed_files/train.txt");
			old_parse(input+"/test.xml", pathgp+"/parsed_files/test.txt");
		}
		else
		{
			new_parse(input+"/train.xml", pathgp+"/parsed_files/train.txt");
			new_parse(input+"/test.xml", pathgp+"/parsed_files/test.txt");
		}
		
	}
	/**
	 * This method parses XML file if the data format follows SemEval 2016-17 task
	 * @param input: input file
	 * @param output: output file
	 */
	public static void new_parse(String input, String output)                     //get user information from XML file          
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
			List<Node> nodes = document.selectNodes("xml/OrgQuestion");          
			for(int i=0; i< nodes.size(); i++)
			{
				String p = nodes.get(i).selectSingleNode("Thread/RelQuestion/RelQSubject").getText().trim().replaceAll("\\s+", " ");
				String l = nodes.get(i).selectSingleNode("Thread/RelQuestion/RelQBody").getText().trim().replaceAll("\\s+", " ");           //Extract Question
				String q_id = nodes.get(i).selectSingleNode("Thread/RelQuestion").valueOf("@RELQ_ID");
				String quser = nodes.get(i).selectSingleNode("Thread/RelQuestion").valueOf("@RELQ_USERID");
				String qusername = nodes.get(i).selectSingleNode("Thread/RelQuestion").valueOf("@RELQ_USERNAME");
				writer.println(q_id+" 10 "+quser+" "+qusername);
				writer.println(p+". "+l);
			    List<Node> comment = nodes.get(i).selectNodes("Thread/RelComment");
			    for(int j=0; j<comment.size(); j++)
			    {
			    	l = comment.get(j).selectSingleNode("RelCText").getText().trim().replaceAll("\\s+", " ");                        // Extract Comment
			    	String c_id = comment.get(j).valueOf("@RELC_ID");
			    	String rlabel = comment.get(j).valueOf("@RELC_RELEVANCE2RELQ");
			    	String cuser = comment.get(j).valueOf("@RELC_USERID");
					String cusername = comment.get(j).valueOf("@RELC_USERNAME");
			    	writer.println(c_id+" "+rlabel+" "+cuser+" "+cusername);
		    		writer.println(l);
			    }
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.close();
    }
	
	/**
	 * This method parses XML file if the data format follows SemEval 2015 task
	 * @param input: input file
	 * @param output: output file
	 */
	public static void old_parse(String input, String output)                               
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
			List<Node> nodes = document.selectNodes("xml/Thread");         
	    	for(int i=0; i<nodes.size(); i++)
	    	{
	    		String q_id = nodes.get(i).selectSingleNode("RelQuestion").valueOf("@RELQ_ID");
	    		String p = nodes.get(i).selectSingleNode("RelQuestion/RelQSubject").getText().trim().replaceAll("\\s+", " ");
	    		String l = nodes.get(i).selectSingleNode("RelQuestion/RelQBody").getText().trim().replaceAll("\\s+", " ");
	    		String user_name = nodes.get(i).selectSingleNode("RelQuestion").valueOf("@RELQ_USERNAME");
	    		List<Node> comment = nodes.get(i).selectNodes("RelComment");
	    		if(comment.size() != 0)
	    		{
	    			writer.println(q_id+" "+comment.size()+" "+nodes.get(i).selectSingleNode("RelQuestion").valueOf("@RELQ_USERID")+" "+user_name);
	    			writer.println(p+". "+l);
	    		}
	    		
			    for(int j=0; j<comment.size(); j++)
			    {
			    	l = comment.get(j).selectSingleNode("RelCText").getText().trim().replaceAll("\\s+", " ");
			    	String c_id = comment.get(j).valueOf("@RELC_ID");
			    	String label = comment.get(j).valueOf("@RELC_RELEVANCE2RELQ");
			    	String commenter_id = comment.get(j).valueOf("@RELC_USERID");
			    	String commenter_name = comment.get(j).valueOf("@RELC_USERNAME");
			    	writer.println(c_id+" "+label+" "+commenter_id+" "+commenter_name);
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
