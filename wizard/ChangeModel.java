package wizard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChangeModel {
	
	private ParseDacnf dacnf;
	private List<String> changed_defaults = new ArrayList<String>();
	private List<String> changed_types = new ArrayList<String>();
	private Set<String> unresolved_ddfs = new HashSet<String>();

	public void calculate_difference( String old_files_path, String new_files_path, String dacnf_file_name ) {
		System.out.println("Calculating differences started...");
		changed_defaults.clear();
		changed_types.clear();
		unresolved_ddfs.clear();
		
//		try {
//			Thread.currentThread();
//			Thread.sleep(1 * 1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		dacnf = new ParseDacnf(dacnf_file_name);
		System.out.println( "Data type " + dacnf.get_const_data_type() );		
		
		// TODO: clean this up - this constructor is one big side-effect.
		// Make it implementation less surprising for developers. 
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
		return !changed_defaults.isEmpty() && !changed_types.isEmpty();
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
	
	public void add_unresolved_ddf( String ddf_file )
	{
//		System.out.println( "add unresolved " + ddf_file );
		unresolved_ddfs.add(ddf_file);
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

	public boolean has_unresolved_ddfs() {
//		System.out.println( "has?");
		return !unresolved_ddfs.isEmpty();
	}

	public Set<String> get_unresolved_ddfs() {
		return unresolved_ddfs;
	}
}
