package org.objectweb.proactive.ic2d.security.tabs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.objectweb.proactive.core.security.SecurityConstants;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.ic2d.security.core.CertificateTree;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeList;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeMap;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeMapTransfer;
import org.objectweb.proactive.ic2d.security.core.KeystoreFile;
import org.objectweb.proactive.ic2d.security.core.KeystoreUtils;
import org.objectweb.proactive.ic2d.security.widgets.CertificateTreeListSection;

public class KeystoreTab extends UpdatableTab {
	public static final String ID = "org.objectweb.proactive.ic2d.security.tabs.KeystoreTab";

	private List<KeystoreFile> keystoreFileList;

	private CertificateTreeList activeKeystore;

	private FormToolkit toolkit;

	private Tree keystoreTree;

	private CertificateTreeListSection activeKeystoreSection;

	private Text typeText;

	private Text subjectText;

	private Text issuerText;

	private Text publicText;

	private Text privateText;

	private Text passwordText;

	public KeystoreTab(CTabFolder folder, CertificateTreeList keystore,
			FormToolkit tk) {
		super(folder, SWT.NULL);
		setText("Keystore Editor");

		keystoreFileList = new ArrayList<KeystoreFile>();
		toolkit = tk;
		activeKeystore = keystore;

		Composite body = toolkit.createComposite(folder);

		body.setLayout(new GridLayout(3, true));

		createSectionLoadSave(body).setLayoutData(
				new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));

		createSectionKeystoreList(body).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		createSectionActiveKeystore(body).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		createSectionCertDetails(body).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		setControl(body);
	}

	private Composite createSectionLoadSave(Composite parent) {
		Composite client = toolkit.createComposite(parent);
		client.setLayout(new GridLayout(4, false));

		createButtonLoad(client).setLayoutData(
				new GridData(SWT.FILL, SWT.TOP, true, false));

		createButtonSave(client).setLayoutData(
				new GridData(SWT.FILL, SWT.TOP, true, false));

		toolkit.createLabel(client, "Keystore Password :");

		passwordText = toolkit.createText(client, "");
		passwordText
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		return client;
	}

	private Button createButtonLoad(Composite parent) {
		Button b = toolkit.createButton(parent, "Load a keystore", SWT.BUTTON1);
		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fd = new FileDialog(new Shell(), SWT.OPEN);
				fd.setText("Open keystore");
				fd
						.setFilterExtensions(new String[] { "*.p12", "*.crt",
								"*.*" });
				String name = fd.open();
				try {
					keystoreFileList.add(new KeystoreFile(name, KeystoreUtils
							.loadKeystore(name, passwordText.getText())));
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				} catch (CertificateException e1) {
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					ErrorDialog.openError(
							Display.getCurrent().getActiveShell(),
							"File Error", "Unable to open file", new Status(
									Status.ERROR, ID, Status.OK, "See details",
									e1));
					return;
				} catch (IOException e1) {
					ErrorDialog.openError(
							Display.getCurrent().getActiveShell(),
							"Keystore Error", "Unable to open keystore",
							new Status(Status.ERROR, ID, Status.OK,
									"See details", e1));
					return;
				} catch (KeyStoreException e1) {
					e1.printStackTrace();
				} catch (NoSuchProviderException e1) {
					e1.printStackTrace();
				} catch (UnrecoverableKeyException eke) {
					eke.printStackTrace();
				}
				updateKeystoreTree();

				super.mouseUp(e);
			}
		});

		return b;
	}

	private Button createButtonSave(Composite parent) {
		Button b = toolkit.createButton(parent, "Save active keystore",
				SWT.BUTTON1);
		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Map<CertificateTree, Boolean> keepPrivateKeyMap = new HashMap<CertificateTree, Boolean>();
				for (TreeItem item : activeKeystoreSection.getTree().getItems()) {
					addSelected(item, keepPrivateKeyMap);
				}

				FileDialog fd = new FileDialog(new Shell(), SWT.SAVE);
				fd.setText("Save active Keystore");
				fd
						.setFilterExtensions(new String[] { "*.p12", "*.crt",
								"*.*" });
				String name = fd.open();
				try {
					KeystoreUtils.saveKeystore(name, passwordText.getText(),
							activeKeystore, keepPrivateKeyMap);
				} catch (FileNotFoundException fnfe) {
					fnfe.printStackTrace();
				} catch (KeyStoreException kse) {
					kse.printStackTrace();
				} catch (NoSuchProviderException nspe) {
					nspe.printStackTrace();
				} catch (NoSuchAlgorithmException nsae) {
					nsae.printStackTrace();
				} catch (CertificateException ce) {
					ce.printStackTrace();
				} catch (IOException oie) {
					oie.printStackTrace();
				} catch (UnrecoverableKeyException uke) {
					uke.printStackTrace();
				}

				super.mouseUp(e);
			}

			private void addSelected(TreeItem item,
					Map<CertificateTree, Boolean> map) {
				CertificateTree tree = (CertificateTree) item.getData();
				if (item.getChecked()
						&& tree.getCertificate().getPrivateKey() != null) {
					map.put(tree, true);
				}
				for (TreeItem child : item.getItems()) {
					addSelected(child, map);
				}
			}
		});

		return b;
	}

	private Section createSectionKeystoreList(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText("Loaded Keystores List");

		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout());

		createTreeKeystore(client).setLayoutData(
				new GridData(GridData.FILL_BOTH));

		section.setClient(client);

		return section;
	}

	private Tree createTreeKeystore(Composite parent) {
		keystoreTree = toolkit.createTree(parent, SWT.SINGLE);
		keystoreTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object data = keystoreTree.getSelection()[0].getData();

				if (data instanceof CertificateTree) {
					CertificateTree ct = (CertificateTree) data;

					updateDetailsSection(ct.getCertificate());
				} else {
					updateDetailsSection(null);
				}

				super.widgetSelected(e);
			}
		});

		keystoreTree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL || e.character == SWT.BS) {
					TreeItem ks = keystoreTree.getSelection()[0];
					CertificateTree selectedTree = (CertificateTree) ks
							.getData();

					while (ks.getParentItem() != null) {
						ks = ks.getParentItem();
					}

					CertificateTreeList list = (CertificateTreeList) ks
							.getData();
					list.remove(selectedTree);

					updateKeystoreTree();
				}

				super.keyPressed(e);
			}
		});

		// drag n drop
		DragSource source = new DragSource(keystoreTree, DND.DROP_COPY);

		source.setTransfer(new Transfer[] { CertificateTreeMapTransfer
				.getInstance() });

		source.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event) {
				// Provide the data of the requested type.
				CertificateTreeMap map = new CertificateTreeMap();
				Object data = keystoreTree.getSelection()[0].getData();

				if (data instanceof KeystoreFile) {
					KeystoreFile ksf = (KeystoreFile) data;

					for (CertificateTree tree : ksf) {
						map.put(tree, tree.getCertChain());
					}
				} else if (data instanceof CertificateTree) {
					CertificateTree ct = (CertificateTree) data;

					map.put(ct, ct.getCertChain());
				}

				event.data = map;
			}
		});

		return keystoreTree;
	}

	private void updateKeystoreTree() {
		keystoreTree.removeAll();

		for (KeystoreFile keystore : keystoreFileList) {
			TreeItem ksItem = new TreeItem(keystoreTree, SWT.NONE);
			ksItem.setData(keystore);
			ksItem.setText(keystore.getName());
			for (CertificateTree tree : keystore) {
				newTreeItem(ksItem, tree);
			}
		}
	}

	private TreeItem newTreeItem(TreeItem parent, CertificateTree tree) {
		TreeItem item = new TreeItem(parent, SWT.NONE);

		if (item == null) {
			return null;
		}

		item.setData(tree);
		item.setText(tree.getName());
		if (tree.getCertificate().getPrivateKey() != null) {
			item.setForeground(new Color(null, 255, 0, 0));
		}
		for (CertificateTree subTree : tree.getChildren()) {
			newTreeItem(item, subTree);
		}
		return item;
	}

	private Section createSectionActiveKeystore(Composite parent) {
		activeKeystoreSection = new CertificateTreeListSection(parent, toolkit,
				"ActiveKeystore", activeKeystore, true, false, true, true);

		activeKeystoreSection.getTree().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						updateDetailsSection(activeKeystoreSection
								.getSelectionData().getCertificate());

						super.widgetSelected(e);
					}
				});

		return activeKeystoreSection.get();
	}

	// private Tree createTreeActiveKeystore(Composite parent) {
	// activeKeystoreTree = toolkit.createTree(parent, SWT.SINGLE|SWT.CHECK);
	// activeKeystoreTree.addSelectionListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// Object data = activeKeystoreTree.getSelection()[0].getData();
	//				
	// if (data instanceof CertificateChain) {
	// CertificateChain cc = (CertificateChain) data;
	//
	// updateDetailsSection(cc.get(0));
	// } else if (data instanceof TypedCertificate) {
	// TypedCertificate tc = (TypedCertificate) data;
	//
	// updateDetailsSection(tc);
	// } else {
	// updateDetailsSection(null);
	// }
	//				
	// super.widgetSelected(e);
	// }
	// });
	// activeKeystoreTree.addKeyListener(new KeyAdapter() {
	// @Override
	// public void keyPressed(KeyEvent e) {
	// if (e.character == SWT.DEL || e.character == SWT.BS) {
	// TreeItem ks = activeKeystoreTree.getSelection()[0];
	// while (ks.getParentItem() != null) {
	// ks = ks.getParentItem();
	// }
	// for (int i = 0; i < activeKeystore.size(); i++) {
	// if (activeKeystore.get(i).equals(ks.getData())) {
	// activeKeystore.remove(i);
	// break;
	// }
	// }
	//
	// updateActiveKeystoreTree();
	// updateDetailsSection(null);
	// }
	//
	// super.keyPressed(e);
	// }
	// });
	// activeKeystoreTree.addMouseListener(new MouseAdapter() {
	// @Override
	// public void mouseDown(MouseEvent e) {
	// // activeKeystoreTree.getSelection()[0].setChsecked(false);
	//				
	// super.mouseDown(e);
	// }
	//			
	// @Override
	// public void mouseUp(MouseEvent e) {
	// // activeKeystoreTree.getSelection()[0].setChecked(false);
	//				
	// super.mouseUp(e);
	// }
	// });
	//
	// // drag n drop
	// DropTarget target = new DropTarget(activeKeystoreTree, DND.DROP_DEFAULT
	// | DND.DROP_COPY);
	//
	// target.setTransfer(new Transfer[] { CertificateChainListTransfer
	// .getInstance() });
	// target.addDropListener(new DropTargetAdapter() {
	//
	// public void dragEnter(DropTargetEvent event) {
	// if (event.detail == DND.DROP_DEFAULT) {
	// event.detail = DND.DROP_COPY;
	// }
	// }
	//
	// public void dragOperationChanged(DropTargetEvent event) {
	// if (event.detail == DND.DROP_DEFAULT) {
	// event.detail = DND.DROP_COPY;
	// }
	// }
	//
	// @Override
	// public void drop(DropTargetEvent event) {
	// if (CertificateChainListTransfer.getInstance().isSupportedType(
	// event.currentDataType)) {
	// List<CertificateChain> data = (List<CertificateChain>) event.data;
	//
	// activeKeystore.addAll(data);
	// updateActiveKeystoreTree();
	// }
	// }
	// });
	//
	// updateActiveKeystoreTree();
	//
	// return activeKeystoreTree;
	// }

	// private void updateActiveKeystoreTree() {
	// activeKeystoreTree.removeAll();
	//
	// for (CertificateChain chain : activeKeystore) {
	// TreeItem chainItem = new TreeItem(activeKeystoreTree, SWT.NONE);
	// chainItem.setData(chain);
	// chainItem.setText(chain.toString());
	// for (TypedCertificate cert : chain) {
	// TreeItem certItem = new TreeItem(chainItem, SWT.NONE);
	// certItem.setData(cert);
	// certItem.setText(cert.toString());
	// }
	// }
	// }

	private Section createSectionCertDetails(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText("Certificate details");

		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(1, false));

		toolkit.createLabel(client, "Type :");
		typeText = toolkit.createText(client, "");
		typeText.setEditable(false);
		typeText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		toolkit.createLabel(client, "Subject DN :");
		subjectText = toolkit.createText(client, "");
		subjectText.setEditable(false);
		subjectText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		toolkit.createLabel(client, "Issuer DN :");
		issuerText = toolkit.createText(client, "");
		issuerText.setEditable(false);
		issuerText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		toolkit.createLabel(client, "Public Key :");
		publicText = toolkit.createText(client, "", SWT.MULTI | SWT.WRAP);
		publicText.setEditable(false);
		publicText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		toolkit.createLabel(client, "Private Key :");
		privateText = toolkit.createText(client, "", SWT.MULTI | SWT.WRAP);
		privateText.setEditable(false);
		privateText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		section.setClient(client);

		return section;
	}

	private void updateDetailsSection(TypedCertificate cert) {
		if (cert != null) {
			typeText.setText(SecurityConstants.typeToString(cert.getType()));
			subjectText.setText(cert.getCert().getSubjectX500Principal()
					.getName());
			issuerText.setText(cert.getCert().getIssuerX500Principal()
					.getName());
			publicText.setText(cert.getCert().getPublicKey().toString());
			try {
				privateText.setText(cert.getPrivateKey().toString());
			} catch (NullPointerException npe) {
				privateText.setText("");
			}
		} else {
			typeText.setText("");
			subjectText.setText("");
			issuerText.setText("");
			publicText.setText("");
			privateText.setText("");
		}
	}

	@Override
	public void update() {
		activeKeystoreSection.updateSection();
	}
}
