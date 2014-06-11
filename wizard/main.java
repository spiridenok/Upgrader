package wizard;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class main {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell( display, SWT.BORDER );
	    WizardDialog wizardDialog = new WizardDialog(shell,new MyWizard());
	    if (wizardDialog.open() == Window.OK) {
	    	System.out.println("Ok pressed");

	    	GenerationDialog gd = new GenerationDialog(shell);
	    	gd.open();
	    	
	    } else {
	    	System.out.println("Cancel pressed");
	    }		
	}
}
