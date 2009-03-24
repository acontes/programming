package org.eclipse.proactive.debug.proactivefilter.ui.dialog;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FilterDialog extends Dialog{
	
	private Shell shell = null;
	
	// Filter info
	private String  threadRegex;
	private String  stackRegex;
	/**
	 * 1 AutomaticMode
	 * 2 ManualMode
	 * 3 NoMode (if the popup close without selected choice)
	 */
	private int mode  = 3;
	private int modeTmp = 3;
	
	public FilterDialog(Shell parent, TreeViewer viewer) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		Display display = getParent().getDisplay();

		/* Init the shell */
		shell = new Shell(getParent(), SWT.BORDER | SWT.CLOSE);
		shell.setText("ProActive Filter");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);

		Label titleLabel = new Label(shell, SWT.NONE);
		FormData titleLabelFormData = new FormData();
		titleLabelFormData.left = new FormAttachment(0, 0);
		titleLabel.setLayoutData(titleLabelFormData);

		// checkboxs
		final Button checkAuto = new Button(shell, SWT.CHECK);
		checkAuto.setText("Use an automatic filter");
		FormData checkAutoFormData = new FormData();
		checkAutoFormData.top = new FormAttachment(titleLabel, 10);
		checkAutoFormData.left = new FormAttachment(0, 10);
		checkAuto.setLayoutData(checkAutoFormData);
		
		final Button checkManual = new Button(shell, SWT.CHECK);
		checkManual.setText("Use a manual filter");
		FormData checkManualFormData = new FormData();
		checkManualFormData.top = new FormAttachment(checkAuto, 10);
		checkManualFormData.left = new FormAttachment(0, 10);
		checkManual.setLayoutData(checkManualFormData);
		
		// separator
		Label separator1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
		FormData separator1FormData = new FormData();
		separator1FormData.width = 420;
		separator1FormData.top = new FormAttachment(checkManual,10);
		separator1.setLayoutData(separator1FormData);

		// Thread regex
		String threadToolTipsText = "Enter a java regular expression to select the threads to keep";
		final Label threadLabel = new Label(shell, SWT.NONE);
		threadLabel.setText("Thread Regex: ");
		FormData hostlabelFormData = new FormData();
		hostlabelFormData.top = new FormAttachment(separator1, 10);
		hostlabelFormData.left = new FormAttachment(0, 10);
		threadLabel.setLayoutData(hostlabelFormData);
		threadLabel.setToolTipText(threadToolTipsText);
		threadLabel.setEnabled(false);

		final Text threadText = new Text(shell, SWT.BORDER);
		FormData threadtextFormData = new FormData();
		threadtextFormData.width = 300;
		threadtextFormData.top = new FormAttachment(separator1, 10);
		threadtextFormData.left = new FormAttachment(threadLabel, 15);
		threadText.setLayoutData(threadtextFormData);
		threadText.setToolTipText(threadToolTipsText);
		threadText.setEnabled(false);

		// Stack regex
		String stackToolTipsText = "Enter a java regular expression to select the stack Trace to keep";
		final Label stackLabel = new Label(shell, SWT.NONE);
		stackLabel.setText("Stack Regex: ");
		FormData portlabelFormData = new FormData();
		portlabelFormData.top = new FormAttachment(threadLabel, 15);
		portlabelFormData.left = new FormAttachment(0, 10);
		stackLabel.setLayoutData(portlabelFormData);
		stackLabel.setToolTipText(stackToolTipsText);
		stackLabel.setEnabled(false);

		final Text stackText = new Text(shell, SWT.BORDER);
		FormData stacktextFormData = new FormData();
		stacktextFormData.width = 300;
		stacktextFormData.top = new FormAttachment(threadLabel, 12);
		stacktextFormData.left = new FormAttachment(threadLabel, 15);
		stackText.setLayoutData(stacktextFormData);
		stackText.setToolTipText(stackToolTipsText);
		stackText.setEnabled(false);

		// separator
		Label separator2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
		FormData separator2FormData = new FormData();
		separator2FormData.width = 420;
		separator2FormData.top = new FormAttachment(stackText,10);
		separator2.setLayoutData(separator2FormData);
		
		// button "OK"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Filter");
		okButton.setEnabled(false);
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == okButton) {
					threadRegex = threadText.getText();
					stackRegex = stackText.getText();
					mode = modeTmp;
					shell.close();
				}

			}
		});
		FormData okFormData = new FormData();
		okFormData.top = new FormAttachment(separator2, 10);
		okFormData.left = new FormAttachment(30, 0);
		okFormData.right = new FormAttachment(70, 0);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		//
		// -- Listeners -----------------------------------------------
		//
		checkAuto.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				if(modeTmp == 3){
					modeTmp = 1;
					checkManual.setEnabled(false);
					okButton.setEnabled(true);
				} else {
					modeTmp = 3;
					checkManual.setEnabled(true);
					okButton.setEnabled(false);
				}
			}
		});
		checkManual.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				if(modeTmp == 3){
					modeTmp = 2;
					checkAuto.setEnabled(false);
					threadLabel.setEnabled(true);
					threadText.setEnabled(true);
					stackLabel.setEnabled(true);
					stackText.setEnabled(true);
					okButton.setEnabled(true);
				} else {
					modeTmp = 3;
					checkAuto.setEnabled(true);
					threadLabel.setEnabled(false);
					threadText.setEnabled(false);
					stackLabel.setEnabled(false);
					stackText.setEnabled(false);
					okButton.setEnabled(false);
				}
			}
		});
		
		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
    //
    // --GETTER AND SETTER -----------------------------------------------
    //
	public String getThreadRegex() {
		return threadRegex;
	}

	public void setThreadRegex(String threadRegex) {
		this.threadRegex = threadRegex;
	}

	public String getStackRegex() {
		return stackRegex;
	}

	public void setStackRegex(String stackRegex) {
		this.stackRegex = stackRegex;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
}
