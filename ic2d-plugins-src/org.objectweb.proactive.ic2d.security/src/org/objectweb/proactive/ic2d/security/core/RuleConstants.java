package org.objectweb.proactive.ic2d.security.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class RuleConstants {

	public static final String STRING_REQUIRED = "required";

	public static final String STRING_OPTIONAL = "optional";

	public static final String STRING_DENIED = "denied";

	public static final int INT_REQUIRED = 0;

	public static final int INT_OPTIONAL = 1;

	public static final int INT_DENIED = 2;

	public static int valToInt(String val) {
		if (val.equals(STRING_REQUIRED)) {
			return INT_REQUIRED;
		} else if (val.equals(STRING_OPTIONAL)) {
			return INT_OPTIONAL;
		} else if (val.equals(STRING_DENIED)) {
			return INT_DENIED;
		}
		return -1;
	}

	public static String valToString(int val) {
		switch (val) {
		case INT_REQUIRED:
			return STRING_REQUIRED;
		case INT_OPTIONAL:
			return STRING_OPTIONAL;
		case INT_DENIED:
			return STRING_DENIED;
		default:
			return null;
		}
	}

	public static Combo createRODCombo(Composite parent) {
		Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		combo.add(RuleConstants.STRING_REQUIRED);
		combo.add(RuleConstants.STRING_OPTIONAL);
		combo.add(RuleConstants.STRING_DENIED);
		combo.select(1);

		return combo;
	}
}
