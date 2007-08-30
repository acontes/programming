package org.objectweb.proactive.ic2d.security.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.objectweb.proactive.core.security.SecurityConstants;
import org.objectweb.proactive.ic2d.security.core.CertificateTree;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeList;
import org.objectweb.proactive.ic2d.security.widgets.CertificateTreeListSection;

public class CertificateGenerationTab extends UpdatableTab {
	public static final String ID = "org.objectweb.proactive.ic2d.security.tabs.CertificateChainTab";

	private FormToolkit toolkit;

	private CertificateTreeList activeKeystore;

	private CertificateTreeList certTreeList;

	private Combo typeCombo;

	private Text keySizeText;

	private Text nameText;

	private Text validityText;

	private CertificateTreeListSection certTreeListSection;

	private CertificateTreeListSection activeKeystoreSection;

	/**
	 * The constructor.
	 */
	public CertificateGenerationTab(CTabFolder folder,
			CertificateTreeList keystore, FormToolkit tk) {
		super(folder, SWT.NULL);
		setText("Certificate chain editor");

		certTreeList = new CertificateTreeList();
		activeKeystore = keystore;
		toolkit = tk;

		Composite body = toolkit.createComposite(folder);

		body.setLayout(new GridLayout(3, true));

		createSectionKeyPair(body).setLayoutData(
				new GridData(SWT.FILL, SWT.TOP, true, true));

		createSectionCertTreeList(body).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		createSectionActiveKeystore(body).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		setControl(body);
	}

	private Section createSectionKeyPair(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText("Stuff");

		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(1, false));

		toolkit.createLabel(client, "Name :");
		nameText = toolkit.createText(client, "CN=");
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		toolkit.createLabel(client, "Validity (days) :");
		validityText = toolkit.createText(client, "365");
		validityText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		toolkit.createLabel(client, "Key size :");
		keySizeText = toolkit.createText(client, "512");
		keySizeText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		toolkit.createLabel(client, "Type : ");
		typeCombo = new Combo(client, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeCombo.add(SecurityConstants.ENTITY_STRING_DOMAIN);
		typeCombo.add(SecurityConstants.ENTITY_STRING_USER);
		typeCombo.add(SecurityConstants.ENTITY_STRING_APPLICATION);
		typeCombo.select(0);

		createButtonNewChain(client);

		createButtonChildCert(client);

		section.setClient(client);

		return section;
	}

	private Button createButtonNewChain(Composite parent) {
		Button button = toolkit.createButton(parent,
				"Generate self signed certificate", SWT.BUTTON1);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				int keySize;
				try {
					keySize = Integer.valueOf(keySizeText.getText());
				} catch (NumberFormatException nfe) {
					System.out.println("keysizepabo!!11");
					return;
				}
				int validity;
				try {
					validity = Integer.valueOf(validityText.getText());
				} catch (NumberFormatException nfe) {
					System.out.println("validitymeuchan!!1oneoneone");
					return;
				}
				String name = nameText.getText();
				if (!name.matches("CN=.+")) {
					System.out.println("nomcassai!!1oneeleven");
					return;
				}
				System.out.println("Generate self signed certificate");
				int type = SecurityConstants.typeToInt(typeCombo
						.getItem(typeCombo.getSelectionIndex()));
				certTreeList.add(new CertificateTree(name, keySize, validity,
						type));
				certTreeListSection.updateSection();

				super.mouseUp(e);
			}
		});

		return button;
	}

	private Button createButtonChildCert(Composite parent) {
		Button button = toolkit.createButton(parent,
				"Generate child certificate", SWT.BUTTON1);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				int keySize;
				try {
					keySize = Integer.valueOf(keySizeText.getText());
				} catch (NumberFormatException nfe) {
					System.out.println("keysizepabo!!11");
					return;
				}
				int validity;
				try {
					validity = Integer.valueOf(validityText.getText());
				} catch (NumberFormatException nfe) {
					System.out.println("validitymeuchan!!1oneoneone");
					return;
				}
				String name = nameText.getText();
				if (!name.matches("CN=.+")) {
					System.out.println("nomcassai!!1oneeleven");
					return;
				}
				CertificateTree tree = certTreeListSection.getSelectionData();
				if (tree.getCertificate().getPrivateKey() == null) {
					System.out
							.println("Impossible to create a child of a certificate without a private key.");
					return;
				}
				System.out.println("Generating child certificate");
				int type = SecurityConstants.typeToInt(typeCombo
						.getItem(typeCombo.getSelectionIndex()));
				tree.add(name, keySize, validity, type);

				certTreeListSection.updateSection();
			}
		});

		return button;
	}

	private Section createSectionCertTreeList(Composite parent) {
		certTreeListSection = new CertificateTreeListSection(parent, toolkit,
				"Certificate Tree", certTreeList, true, true, true, false);
		return certTreeListSection.get();
	}

	private Section createSectionActiveKeystore(Composite parent) {
		activeKeystoreSection = new CertificateTreeListSection(parent, toolkit,
				"ActiveKeystore", activeKeystore, true, true, true, false);
		return activeKeystoreSection.get();
	}

	@Override
	public void update() {
		activeKeystoreSection.updateSection();
	}
}
