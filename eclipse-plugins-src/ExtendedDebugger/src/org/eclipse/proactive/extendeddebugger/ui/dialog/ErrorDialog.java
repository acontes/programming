package org.eclipse.proactive.extendeddebugger.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ErrorDialog extends Dialog{
	
	private Shell shell = null;

	public ErrorDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		Display display = getParent().getDisplay();

		/* Init the shell */
		shell = new Shell(getParent(), SWT.BORDER | SWT.CLOSE);
		shell.setText("Debugger Extended");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);

		Label titleLabel = new Label(shell, SWT.NONE);
		FormData titleLabelFormData = new FormData();
		titleLabelFormData.left = new FormAttachment(0, 0);
		titleLabel.setLayoutData(titleLabelFormData);

		final Label urlLabel = new Label(shell, SWT.NONE);
		urlLabel.setForeground(new Color(display, 255,0,0));
		urlLabel.setText("Connection failed, try again!");
		FormData urllabelFormData = new FormData();
		urllabelFormData.top = new FormAttachment(titleLabel, 10);
		urllabelFormData.left = new FormAttachment(0, 10);
		urlLabel.setLayoutData(urllabelFormData);

		
		// button "OK"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("ok");
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == okButton) {
					shell.close();
				}

			}
		});
		FormData okFormData = new FormData();
		okFormData.top = new FormAttachment(urlLabel, 15);
		okFormData.left = new FormAttachment(30, 0);
		okFormData.right = new FormAttachment(70, 0);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
