package wizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

public class MyWizard extends Wizard {

	protected SourceSelectionPage one;
	protected AnalysisResultPage two;
	
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
		addPage(new SourceSelectionPage());
		addPage(new AnalysisResultPage());
	}

	@Override
	public boolean performFinish() {
		// Print the result to the console
		return true;
	}
}
