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
	
	private boolean isEnable = false;
	private ProactiveFilter pf;

	@Override
	public void init(IViewPart view) {
		pf = new ProactiveFilter();
		this.window = view.getViewSite().getWorkbenchWindow();
		this.view = (IDebugView) view;
		this.viewer = (TreeViewer)this.view.getViewer();
	}
	
	/**
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		if(isEnable){
			action.setChecked(false);
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
				action.setChecked(true);
				isEnable = true;
			} else {
				action.setChecked(false);
			}
		}
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
