package test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ParseDacnf {

	public static void main(String[] args) 
	{
		System.out.println( "Machine const type is " + read_mc_data_type( "MEEN_MC.dacnf" ) );
	}
	
	public static String read_mc_data_type( String filename )
	{
		for( String line: read_dacnf_file( filename ).split("\n") )
		{
			if( !line.startsWith("#") )
			{
				// TODO: this only detects the first constants definition, need to return all of them!
				return line.split(",")[3];				
			}
		}
		return "";
	}
	
	public static String read_dacnf_file(String filename)
	{
	   String content = null;
	   File file = new File(filename);
	   try 
	   {
	       FileReader reader = new FileReader(file);
	       char[] chars = new char[(int) file.length()];
	       reader.read(chars);
	       content = new String(chars);
	       reader.close();
	   } 
	   catch (IOException e) 
	   {
	       e.printStackTrace();
	   }
	   return content;
	}	

}
