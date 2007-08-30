package org.objectweb.proactive.ic2d.security.tabs;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.objectweb.proactive.core.security.SecurityConstants;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.ic2d.security.core.CertificateTree;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeList;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeMap;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeMapTransfer;
import org.objectweb.proactive.ic2d.security.core.PolicyFile;
import org.objectweb.proactive.ic2d.security.core.PolicyTools;
import org.objectweb.proactive.ic2d.security.core.RuleConstants;
import org.objectweb.proactive.ic2d.security.core.SimplePolicyRule;
import org.objectweb.proactive.ic2d.security.widgets.CertificateTreeListSection;
import org.objectweb.proactive.ic2d.security.widgets.EntityTableComposite;

public class RuleTab extends UpdatableTab {
	private CertificateTreeList activeKeystore;

	private CertificateTreeListSection activeKeystoreSection;

	private List<SimplePolicyRule> rules;

	private List<TypedCertificate> authorizedUsers;

	private FormToolkit toolkit;

	private EntityTableComposite fromTable;

	private EntityTableComposite toTable;

	private Button requestCheck;

	private Combo reqAuthCombo;

	private Combo reqIntCombo;

	private Combo reqConfCombo;

	private Button replyCheck;

	private Combo repAuthCombo;

	private Combo repIntCombo;

	private Combo repConfCombo;

	private Button aoCreationCheck;

	private Button migrationCheck;

	private Table rulesTable;

	private TableViewer rulesTableViewer;

	private Table usersTable;

	private TableViewer usersTableViewer;

	private Text applicationNameText;

	private Text keystoreText;

	public RuleTab(CTabFolder folder, CertificateTreeList keystore,
			FormToolkit toolkit) {
		super(folder, SWT.NULL);
		setText("Rule Editor");

		this.activeKeystore = keystore;
		this.toolkit = toolkit;
		rules = new ArrayList<SimplePolicyRule>();
		authorizedUsers = new ArrayList<TypedCertificate>();

		Composite body = toolkit.createComposite(folder);
		body.setLayout(new GridLayout(3, true));

		createSectionActiveKeystore(body).setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true));

		createSectionRuleEdition(body).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		createSectionRules(body).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		updateRuleEditor();

		setControl(body);
	}
	
	private Section createSectionActiveKeystore(Composite parent) {
		activeKeystoreSection = new CertificateTreeListSection(parent, toolkit,
				"ActiveKeystore", activeKeystore, false, true, false, false);
		return activeKeystoreSection.get();
	}

	private Section createSectionRuleEdition(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText("Rule edition");

		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout());

		toolkit.createLabel(client, "From");
		fromTable = new EntityTableComposite(client, toolkit, rules, true);
		fromTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		toolkit.createLabel(client, "To");
		toTable = new EntityTableComposite(client, toolkit, rules, false);
		toTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		createCheckRequest(client);

		createCompositeRequest(client).setLayoutData(
				new GridData(SWT.FILL, SWT.TOP, true, false));

		createCheckReply(client);

		createCompositeReply(client).setLayoutData(
				new GridData(SWT.FILL, SWT.TOP, true, false));

		createCheckAoCreation(client);

		createCheckMigration(client);

		section.setClient(client);

		return section;
	}

	private Button createCheckRequest(Composite parent) {
		requestCheck = toolkit.createButton(parent, "Authorize requests",
				SWT.CHECK);
		requestCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rules.get(rulesTable.getSelectionIndex()).setRequest(
						requestCheck.getSelection());
				enableRequestEditor(requestCheck.getSelection());

				super.widgetSelected(e);
			}
		});

		return requestCheck;
	}

	private Composite createCompositeRequest(Composite parent) {
		Composite client = toolkit.createComposite(parent);
		client.setLayout(new GridLayout(3, true));

		toolkit.createLabel(client, "Authentication");
		toolkit.createLabel(client, "Confidentiality");
		toolkit.createLabel(client, "Integrity");

		reqAuthCombo = RuleConstants.createRODCombo(client);
		reqAuthCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rules.get(rulesTable.getSelectionIndex()).setReqAuth(
						reqAuthCombo.getSelectionIndex());

				super.widgetSelected(e);
			}
		});
		reqConfCombo = RuleConstants.createRODCombo(client);
		reqConfCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rules.get(rulesTable.getSelectionIndex()).setReqConf(
						reqConfCombo.getSelectionIndex());

				super.widgetSelected(e);
			}
		});
		reqIntCombo = RuleConstants.createRODCombo(client);
		reqIntCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rules.get(rulesTable.getSelectionIndex()).setReqInt(
						reqIntCombo.getSelectionIndex());

				super.widgetSelected(e);
			}
		});

		return client;
	}

	private Button createCheckReply(Composite parent) {
		replyCheck = toolkit.createButton(parent, "Authorize reply", SWT.CHECK);
		replyCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rules.get(rulesTable.getSelectionIndex()).setReply(
						replyCheck.getSelection());
				enableReplyEditor(replyCheck.getSelection());

				super.widgetSelected(e);
			}
		});

		return replyCheck;
	}

	private Composite createCompositeReply(Composite parent) {
		Composite client = toolkit.createComposite(parent);
		client.setLayout(new GridLayout(3, true));

		toolkit.createLabel(client, "Authentication");
		toolkit.createLabel(client, "Confidentiality");
		toolkit.createLabel(client, "Integrity");

		repAuthCombo = RuleConstants.createRODCombo(client);
		repAuthCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rules.get(rulesTable.getSelectionIndex()).setRepAuth(
						repAuthCombo.getSelectionIndex());

				super.widgetSelected(e);
			}
		});
		repConfCombo = RuleConstants.createRODCombo(client);
		repConfCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rules.get(rulesTable.getSelectionIndex()).setRepConf(
						repConfCombo.getSelectionIndex());

				super.widgetSelected(e);
			}
		});
		repIntCombo = RuleConstants.createRODCombo(client);
		repIntCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rules.get(rulesTable.getSelectionIndex()).setRepInt(
						repIntCombo.getSelectionIndex());

				super.widgetSelected(e);
			}
		});

		return client;
	}

	private Button createCheckAoCreation(Composite parent) {
		aoCreationCheck = toolkit.createButton(parent, "AOCreation", SWT.CHECK);
		aoCreationCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rules.get(rulesTable.getSelectionIndex()).setAoCreation(
						aoCreationCheck.getSelection());

				super.widgetSelected(e);
			}
		});

		return aoCreationCheck;
	}

	private Button createCheckMigration(Composite parent) {
		migrationCheck = toolkit.createButton(parent, "Migration", SWT.CHECK);
		migrationCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rules.get(rulesTable.getSelectionIndex()).setMigration(
						migrationCheck.getSelection());

				super.widgetSelected(e);
			}
		});

		return migrationCheck;
	}

	private Section createSectionRules(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText("Rules");

		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout());

		createTableRules(client).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		createCompositeOrderButtons(client).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false));

		createCompositeButtons(client).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false));

		createCompositeFields(client).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false));

		toolkit.createLabel(client, "Authorized users :");
		createTableUsers(client).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		section.setClient(client);
		return section;
	}

	private Composite createCompositeOrderButtons(Composite parent) {
		Composite client = toolkit.createComposite(parent);
		client.setLayout(new GridLayout(2, true));

		createButtonUp(client).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false));

		createButtonDown(client).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false));

		return client;
	}
	
	private Button createButtonUp(Composite parent) {
		Button button = toolkit.createButton(parent, "^", SWT.BUTTON1);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				int index = rulesTable.getSelectionIndex();
				if (index == -1 || index == 0) {
					return;
				}
				rules.set(index, rules.set(index - 1, rules.get(index)));
				rulesTable.setSelection(index - 1);
				updateRulesTable();

				super.mouseUp(e);
			}
		});

		return button;
	}
	
	private Button createButtonDown(Composite parent) {
		Button button = toolkit.createButton(parent, "v", SWT.BUTTON1);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				int index = rulesTable.getSelectionIndex();
				if (index == -1 || index == rules.size() - 1) {
					return;
				}
				rules.set(index, rules.set(index + 1, rules.get(index)));
				rulesTable.setSelection(index + 1);
				updateRulesTable();

				super.mouseUp(e);
			}
		});

		return button;
	}
	
	private Composite createCompositeButtons(Composite parent) {
		Composite client = toolkit.createComposite(parent);
		client.setLayout(new GridLayout(3, true));

		createButtonNewRule(client).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false));

		createButtonSaveRules(client).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false));

		createButtonLoadPolicy(client).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false));

		return client;
	}

	private Button createButtonNewRule(Composite parent) {
		Button button = toolkit.createButton(parent, "New rule", SWT.BUTTON1);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				rules.add(new SimplePolicyRule());
				updateRulesTable();

				super.mouseUp(e);
			}
		});

		return button;
	}

	private Button createButtonSaveRules(Composite parent) {
		Button button = toolkit.createButton(parent, "Save rules", SWT.BUTTON1);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fd = new FileDialog(new Shell(), SWT.SAVE);
				fd.setText("Save active Policy file");
				fd.setFilterExtensions(new String[] { "*.policy", "*.*" });
				String fileName = fd.open();
				try {
					PolicyFile policy = new PolicyFile(applicationNameText
							.getText(), keystoreText.getText(), rules,
							authorizedUsers);
					PolicyTools.writePolicyFile(fileName, policy);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}

				super.mouseUp(e);
			}
		});

		return button;
	}

	private Button createButtonLoadPolicy(Composite parent) {
		Button button = toolkit.createButton(parent, "Load rules", SWT.BUTTON1);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fd = new FileDialog(new Shell(), SWT.OPEN);
				fd.setText("Load Policy file");
				fd.setFilterExtensions(new String[] { "*.policy", "*.*" });
				String name = fd.open();
				PolicyFile policy = PolicyTools.readPolicyFile(name, activeKeystore);

				rules.clear();
				rules.addAll(policy.getRules());
				applicationNameText.setText(policy.getApplicationName());
				keystoreText.setText(policy.getKeystorePath());
				authorizedUsers.clear();
				authorizedUsers.addAll(policy.getAuthorizedUsers());
				
				updateRulesTable();
				updateUsersTable();

				super.mouseUp(e);
			}
		});

		return button;
	}

	private Composite createCompositeFields(Composite parent) {
		Composite client = toolkit.createComposite(parent);
		client.setLayout(new GridLayout(3, false));

		toolkit.createLabel(client, "Application name :");
		applicationNameText = toolkit.createText(client, "");
		applicationNameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false, 2, 1));

		toolkit.createLabel(client, "Keystore :");
		keystoreText = toolkit.createText(client, "");
		keystoreText
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		Button button = toolkit.createButton(client, "...", SWT.BUTTON1);
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fd = new FileDialog(new Shell(), SWT.OPEN);
				fd.setText("Choose keystore");
				fd.setFilterExtensions(new String[] { "*.p12", "*.*" });
				String name = fd.open();
				if (name != null) {
					keystoreText.setText(name);
				}
				
				super.mouseUp(e);
			}
		});

		return client;
	}

	private Table createTableUsers(Composite parent) {
		usersTable = toolkit.createTable(parent, SWT.NULL);
		usersTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL || e.character == SWT.BS) {
					authorizedUsers.remove(usersTable.getSelectionIndex());
					updateUsersTable();
				}

				super.keyPressed(e);
			}
		});
		usersTableViewer = new TableViewer(usersTable);

		// drag n drop
		DropTarget target = new DropTarget(usersTable, DND.DROP_DEFAULT
				| DND.DROP_COPY);

		target.setTransfer(new Transfer[] { CertificateTreeMapTransfer
				.getInstance() });
		target.addDropListener(new DropTargetAdapter() {

			@Override
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					event.detail = DND.DROP_COPY;
				}
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					event.detail = DND.DROP_COPY;
				}
			}

			@Override
			public void drop(DropTargetEvent event) {
				if (CertificateTreeMapTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
					CertificateTreeMap map = ((CertificateTreeMap) event.data);

					for (CertificateTree tree : map.keySet()) {
						if (tree.getCertificate().getType() == SecurityConstants.ENTITY_TYPE_USER) {
							authorizedUsers.add(tree.getCertificate());
						}
					}

					updateUsersTable();
				}
			}
		});

		return usersTable;
	}

	private void updateUsersTable() {
		int selection = usersTable.getSelectionIndex();
		usersTable.removeAll();

		usersTableViewer.add(authorizedUsers.toArray());

		if (usersTable.getItemCount() == 0) {
			usersTable.deselectAll();
		} else {
			if (selection >= usersTable.getItemCount()) {
				selection--;
			}
			usersTable.setSelection(selection);
		}
	}

	private void updateRulesTable() {
		int selection = rulesTable.getSelectionIndex();
		rulesTable.removeAll();

		rulesTableViewer.add(rules.toArray());

		if (rulesTable.getItemCount() == 0) {
			rulesTable.deselectAll();
		} else {
			if (selection >= rulesTable.getItemCount()) {
				selection--;
			}
			rulesTable.setSelection(selection);
		}
		updateRuleEditor();
	}

	private Table createTableRules(Composite parent) {
		rulesTable = toolkit.createTable(parent, SWT.NULL);
		rulesTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRuleEditor();

				super.widgetSelected(e);
			}
		});
		rulesTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL || e.character == SWT.BS) {
					rules.remove(rulesTable.getSelectionIndex());
					updateRulesTable();
				}

				super.keyPressed(e);
			}
		});
		rulesTableViewer = new TableViewer(rulesTable);

		return rulesTable;
	}

	private void updateRuleEditor() {
		fromTable.updateTable(rulesTable);
		toTable.updateTable(rulesTable);

		if (rulesTable.getSelectionIndex() != -1) {
			SimplePolicyRule selectedRule = rules.get(rulesTable
					.getSelectionIndex());

			requestCheck.setEnabled(true);

			replyCheck.setEnabled(true);

			aoCreationCheck.setEnabled(true);
			migrationCheck.setEnabled(true);

			requestCheck.setSelection(selectedRule.isRequest());

			enableRequestEditor(selectedRule.isRequest());

			reqAuthCombo.select(selectedRule.getReqAuth());
			reqIntCombo.select(selectedRule.getReqInt());
			reqConfCombo.select(selectedRule.getReqConf());

			replyCheck.setSelection(selectedRule.isReply());

			enableReplyEditor(selectedRule.isReply());

			repAuthCombo.select(selectedRule.getRepAuth());
			repIntCombo.select(selectedRule.getRepInt());
			repConfCombo.select(selectedRule.getRepConf());

			aoCreationCheck.setSelection(selectedRule.isAoCreation());
			migrationCheck.setSelection(selectedRule.isMigration());
		} else {
			requestCheck.setEnabled(false);

			enableRequestEditor(false);

			replyCheck.setEnabled(false);

			enableReplyEditor(false);

			aoCreationCheck.setEnabled(false);
			migrationCheck.setEnabled(false);
		}
	}

	private void enableRequestEditor(boolean enable) {
		reqAuthCombo.setEnabled(enable);
		reqIntCombo.setEnabled(enable);
		reqConfCombo.setEnabled(enable);
	}

	private void enableReplyEditor(boolean enable) {
		repAuthCombo.setEnabled(enable);
		repIntCombo.setEnabled(enable);
		repConfCombo.setEnabled(enable);
	}

	@Override
	public void update() {
		activeKeystoreSection.updateSection();
	}

}
