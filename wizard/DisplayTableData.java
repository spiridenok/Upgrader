package wizard;

public class DisplayTableData {
	
	public String changed_item;
	public String change_type;
	public String upgrader;
	public boolean patch_scripts_needed;

	public DisplayTableData( String item, String type, String upgr, boolean scripts_needed) 
	{
		changed_item = item;
		change_type = type;
		upgrader = upgr;
		patch_scripts_needed = scripts_needed;
	}
}
