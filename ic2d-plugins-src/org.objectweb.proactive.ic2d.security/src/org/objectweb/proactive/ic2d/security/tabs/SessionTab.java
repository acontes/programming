package org.objectweb.proactive.ic2d.security.tabs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.objectweb.proactive.core.security.SecurityContext;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.core.security.SecurityConstants.EntityType;
import org.objectweb.proactive.core.security.crypto.Session;
import org.objectweb.proactive.ic2d.security.widgets.CertificateDetailsSection;
import org.objectweb.proactive.ic2d.security.widgets.CommunicationDetailsComposite;

public class SessionTab extends UpdatableTab {

	private List<Session> sessionList;

	private FormToolkit toolkit;

	private Table sessionTable;

	private TableViewer sessionTableViewer;

	private CertificateDetailsSection certDetailsSection;
	
	private CommunicationDetailsComposite requestComposite;
	private CommunicationDetailsComposite replyComposite;

	public SessionTab(CTabFolder folder, FormToolkit tk) {
		super(folder, SWT.NULL);
		setText("Sessions browser");

		this.sessionList = new ArrayList<Session>();
		this.toolkit = tk;

		Composite body = this.toolkit.createComposite(folder);

		body.setLayout(new GridLayout(3, true));

		createSectionSessionList(body).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		createSectionDistantCertificate(body).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		createSectionContext(body).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		setControl(body);
	}

	private Section createSectionSessionList(Composite parent) {
		Section section = this.toolkit.createSection(parent,
				ExpandableComposite.TITLE_BAR);
		section.setText("Sessions List");

		Composite client = this.toolkit.createComposite(section);
		client.setLayout(new GridLayout());

		createListSessions(client);

		section.setClient(client);

		return section;
	}

	private Table createListSessions(Composite parent) {
		this.sessionTable = this.toolkit.createTable(parent, SWT.NULL);
		this.sessionTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		this.sessionTableViewer = new TableViewer(this.sessionTable);

		this.sessionTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateViewers();
				
				super.widgetSelected(e);
			}
		});

		return this.sessionTable;
	}

	private Section createSectionDistantCertificate(Composite parent) {
		this.certDetailsSection = new CertificateDetailsSection(parent,
				this.toolkit);

		return this.certDetailsSection.get();
	}

	private Section createSectionContext(Composite parent) {
		Section section = this.toolkit.createSection(parent,
				ExpandableComposite.TITLE_BAR);
		section.setText("Communication");

		Composite client = this.toolkit.createComposite(section);
		client.setLayout(new GridLayout());

		this.requestComposite = new CommunicationDetailsComposite(client, this.toolkit, "Request");
		this.requestComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		
		this.replyComposite = new CommunicationDetailsComposite(client, this.toolkit, "Reply");
		this.replyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));

		section.setClient(client);

		return section;
	}

	private void updateSessionTable() {
		this.sessionTable.removeAll();
		for (Session session : this.sessionList) {
			this.sessionTableViewer.add(new Long(session.getSessionID())
					.toString());
		}
		updateViewers();
	}

	protected void updateViewers() {
		this.certDetailsSection.update(new TypedCertificate(this.sessionList
				.get(this.sessionTable.getSelectionIndex())
				.getDistantOACertificate(),
				EntityType.UNKNOWN, null));

		SecurityContext sc = this.sessionList.get(
				this.sessionTable.getSelectionIndex()).getSecurityContext();
		
		this.requestComposite.updateCommunication(sc.getSendRequest());
		
		this.replyComposite.updateCommunication(sc.getSendReply());
	}
	
	public void setSessions(List<Session> sessions) {
		this.sessionList.clear();
		this.sessionList.addAll(sessions);
	}

	@Override
	public void update() {
		updateSessionTable();
	}

}
