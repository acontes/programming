package org.objectweb.proactive.ic2d.componentmonitoring.editpart;


import java.util.List;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.objectweb.proactive.ic2d.componentmonitoring.data.HelloModel;
import org.objectweb.proactive.ic2d.componentmonitoring.view.ComponentTreeView;

public class HelloWorldEditPart extends AbstractTreeEditPart{
	
	public static final Color HEADER_COLOR = new Color(Display.getCurrent(), 225, 225, 255);
//	public static final Image VN_IMAGE = new Image(Display.getCurrent(), HelloWorldEditPart.class
//            .getResourceAsStream("vn_icon.png"));
	
//    protected ComponentTreeView componentTreeView;
    private TreeItem widgetTreeItem;

    public HelloWorldEditPart(HelloModel model) {
    	super(model);
//        this.componentTreeView = componentTreeView;
    }

    @Override
//    protected final void createEditPolicies() {
//        super.createEditPolicies();
//        if (super.getWidget() instanceof TreeItem) {
//            this.widgetTreeItem = (TreeItem) super.getWidget();
//            this.widgetTreeItem.setText(new String[ComponentTreeView.NUMBER_OF_COLUMNS]); // 5 columns
////            HelloModel model = (HelloModel) getModel();
//
//            // If is header choose different color
////            if ((model.getParent() == null) && (model.getCurrentTimer() == null)) {
////                this.widgetTreeItem.setBackground(HEADER_COLOR);
////            }
//        }
//        if(super.getWidget() instanceof Tree)
//        {
//        	this.widgetTreeItem = new TreeItem((Tree)super.getWidget(),0);
//            this.widgetTreeItem.setText(new String[ComponentTreeView.NUMBER_OF_COLUMNS]); 
//        }
//    }

    protected final void refreshVisuals() {
    	HelloModel model = (HelloModel) getModel();
    	 if (getWidget() instanceof Tree) {
             return;
         }
//    	if (getWidget() instanceof Tree) {
//            return;
//        }
//        setWidgetImage(getImage());
    	 if(this.getWidget() instanceof TreeItem)
    	 {
    		 ((TreeItem)this.getWidget()).setText(ComponentTreeView.NAME_COLUMN, model.getText());
    		 ((TreeItem)this.getWidget()).setText(ComponentTreeView.HIERARCHICAL_COLUMN, "1");
    	 }
//        setWidgetText(getText());
//        getParent()
    	
    	
//        if ((this.widgetTreeItem != null)) {
//            
//                // 5 columns available
//                this.widgetTreeItem.setText(ComponentTreeView.NAME_COLUMN, model.getText()); // Name
////                showChildren(model,this.widgetTreeItem);
//          
//        }
    }
    
    private void showChildren(HelloModel parent, TreeItem Parentitem)
    {
    	List children = parent.getChildren();
    	for(Object a: children)
    	{
    		TreeItem item = new TreeItem(Parentitem,0);
    		item.setText(ComponentTreeView.NAME_COLUMN, ((HelloModel)a).getText());
    		showChildren((HelloModel)a,item);
    	}
    }
    
//    public void setWidget(Widget w)
//    {
//    	super.setWidget(w);
//    }

    
    @Override
    protected final List<HelloModel> getModelChildren() {
        return ((HelloModel) getModel()).getChildren();
    }
    
    @Override
    protected final String getText() {
        return ((HelloModel)getModel()).getText();
    }

    
//    @Override
//    protected final Image getImage() {
//        return null;
//    }
    /**
     * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getText()
     */
    
    @SuppressWarnings("unchecked")
    protected final HelloModel getCastedModel() {
        return (HelloModel) super.getModel();
    }
   
}
