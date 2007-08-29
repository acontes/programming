package org.objectweb.proactive.ic2d.security.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.ic2d.security.core.CertificateTree;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeList;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeMap;
import org.objectweb.proactive.ic2d.security.core.CertificateTreeMapTransfer;

public class CertificateTreeListSection {
	
	private Section section;

	private Tree tree;

	private CertificateTreeList certTreeList;

	public CertificateTreeListSection(Composite parent, FormToolkit toolkit,
			String title, CertificateTreeList data, boolean allowDeletion,
			boolean allowDrag, boolean allowDrop, boolean withChecks) {
		section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(title);
		certTreeList = data;

		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout());

		int style = withChecks ? SWT.SINGLE | SWT.CHECK : SWT.SINGLE;
		tree = toolkit.createTree(client, style);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		if (allowDeletion) {
			tree.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.character == SWT.DEL || e.character == SWT.BS) {
						certTreeList.remove(getSelectionData());

						updateSection();
					}

					super.keyPressed(e);
				}
			});
		}

		if (allowDrag) {
			DragSource source = new DragSource(tree, DND.DROP_COPY);

			source.setTransfer(new Transfer[] { CertificateTreeMapTransfer
					.getInstance() });

			source.addDragListener(new DragSourceAdapter() {
				@Override
				public void dragSetData(DragSourceEvent event) {
					// Provide the data of the requested type.
					CertificateTreeMap map = new CertificateTreeMap();
					map.put(getSelectionData(), getSelectionData()
							.getCertChain());
					event.data = map;
				}
			});
		}

		if (allowDrop) {
			DropTarget target = new DropTarget(tree, DND.DROP_DEFAULT
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
					if (CertificateTreeMapTransfer.getInstance()
							.isSupportedType(event.currentDataType)) {
						CertificateTreeMap map = (CertificateTreeMap) event.data;

						for (Entry<CertificateTree, List<TypedCertificate>> entry : map
								.entrySet()) {
							CertificateTree newTree = CertificateTree
									.newTree(entry.getValue());
							newTree.merge(entry.getKey());
							certTreeList.add(newTree.getRoot());
						}

						updateSection();
					}
				}
			});
		}

		section.setClient(client);
	}

	public void updateSection() {
		Map<TypedCertificate, Boolean> state = new HashMap<TypedCertificate, Boolean>();
		for (TreeItem item : tree.getItems()) {
			state.putAll(getState(item));
		}

		tree.removeAll();
		for (CertificateTree subTree : certTreeList) {
			newTreeItem(tree, subTree);
		}

		for (TreeItem item : tree.getItems()) {
			setState(item, state);
		}
	}

	private Map<TypedCertificate, Boolean> getState(TreeItem item) {
		Map<TypedCertificate, Boolean> state = new HashMap<TypedCertificate, Boolean>();

		state.put(((CertificateTree) item.getData()).getCertificate(), item
				.getExpanded());
		for (TreeItem child : item.getItems()) {
			state.putAll(getState(child));
		}

		return state;
	}

	private void setState(TreeItem item, Map<TypedCertificate, Boolean> state) {
		TypedCertificate cert = ((CertificateTree) item.getData())
				.getCertificate();
		if (state.containsKey(cert)) {
			item.setExpanded(state.get(cert));
			for (TreeItem child : item.getItems()) {
				setState(child, state);
			}
		}
	}

	private TreeItem newTreeItem(Widget parent, CertificateTree newTree) {
		TreeItem item = null;
		if (parent instanceof Tree) {
			Tree treeParent = (Tree) parent;
			item = new TreeItem(treeParent, SWT.NONE);
		} else if (parent instanceof TreeItem) {
			TreeItem itemParent = (TreeItem) parent;
			item = new TreeItem(itemParent, SWT.NONE);
		}

		if (item == null) {
			return null;
		}

		item.setData(newTree);
		item.setText(newTree.getName());
		boolean hasPrivateKey = newTree.getCertificate().getPrivateKey() != null;
		item.setGrayed(!hasPrivateKey);
		if (hasPrivateKey) {
			item.setForeground(new Color(null, 255, 0, 0));
		}
		for (CertificateTree subTree : newTree.getChildren()) {
			newTreeItem(item, subTree);
		}
		return item;
	}

	public CertificateTree getSelectionData() {
		return (CertificateTree) tree.getSelection()[0].getData();
	}
	
	public Section get() {
		return section;
	}

	public Tree getTree() {
		return tree;
	}

}
