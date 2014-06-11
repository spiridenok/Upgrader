package wizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class GenerationDialog extends Dialog {	
	
	private Button button_ok;

	public GenerationDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		new Label(container, SWT.NONE);

		Label lblNewLabel = new Label(container, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblNewLabel.heightHint = 99;
		gd_lblNewLabel.widthHint = 380;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel
				.setText("Files are being generated, this could take some seconds. \r\nPlease wait.\r\nWhen this window disappears, all the required files will be in \r\nthe src-gen and patch_scripts-gen directories");
		
    	Cursor cursor = Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT);
    	parent.getShell().setCursor(cursor);
	    class MyRunnable implements Runnable {
	    	
	    	// Cannot pass just a single button because it's created after this thread is started
	    	// resulting in passing null as the button object.
	    	private GenerationDialog _dialog;
	    	private Display _display;
	    	
	    	public MyRunnable( Display display, GenerationDialog dialog )
	    	{
	    		_dialog = dialog;
	    		_display = display;
	    	}
	    	
			@Override
			public void run() {
		    	System.out.println("1");
		    	final int sec = 1;
		    	try {
					Thread.currentThread();
					Thread.sleep(sec * 1000 );
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	System.out.println("2");
		    	try {
					Thread.currentThread();
					Thread.sleep(sec * 1000 );
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	System.out.println("3");
		    	try {
					Thread.currentThread();
					Thread.sleep(sec * 1000 );
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	System.out.println("4");
		    	_display.asyncExec( new Runnable ()
		    	{
		    		public void run()
		    		{
				    	Cursor cursor = _display.getSystemCursor(SWT.CURSOR_ARROW);
				    	_display.getActiveShell().setCursor(cursor);
				    	_dialog.button_ok.setEnabled(true);
		    		}
		    	} );
			}
	      };
	    
	    new Thread( new MyRunnable( Display.getCurrent(), this )).start();  
		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("File Generation is progress...");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		button_ok = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button_ok.setEnabled(false);
		// createButton(parent, IDialogConstants.CANCEL_ID,
		// IDialogConstants.CANCEL_LABEL, false);
	}

}
