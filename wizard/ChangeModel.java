package wizard;

import java.security.spec.DSAParameterSpec;
import java.util.ArrayList;
import java.util.List;

public class ChangeModel {
	
	private ParseDacnf dacnf;
	private List<String> changed_defaults = new ArrayList<String>();
	private List<String> changed_types = new ArrayList<String>();

	public void calculate_difference( String old_files_path, String new_files_path, String dacnf_file_name ) {
		System.out.println("Calculating differences started...");
		changed_defaults = new ArrayList<String>();
		changed_types = new ArrayList<String>();
		
//		try {
//			Thread.currentThread();
//			Thread.sleep(1 * 1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		dacnf = new ParseDacnf(dacnf_file_name);
		System.out.println( "Data type " + dacnf.get_const_data_type() );		
		
		ManualDiff diff = new ManualDiff( this, old_files_path, new_files_path, dacnf.get_const_data_type() );
		
		System.out.println("Calculating differences finished.");
	}

	public boolean changed_scales() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean changed_len_var_array() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean need_specific_upgrader() {
		// TODO Auto-generated method stub
		return true;
	}

	boolean changed_type_and_default() {
		return true;
	}

	String get_const_file_name()
	{
		return dacnf.get_const_file_name();
	}

	public void add_changed_default( String var )
	{
		changed_defaults.add(var);
	}
	
	public void add_changed_type( String var )
	{
		changed_types.add(var);
	}
	
	public List<DisplayTableData> get_changes()
	{
		List<DisplayTableData> data = new ArrayList<DisplayTableData>();
		for( String var: changed_defaults )
		{
			data.add( new DisplayTableData( var, "default", "None", false ) );
		}
		for( String var: changed_types )
		{
			data.add( new DisplayTableData( var, "type", "None", true ) );
		}
		
		return data;
	}
}
