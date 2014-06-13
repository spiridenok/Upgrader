package wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Table;

public class AnalysisResultPage extends WizardPage {

	private Composite container;
	private Table table;
	private ChangeModel model;
	
	public AnalysisResultPage( ChangeModel m ) {
		super("Second Page");
		setTitle("Results of constants analysis");
		setDescription("Followng are result of automatic analysis of constants DDF files");
		model = m;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		// required to avoid an error in the system
		setControl(container);
		
		table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);

		// For some reason set width on a column does not work...
		String[] titles = {"Changed item", "Change type",  "Upgrader Type", "Patch scripts"};
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
//			column.setAlignment(SWT.CENTER);
			column.setText(titles [i]);
			column.pack ();
		}
		
		GridData gd_table = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_table.heightHint = 192;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("Pressing finish will start process of generation of the required code, this might take some time..");
		
		setPageComplete(true);
	}

	public void display_results() 
	{
		table.setRedraw(false);
		table.removeAll();
		for( DisplayTableData data: model.get_changes() )
		{
			TableItem item = new TableItem (table, SWT.NONE);
			item.setText(0, data.changed_item);
			item.setText(1, data.change_type);
			item.setText(2, data.upgrader);
			item.setText(3, data.patch_scripts_needed? "Yes":"No");
		}
		table.getColumn(0).setWidth(480);

		table.setRedraw(true);
	}
	
	@Override
	public void setVisible(boolean visible) {
		if( visible )
			this.getShell().setSize(800, 600);
		super.setVisible(visible);
	}
}
