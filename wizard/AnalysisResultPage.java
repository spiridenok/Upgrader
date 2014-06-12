package wizard;

import javax.swing.GroupLayout.Alignment;

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

		// For some reason set width on a column does not work...
		String[] titles = {"Changed item", "Change type",  "Upgrader Type", "Patch scripts"};
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
//			column.setAlignment(SWT.CENTER);
			column.setText(titles [i]);
		}	
		
		TableItem item = new TableItem (table, SWT.NONE);
		item.setText(0, "top_level.bla.data.something");
		item.setText(1, "default");
		item.setText(2, "None");
		item.setText(3, "Yes");
		
		item = new TableItem (table, SWT.NONE);
		item.setText(0, "top_level.something_totally_different.test");
		item.setText(1, "type");
		item.setText(2, "Copy");
		item.setText(3, "No");
		
		item = new TableItem (table, SWT.NONE);
		item.setText(0, "top_level.some_different.struct.changed");
		item.setText(1, "scale");
		item.setText(2, "Copy");
		item.setText(3, "Yes");
		
		for (int i=0; i<titles.length; i++) {
			table.getColumn(i).pack ();
		}
		
		GridData gd_table = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_table.heightHint = 192;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("Pressing finish will start process of generation of the required code, this might take some time...");
		
		setPageComplete(true);
	}
}
