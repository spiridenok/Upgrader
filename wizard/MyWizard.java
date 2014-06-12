package wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class MyWizard extends Wizard {

	private ChangeModel model;
	protected SourceSelectionPage source_selection_page;
	protected AnalysisResultPage analysis_result_page;
	protected OptionalPage optional_page = new OptionalPage();
	
	public MyWizard( ChangeModel m ) {
		super();
		setNeedsProgressMonitor(true);
		model = m;
		source_selection_page = new SourceSelectionPage( model );
		analysis_result_page = new AnalysisResultPage( model );
	}

	@Override
	public String getWindowTitle() {
		return "Export My Data";
	}

	@Override
	public void addPages() {
		addPage(source_selection_page);
		addPage(optional_page);
		addPage(analysis_result_page);
	}

	@Override
	public boolean performFinish() {
		// Print the result to the console
		return true;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if( false )
			return optional_page;
		if( page == source_selection_page )
			return analysis_result_page;
		return super.getNextPage(page);
	}
}
