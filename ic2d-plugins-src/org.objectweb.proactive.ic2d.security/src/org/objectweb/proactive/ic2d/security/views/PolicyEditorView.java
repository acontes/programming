package org.objectweb.proactive.ic2d.security.views;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.security.core.CertificateTree;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeList;
import org.objectweb.proactive.ic2d.security.core.SimplePolicyRule;
import org.objectweb.proactive.ic2d.security.tabs.CertificateGenerationTab;
import org.objectweb.proactive.ic2d.security.tabs.KeystoreTab;
import org.objectweb.proactive.ic2d.security.tabs.RuleTab;
import org.objectweb.proactive.ic2d.security.tabs.UpdatableTab;

public class PolicyEditorView extends ViewPart {

	public static final String ID = "org.objectweb.proactive.ic2d.security.views.PolicyEditorView";

	private CertificateTreeList keystore;

	private CTabFolder tabFolder;

	private ScrolledForm form;
	
	private CertificateGenerationTab cgt;
	private KeystoreTab kt;
	private RuleTab rt;

	public PolicyEditorView() {
		keystore = new CertificateTreeList();
	}

	@Override
	public void createPartControl(Composite parent) {

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		toolkit.setBorderStyle(SWT.BORDER);

		form = toolkit.createScrolledForm(parent);

		form.setText("Policy Editor");
		form.getBody().setLayout(new GridLayout());
		tabFolder = new CTabFolder(form.getBody(), SWT.FLAT | SWT.TOP);
		toolkit.adapt(tabFolder, true, true);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		Color selectedColor = toolkit.getColors()
				.getColor(FormColors.SEPARATOR);
		tabFolder.setSelectionBackground(new Color[] { selectedColor,
				toolkit.getColors().getBackground() }, new int[] { 50 });
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((UpdatableTab) tabFolder.getSelection()).update();
			}
		});

		toolkit.paintBordersFor(tabFolder);
		cgt = new CertificateGenerationTab(tabFolder, keystore, toolkit);
		kt = new KeystoreTab(tabFolder, keystore, toolkit);
		rt = new RuleTab(tabFolder, keystore, toolkit);

		tabFolder.setSelection(0);
	}

	public void update(CertificateTreeList list,
			List<SimplePolicyRule> policies, String appName, List<String> authorizedUsers) {
		if (list != null) {
			keystore.clear();
			keystore.addAll(list);
		}

		cgt.update();
		
		kt.update();
//		kt.select();
		
		rt.update();
		rt.setAppName(appName);
		rt.setRules(policies);
		rt.setAuthorizedUsers(authorizedUsers);
	}

	public CertificateTreeList getKeystore() {
		return keystore;
	}

	public String getAppName() {
		for (CTabItem item : tabFolder.getItems()) {
			if (item instanceof RuleTab) {
				RuleTab ruleTab = (RuleTab) item;
				return ruleTab.getAppName();
			}
		}
		return null;
	}

	public Map<CertificateTree, Boolean> getKeysToKeep() {
		for (CTabItem item : tabFolder.getItems()) {
			if (item instanceof KeystoreTab) {
				KeystoreTab keystoreTab = (KeystoreTab) item;
				return keystoreTab.getSelected();
			}
		}
		return null;
	}

	@Override
	public void setFocus() {
		form.setFocus();
	}

	public RuleTab getRt() {
		return rt;
	}

}
