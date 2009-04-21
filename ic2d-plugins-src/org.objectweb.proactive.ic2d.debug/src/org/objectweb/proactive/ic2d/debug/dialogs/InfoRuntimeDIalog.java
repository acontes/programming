package org.objectweb.proactive.ic2d.debug.dialogs;

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

public class InfoRuntimeDIalog extends Dialog{
    private Shell shell = null;
    private Button okButton;

    //
    // -- CONSTRUCTOR ----------------------------------------------
    //
    /**
     * Constructor which create the dialog
     */
    public InfoRuntimeDIalog(Shell parent, String rumtimeUrl) {
        // Pass the default styles here
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

        /* Init the display */
        Display display = getParent().getDisplay();

        /* Init the shell */
        shell = new Shell(getParent(), SWT.BORDER | SWT.CLOSE);
        shell.setText("Debugger Tunneling informations");
        FormLayout layout = new FormLayout();
        layout.marginHeight = 5;
        layout.marginWidth = 5;
        shell.setLayout(layout);

        Label titleLabel = new Label(shell, SWT.NONE);
        titleLabel
                .setText("To use the Extended eclipse debugger");
        FormData titleLabelFormData = new FormData();
        titleLabelFormData.left = new FormAttachment(0, 0);
        titleLabel.setLayoutData(titleLabelFormData);

        // Port info
        Label portlabel = new Label(shell, SWT.NONE);
        portlabel.setText("runtime url : ");
        FormData portlabelFormData = new FormData();
        portlabelFormData.top = new FormAttachment(titleLabel, 15);
        portlabelFormData.left = new FormAttachment(0, 10);
        portlabel.setLayoutData(portlabelFormData);

        Text porttext = new Text(shell, SWT.BORDER);
        porttext.setText(rumtimeUrl);
        FormData porttextFormData = new FormData();
        porttextFormData.top = new FormAttachment(titleLabel, 12);
        porttextFormData.left = new FormAttachment(portlabel, 18);
        porttextFormData.right = new FormAttachment(95, 0);
        porttext.setLayoutData(porttextFormData);

        // button "OK"
        this.okButton = new Button(shell, SWT.PUSH);
        okButton.setText("Ok");
        okButton.addSelectionListener(new StepByStepDelayListener());
        FormData okFormData = new FormData();
        okFormData.top = new FormAttachment(portlabel, 20);
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

    //
    // -- INNER CLASS -----------------------------------------------
    //
    private class StepByStepDelayListener extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            if (e.widget == okButton) {
                shell.close();
            }

        }

    }
}
