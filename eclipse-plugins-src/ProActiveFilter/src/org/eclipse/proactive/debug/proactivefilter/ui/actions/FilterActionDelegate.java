package org.eclipse.proactive.debug.proactivefilter.ui.actions;

import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.proactive.debug.proactivefilter.core.ProactiveFilter;
import org.eclipse.proactive.debug.proactivefilter.ui.dialog.FilterDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * 
 * @author lvanni
 * @see IWorkbenchWindowActionDelegate
 */
public class FilterActionDelegate implements IViewActionDelegate {
	private IWorkbenchWindow window;
	private IDebugView view;
	private TreeViewer viewer;
	private FilterDialog filterDialog;
	
	private boolean isEnable;
	private ProactiveFilter pf;

	/**
	 * The constructor.
	 */
	public FilterActionDelegate() {
		pf = new ProactiveFilter();
		isEnable = false;
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		if(isEnable){
			viewer.removeFilter(pf);
			isEnable = false;
		} else {
			this.filterDialog = new FilterDialog(window.getShell(), viewer);
			int mode = filterDialog.getMode();
			if(mode != 3){
				if (mode == 1) {
					pf.removeAllRegex();
				} else { // mode == 2
					System.out.println("ThreadRegex: " + filterDialog.getThreadRegex());
					pf.addRegex(filterDialog.getThreadRegex(), filterDialog.getStackRegex());
				}
				viewer.addFilter(pf);
				isEnable = true;
			}
		}
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	@Override
	public void init(IViewPart view) {
		this.window = view.getViewSite().getWorkbenchWindow();
		this.view = (IDebugView) view;
		this.viewer = (TreeViewer)this.view.getViewer();
	}
}
