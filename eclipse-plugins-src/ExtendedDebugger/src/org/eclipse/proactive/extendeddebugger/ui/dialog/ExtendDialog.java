package org.eclipse.proactive.extendeddebugger.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ExtendDialog extends Dialog{
	
	private Shell shell = null;
	private String url;
	private String port;

	public ExtendDialog(Shell parent) {
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
		urlLabel.setText("host: ");
		FormData urllabelFormData = new FormData();
		urllabelFormData.top = new FormAttachment(titleLabel, 10);
		urllabelFormData.left = new FormAttachment(0, 10);
		urlLabel.setLayoutData(urllabelFormData);

		final Text urlText = new Text(shell, SWT.BORDER);
		FormData urltextFormData = new FormData();
		urltextFormData.width = 300;
		urltextFormData.top = new FormAttachment(titleLabel, 10);
		urltextFormData.left = new FormAttachment(urlLabel, 15);
		urlText.setLayoutData(urltextFormData);
		
		final Label portLabel = new Label(shell, SWT.NONE);
		portLabel.setText("Port: ");
		FormData portLabelFormData = new FormData();
		portLabelFormData.top = new FormAttachment(urlLabel, 10);
		portLabelFormData.left = new FormAttachment(0, 10);
		portLabel.setLayoutData(portLabelFormData);

		final Text portText = new Text(shell, SWT.BORDER);
		FormData portTextFormData = new FormData();
		portTextFormData.width = 300;
		portTextFormData.top = new FormAttachment(urlLabel, 10);
		portTextFormData.left = new FormAttachment(portLabel, 15);
		portText.setLayoutData(portTextFormData);
		
		// button "OK"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Send");
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == okButton) {
					url = urlText.getText();
					port = portText.getText();
					shell.close();
				}

			}
		});
		FormData okFormData = new FormData();
		okFormData.top = new FormAttachment(portLabel, 10);
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
	
	public String getUrl() {
		return url;
	}
	
	public String getPort(){
		return port;
	}
}
