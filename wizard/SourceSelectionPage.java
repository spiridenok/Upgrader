package wizard;

import javax.swing.JFileChooser;

public class SourceSelectionPage extends WizardPage {

	private Composite container;
	private Label lbl_new_dir;
	private Label lbl_old_dir;
	private Label lbl_dacnf_file;
	
	private boolean new_dir_selected = false;
	private boolean dacnf_file_selected = false;
	
	private ChangeModel model;
	
	// TODO: this is a dirty hack - pages should not know each other.
	// Must be cleaned up later!
	private AnalysisResultPage res_page;
	
	public SourceSelectionPage( ChangeModel m, AnalysisResultPage result_page ) {
		super("First Page");
		setTitle("Specify Files");
		setDescription("Please specify constant files and directories");
		model = m;
		res_page = result_page;
	}

	public void validate_page()
	{
		setPageComplete( dacnf_file_selected );
//		setPageComplete( dacnf_file_selected && new_dir_selected );
//		setPageComplete( true );
	}
	
	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		container.setLayout(layout);

		Label label1 = new Label(container, SWT.NONE);

		label1.setText("Please open the directory where new (patch) DDF files are located.");

		// required to avoid an error in the system

		setControl(container);
		
				Button btnNewDir = new Button(container, SWT.NONE);
				btnNewDir.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);				
						dialog.setMessage("Select directory with patch files");
						String selected_dir = dialog.open();
						if( selected_dir != null )
						{
							lbl_new_dir.setText(selected_dir);
							new_dir_selected = true;
							validate_page();
						}
					}
				});
				GridData gd_btnNewDir = new GridData(SWT.LEFT, SWT.CENTER, false,
						false, 1, 1);
				gd_btnNewDir.widthHint = 118;
				btnNewDir.setLayoutData(gd_btnNewDir);
				btnNewDir.setText("Browse New Dir...");

		lbl_new_dir = new Label(container, SWT.NONE);
		GridData gd_lbl_new_dir = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_new_dir.widthHint = 423;
		lbl_new_dir.setLayoutData(gd_lbl_new_dir);
		lbl_new_dir.setText("<no dir selected>");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblPleaseOpenThe = new Label(container, SWT.NONE);
		lblPleaseOpenThe
				.setText("Please open the directory where old (befor patch) DDF files are located.");
		
				Button btnOldDir = new Button(container, SWT.NONE);
				btnOldDir.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);				
						dialog.setMessage("Select directory with old files (before patch)");
						String selected_dir = dialog.open();
						if( selected_dir != null )
							lbl_old_dir.setText(selected_dir);
					}
				});
				btnOldDir.setText("Browse Old Dir...");

		lbl_old_dir = new Label(container, SWT.NONE);
		GridData gd_lbl_old_dir = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_old_dir.widthHint = 428;
		lbl_old_dir.setLayoutData(gd_lbl_old_dir);
		lbl_old_dir.setText("<no dir selected>");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblSelectDacnfFile = new Label(container, SWT.NONE);
		lblSelectDacnfFile.setText("Select dacnf file:");
		
				Button btnDacnfFile = new Button(container, SWT.NONE);
				btnDacnfFile.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						FileDialog dialog = new FileDialog(getShell());
						dialog.setFilterExtensions(new String[] { "*.dacnf" });
						String selected_file = dialog.open();
						if( selected_file != null )
						{
							lbl_dacnf_file.setText(selected_file);
							dacnf_file_selected = true;
							validate_page();
						}
					}
				});
				btnDacnfFile.setText("Open dacnf file...");
		
		lbl_dacnf_file = new Label(container, SWT.NONE);
		GridData gd_lbl_dacnf_file = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_dacnf_file.widthHint = 429;
		lbl_dacnf_file.setLayoutData(gd_lbl_dacnf_file);
		lbl_dacnf_file.setText("<no file selected>");
		new Label(container, SWT.NONE);
		
		// For testing only:
		lbl_new_dir.setText("C:\\Users\\dspirydz\\Desktop\\new");
		lbl_old_dir.setText("C:\\Users\\dspirydz\\Desktop\\old");
		lbl_dacnf_file.setText("H:\\garbage\\MEEN_MC.dacnf");
		dacnf_file_selected = true;
		
		validate_page();
	}
	
	@Override
	public IWizardPage getNextPage() {
		return super.getNextPage();
	}
	
	@Override
	public void setVisible(boolean visible) {
		// Page gets hidden -> "Next" is clicked on it
		if( !visible )
		{
			model.calculate_difference( lbl_old_dir.getText(), lbl_new_dir.getText(), lbl_dacnf_file.getText() );
			res_page.display_results();
		}
		else
		{
			this.getShell().setSize(600, 500);
		}
		super.setVisible(visible);
	}
}
