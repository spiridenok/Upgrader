package test;

import wizard.*; 

public class ManualDiffMain 
{
	public static void main(String[] args) 
	{
		ChangeModel model = new ChangeModel();
//		new ManualDiff(model, "C:\\Users\\dspirydz\\workspace_kepler_ddf_2\\test\\org", 
//							  "C:\\Users\\dspirydz\\workspace_kepler_ddf_2\\test\\new", 
//							  "MEENXAxMC:MACH_CONST_STRUCT" );
		new ManualDiff(model, "C:\\Users\\dspirydz\\workspace_kepler_ddf_2\\test\\org", 
							  "C:\\Users\\dspirydz\\workspace_kepler_ddf_2\\test\\new", 
				  			  "DNDMxTRxDEF:trace_info_struct" );
	}
}
