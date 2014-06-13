package wizard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class CodeGenerator {

	private final String SRC_GEN_DIR = "src-gen";
	private final String PATCH_GEN_DIR = "patch-gen";
	
	private void delay() {
		System.out.println("1");
		final int sec = 1;
		try {
			Thread.currentThread();
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		System.out.println("2");
//		try {
//			Thread.currentThread();
//			Thread.sleep(sec * 1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println("3");
//		try {
//			Thread.currentThread();
//			Thread.sleep(sec * 1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		System.out.println("4");
	}
	
	private void generate_src_struct()
	{
		new File( SRC_GEN_DIR ).mkdir();
	}
	
	private void gen_update_sh_script( String upgrader_name)
	{
		try( PrintWriter writer = new PrintWriter( SRC_GEN_DIR + "/" + upgrader_name, "UTF-8") )
		{
			writer.println("#!/bin/sh");
			writer.println( String.format("python -m %s 2> /dev/null", upgrader_name) );
			writer.close();		
		}	
		catch( IOException ex ) {
			ex.printStackTrace();
		}
	}
	
	private void gen_update_py_script()
	{
		try( PrintWriter writer = new PrintWriter( SRC_GEN_DIR + "/CCBB_upgrade.py", "UTF-8") )
		{
			writer.println("The first line");
			writer.println("The second line");
			writer.close();		
		}	
		catch( IOException ex ) {
			ex.printStackTrace();
		}
	}
	
	private void gen_pre_patch_script( String const_file_name ) {
	    try {
	    	String pre_patch_filename = PATCH_GEN_DIR + "/prepatch";
	    	// TODO: should not be copy but append to the pre-patch file.
			Files.copy( new File("src/PrepatchBackupFile.sh").toPath(), 
						new File(pre_patch_filename).toPath() );
			
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(pre_patch_filename, true)));
		    out.println("\nprepatch_backup_file \"${ASMHOME}\"" + const_file_name );
		    out.println("echo \"Backed up " + const_file_name + " file before patch install.\"");
		    out.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    try {
	    	String post_backout_filename = PATCH_GEN_DIR + "/postbackout";
	    	// TODO: should not be copy but append to the post-backout file.
			Files.copy( new File("src/PostbackoutRestoreBackupFile.sh").toPath(), 
						new File(post_backout_filename).toPath() );
			
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(post_backout_filename, true)));
		    out.println("\npostbackout_restore_backup \"${ASMHOME}\"" + const_file_name );
		    out.println("echo \"Restored after patch backout " + const_file_name + " file that was saved before patch install.\"");
		    out.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generate( ChangeModel model ) {

		removeDirectory(new File(PATCH_GEN_DIR));
		removeDirectory(new File(SRC_GEN_DIR));
		
		if (model.changed_type_and_default())
		{
			new File( PATCH_GEN_DIR ).mkdir();
			// TODO: const file name must be retrieved from the real dacnf file
			gen_pre_patch_script( model.get_const_file_name() );
		}

		if( model.need_specific_upgrader() )
		{
			generate_src_struct();
			// TODO: upgrader script must be retrieved from the wizard
			gen_update_sh_script( "CCBB_upgrade" );
			gen_update_py_script();
		}
		
		if( model.changed_len_var_array() )
			generate_src_struct();
		
		if( model.changed_scales() )
			generate_src_struct();
		
		//generate_test_doc();
		
		// TODO: remove delay later
		delay();
	}
	
	private static boolean removeDirectory(File directory) {
		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();

		// Some JVMs return null for File.list() when the
		// directory is empty.
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				// System.out.println("\tremoving entry " + entry);

				if (entry.isDirectory()) {
					if (!removeDirectory(entry))
						return false;
				} else {
					if (!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}
}
