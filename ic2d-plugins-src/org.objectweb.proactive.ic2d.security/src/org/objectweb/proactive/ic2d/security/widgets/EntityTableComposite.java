package org.objectweb.proactive.ic2d.security.widgets;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.objectweb.proactive.ic2d.security.core.CertificateTree;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeMap;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeMapTransfer;
import org.objectweb.proactive.ic2d.security.core.SimplePolicyRule;

public class EntityTableComposite extends Composite {

	private Table entities;

	private TableViewer viewer;

	private List<SimplePolicyRule> rules;

	private Table rulesTable;

	private boolean isFrom;

	public EntityTableComposite(Composite parent, FormToolkit toolkit,
			List<SimplePolicyRule> data, final boolean isFrom) {
		super(parent, SWT.NULL);
		toolkit.adapt(this);
		rules = data;
		this.isFrom = isFrom;
		setLayout(new GridLayout());

		entities = toolkit.createTable(this, SWT.NULL);
		entities.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer = new TableViewer(entities);

		entities.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL || e.character == SWT.BS) {
					if (isFrom) {
						rules.get(rulesTable.getSelectionIndex()).removeFrom(
								entities.getSelectionIndex());
					} else {
						rules.get(rulesTable.getSelectionIndex()).removeTo(
								entities.getSelectionIndex());
					}
					updateTable();
				}

				super.keyPressed(e);
			}
		});

		// drag n drop
		DropTarget target = new DropTarget(entities, DND.DROP_DEFAULT
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
						if (isFrom) {
							rules.get(rulesTable.getSelectionIndex()).addFrom(
									tree.getCertificate());
						} else {
							rules.get(rulesTable.getSelectionIndex()).addTo(
									tree.getCertificate());
						}
					}

					updateTable();
				}
			}
		});
	}
	
	public void updateTable() {
		updateTable(null);
	}

	public void updateTable(Table newRulesTable) {
		if (newRulesTable != null) {
			rulesTable = newRulesTable;
		}
		entities.removeAll();
		if (rulesTable.getSelectionIndex() != -1) {
			if (isFrom) {
				viewer.add(rules.get(rulesTable.getSelectionIndex()).getFrom()
						.toArray());
			} else {
				viewer.add(rules.get(rulesTable.getSelectionIndex()).getTo()
						.toArray());
			}
		}
	}

}
