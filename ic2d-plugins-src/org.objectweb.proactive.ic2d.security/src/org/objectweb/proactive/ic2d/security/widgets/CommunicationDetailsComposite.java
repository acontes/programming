package org.objectweb.proactive.ic2d.security.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.objectweb.proactive.core.security.Communication;

public class CommunicationDetailsComposite extends Composite {

	private Text communicationText;

	private Text authenticationText;

	private Text confidentialityText;

	private Text integrityText;

	public CommunicationDetailsComposite(Composite parent, FormToolkit toolkit,
			String name) {
		super(parent, SWT.NULL);
		toolkit.adapt(this);

		super.setLayout(new GridLayout());

		toolkit.createLabel(this, name);

		toolkit.createLabel(this, "Communication :");
		this.communicationText = toolkit.createText(this, "");
		this.communicationText.setEditable(false);
		this.communicationText.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				true, false));

		toolkit.createLabel(this, "Authentication :");
		this.authenticationText = toolkit.createText(this, "");
		this.authenticationText.setEditable(false);
		this.authenticationText.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				true, false));

		toolkit.createLabel(this, "Confidentiality :");
		this.confidentialityText = toolkit.createText(this, "");
		this.confidentialityText.setEditable(false);
		this.confidentialityText.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				true, false));

		toolkit.createLabel(this, "Integrity :");
		this.integrityText = toolkit.createText(this, "");
		this.integrityText.setEditable(false);
		this.integrityText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
	}

	public void updateCommunication(Communication c) {
		this.communicationText.setText(c.getCommunication() ? "Authorized"
				: "Forbidden");
		this.authenticationText.setText(c.getAuthentication().toString());
		this.confidentialityText.setText(c.getConfidentiality().toString());
		this.integrityText.setText(c.getIntegrity().toString());
	}
}
