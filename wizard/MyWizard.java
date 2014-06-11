package wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

public class MyWizard extends Wizard {

	protected SourceSelectionPage source_selection_page = new SourceSelectionPage();
	protected AnalysisResultPage analysis_result_page = new AnalysisResultPage();
	protected OptionalPage optional_page = new OptionalPage();
	
	private String[] result = { "Result 1", "Result 2", "End Result" };

	public MyWizard() {
		super();
		setNeedsProgressMonitor(true);
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
