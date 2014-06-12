package wizard;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ParseDacnf {
	
	private String file_content;
	
	public ParseDacnf( String dacnf_file_name )
	{
		file_content = read_dacnf_file(dacnf_file_name);
	}
	
	public String get_const_file_name()
	{
		for( String line: file_content.split("\n") )
		{
			// TODO: this only detects the first constants definition, need to return all of them!
			if( !line.startsWith("#") )
			{
				return line.split(",")[2];
			}
		}
		return "";
	}
	
	public String get_const_data_type( String filename )
	{
		for( String line: file_content.split("\n") )
		{
			// TODO: this only detects the first constants definition, need to return all of them!
			if( !line.startsWith("#") )
			{
				return line.split(",")[3];				
			}
		}
		return "";
	}
	
	private static String read_dacnf_file(String filename)
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
