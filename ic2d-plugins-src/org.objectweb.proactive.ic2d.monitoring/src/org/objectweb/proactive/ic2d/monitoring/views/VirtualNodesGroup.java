package org.objectweb.proactive.ic2d.monitoring.views;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.objectweb.proactive.ic2d.monitoring.data.AbstractDataObject;
import org.objectweb.proactive.ic2d.monitoring.data.NodeObject;
import org.objectweb.proactive.ic2d.monitoring.data.VNObject;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.monitoring.figures.VNColors;

public class VirtualNodesGroup implements Observer {

	private Group group;

	private Map<Button, VNObject> buttons = new HashMap<Button, VNObject>();

	private Map<VNObject, Button> virtualNodes = new HashMap<VNObject, Button>();

	private VNColors vnColors;

	//
	// -- CONSTRUCTOR -----------------------------------------------
	//

	public VirtualNodesGroup(Composite parent) {

		vnColors = new VNColors();

		group = new Group(parent, SWT.NONE);
		group.setText("Virtual nodes");

		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = true;
		rowLayout.justify = false;
		rowLayout.marginLeft = 5;
		rowLayout.marginTop = 0;
		rowLayout.marginRight = 5;
		rowLayout.marginBottom = 5;
		rowLayout.spacing = 10;
		group.setLayout(rowLayout);
	}


	//
	// -- PUBLIC METHOD -----------------------------------------------
	//

	public void update(Observable o, Object arg) {
		// <"Message",VNObject>
		if(arg instanceof Hashtable){//<"Add a virtual node",VNObject>
			Hashtable<String, VNObject> table = (Hashtable<String, VNObject>) arg;
			final VNObject vnAdded = table.get(WorldObject.ADD_VN_MESSAGE);
			if(vnAdded!=null){
				Display.getDefault().asyncExec(new Runnable() {
					public void run () {
						Button b = new Button(group, SWT.CHECK);
						b.setForeground(vnColors.getColor(vnAdded.getKey())); //DOESN'T WORK !!!
						b.setText(vnAdded.getFullName());
						b.addSelectionListener(new VirtualNodeButtonListener());
						buttons.put(b, vnAdded);
						virtualNodes.put(vnAdded, b);
						group.pack(true);
					}
				});
			}
			else{//<"Remove a virtual node",VNObject>
				final VNObject vnRemoved = table.get(WorldObject.REMOVE_VN_MESSAGE);
				if(vnRemoved!=null){
					Display.getDefault().asyncExec(new Runnable() {
						public void run () {
							Button b = virtualNodes.get(vnRemoved);
							virtualNodes.remove(vnRemoved);
							buttons.remove(b);
							if(!b.isDisposed())
								b.dispose();
							if(!group.isDisposed())
								group.pack();
						}
					});
				}
			}
		}
	}

	public Group getGroup() {
		return group;
	}


	public Color getColor(VNObject vn) {
		Button b = virtualNodes.get(vn);
		if(b == null)
			return null;
		if(! b.getSelection())
			return null;
		return vnColors.getColor(vn.getKey());
	}

	//
	// -- INNER CLASSES -----------------------------------------------
	//

	private class VirtualNodeButtonListener extends SelectionAdapter {

		public void widgetSelected(SelectionEvent e) {
			VNObject vn = buttons.get(e.widget);
			List<AbstractDataObject> nodes = vn.getMonitoredChildren();
			for(int i=0, size=nodes.size() ; i<size ; i++) {
				NodeObject node = (NodeObject)nodes.get(i);
				node.setHighlight(((Button)e.widget).getSelection());
			}
		}

	}
}