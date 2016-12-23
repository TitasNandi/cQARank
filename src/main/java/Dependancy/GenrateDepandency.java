/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dependancy;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import writer.SVMWriter;

/**
 *
 * @author Deepak
 */
public class GenrateDepandency {

    public static void main(String[] args) throws IOException {
        GetDependencyParser gdp=new GetDependencyParser();
        
        GetDependencyParser.loadDependencyParser();
        File file = new File(args[0]);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        PrintWriter out2 = new PrintWriter(new BufferedWriter(new FileWriter(args[1]+"ids.txt", false)));
        PrintWriter out3 = new PrintWriter(new BufferedWriter(new FileWriter(args[1]+"doubleID.txt", false)));
        while((line = reader.readLine()) != null)
		{
			String splited[] = line.split("\\s+", 3);
			String qid = splited[0];
			out2.println(qid+".txt");
			out3.print(qid+".txt\t");
			String question = reader.readLine();
			ArrayList<String> qdp = GetDependencyParser.getDP(question);
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(args[1]+"/test_parses/"+qid+".txt", false)));
			Iterator itr = qdp.iterator();//getting Iterator from arraylist to traverse elements  
            String dep="";
            while (itr.hasNext()) {
                dep=dep+"\n"+itr.next().toString();
                
            }
            out.append(dep.trim());
            out.close();
			for(int i=0; i<10; i++)
			{
				line = reader.readLine();
				splited = line.split("\\s+", 4);
				String cid = splited[0];
				out2.println(cid+".txt");
				if(i < 9)
					out3.print(cid+".txt,");
				else
					out3.println(cid+".txt");
				String comment = reader.readLine();
				ArrayList<String> adp = GetDependencyParser.getDP(comment);
				out = new PrintWriter(new BufferedWriter(new FileWriter(args[1]+"/test_parses/"+cid+".txt", false)));
				itr = adp.iterator();//getting Iterator from arraylist to traverse elements  
	            dep="";
	            while (itr.hasNext()) {
	                dep=dep+"\n"+itr.next().toString();
	            }
	            out.append(dep.trim());
	            out.close();
			}
		}
        out2.close();
        out3.close();
    }
}
