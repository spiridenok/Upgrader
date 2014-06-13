package wizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

public class MyWizard extends Wizard {

	private ChangeModel model;
	protected SourceSelectionPage source_selection_page;
	protected AnalysisResultPage analysis_result_page;
	protected OptionalPage optional_page = new OptionalPage();
	
	private WizardPage previously_opened_page = null;  
	
	public MyWizard( ChangeModel m ) {
		super();
		setNeedsProgressMonitor(true);
		model = m;
		analysis_result_page = new AnalysisResultPage( model );
		source_selection_page = new SourceSelectionPage( model, analysis_result_page );
	}

	@Override
	public String getWindowTitle() {
		return "Export My Data";
	}

	@Override
	public void addPages() {
		addPage(source_selection_page);
//		addPage(optional_page);
		addPage(analysis_result_page);
	}

	@Override
	public boolean performFinish() {
		// Print the result to the console
		return true;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
//		String name = previously_opened_page==null? "NULL": previously_opened_page.getName();
//		System.out.println( page.getName()  + " --- prev: " + name);
		if( previously_opened_page == source_selection_page )
		{
			if( page == source_selection_page )
			{
				model.calculate_difference( source_selection_page.lbl_old_dir.getText(), 
											source_selection_page.lbl_new_dir.getText(), 
											source_selection_page.lbl_dacnf_file.getText() );
				if( model.has_unresolved_ddfs() )
				{
					String message = "Following unresolved DDFs found:\n";
					for( String unresolved_ddf: model.get_unresolved_ddfs() )
						message += "\t" + unresolved_ddf + "\n";
					
					message += "\nPress 'OK' to continue. However in this case the analysis will be incomplete possibly resulting in missing files.\n";
					message += "\nPress 'Back' to return to the previous sceen, then add missing DDFs to the directories, click again on 'Next'.";
					
					MessageDialog dialog = new MessageDialog(this.getShell(), 
							"Unresolved DDFs found", 
							null, 
							message, 
							MessageDialog.WARNING, 
							new String[] { "OK", "BACK"}, 
							0);
					if( dialog.open() != 0 )
					{
						return source_selection_page;
					}
				}
			}
			analysis_result_page.display_results();
//			return analysis_result_page;
		}
		
		previously_opened_page = (WizardPage)page;
		return super.getNextPage(page);
	}
}
