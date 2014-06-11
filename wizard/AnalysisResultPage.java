package wizard;

import javax.swing.GroupLayout.Alignment;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;

public class AnalysisResultPage extends WizardPage {

	private Composite container;
	private Table table;

	public AnalysisResultPage() {

		super("Second Page");
		setTitle("Results of constants analysis");
		setDescription("Followng are result of automatic analysis of constants DDF files");
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
		String[] titles = {"Changed item", "Change type",  "Upgrader?"};
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
//			column.setAlignment(SWT.CENTER);
			column.setText(titles [i]);
		}	
		
		TableItem item = new TableItem (table, SWT.NONE);
		item.setText(0, "tc_main.bla.data.something");
		item.setText(1, "default");
		item.setText(2, "NO");
		
		item = new TableItem (table, SWT.NONE);
		item.setText(0, "tc_main.something_totally_different.test");
		item.setText(1, "type");
		item.setText(2, "YES");

		for (int i=0; i<titles.length; i++) {
			table.getColumn(i).pack ();
		}
		
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = 182;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		setPageComplete(true);
	}
}
