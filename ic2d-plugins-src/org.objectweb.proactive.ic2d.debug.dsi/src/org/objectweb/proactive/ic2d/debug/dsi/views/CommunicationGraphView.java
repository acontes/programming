package org.objectweb.proactive.ic2d.debug.dsi.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.ContinuousLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.debug.dsi.Activator;
import org.objectweb.proactive.ic2d.debug.dsi.handler.DSIHandler;
import org.objectweb.proactive.ic2d.debug.dsi.handler.RequestDSI;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.NamesFactory;

public class CommunicationGraphView extends ViewPart implements IZoomableWorkbenchPart {

    /* The view ID */
    public static final String ID = "org.objectweb.proactive.ic2d.debug.dsi.views.CommunicationGraphView";

    private Map<String, GraphNode> nodes = new HashMap<String, GraphNode>();
    private Map<String, List<GraphNode>> dsiNodes = new HashMap<String, List<GraphNode>>();
    private Graph g = null;

    private FormToolkit toolKit = null;
//    private ScrolledForm form = null;
//    private ManagedForm managedForm = null;
    private GraphViewer viewer;
    private VisualizationForm visualizationForm;

    private Color colors[] = { 
            new Color(Display.getDefault(), 1, 70, 122), new Color(Display.getDefault(), 139, 150, 171),
            new Color(Display.getDefault(), 0, 0, 0), new Color(Display.getDefault(), 255, 0, 0),
            new Color(Display.getDefault(), 127, 0, 0), new Color(Display.getDefault(), 255, 196, 0),
            new Color(Display.getDefault(), 255, 255, 0), new Color(Display.getDefault(), 0, 255, 0),
            new Color(Display.getDefault(), 0, 127, 0), new Color(Display.getDefault(), 96, 255, 96),
            new Color(Display.getDefault(), 0, 255, 255), new Color(Display.getDefault(), 0, 0, 255) };

    @Override
    public void createPartControl(Composite parent) {
        //TODO : Trouver comment afficher le graphe par rapport a la vue du PDE
        //        g = new Graph(Display.getCurrent().getActiveShell(), SWT.NONE);


        toolKit = new FormToolkit(parent.getDisplay());
        visualizationForm = new VisualizationForm(parent, toolKit, this);
        viewer = visualizationForm.getGraphViewer();
        //form = visualizationForm.getForm();
        //managedForm = visualizationForm.getManagedForm();

        g = viewer.getGraphControl();

        //        final Graph g = viewer.getGraphControl();
        //
        //        GraphNode a = new GraphNode(g, ZestStyles.CONNECTIONS_DIRECTED, "Root");
        //        GraphConnection connection = new GraphConnection(g, SWT.NONE, a, a);
        //        connection.setText("A to A");
        //        a.setLocation(100, 100);

        //      g.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);

    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    private void createNode(UniqueID id, UniqueID dsi) {
        String name = NamesFactory.getInstance().associateName(id, "AO");
        GraphNode n = new GraphNode(g, SWT.NONE, name);
        List<GraphNode> gn = dsiNodes.get(dsi.shortString());
        if(gn == null){
            gn = new ArrayList<GraphNode>();
        }       
        gn.add(n);
        dsiNodes.put(dsi.shortString(),gn);
        nodes.put(id.shortString() + dsi.shortString(), n);
    }

    public Set<UniqueID> refresh(){
        Random rand = new Random();
        Map<UniqueID, Set<RequestDSI>> dsi = DSIHandler.getInstance().getDSI();
        
        for (Map.Entry<UniqueID, Set<RequestDSI>> e : dsi.entrySet()) {

            for (RequestDSI r : e.getValue()) {
                if (nodes.get(r.getSender().shortString() + e.getKey().shortString()) == null) {
                    createNode(r.getSender(), e.getKey());
                }
                if (nodes.get(r.getDestinator().shortString() + e.getKey().shortString()) == null) {
                    createNode(r.getDestinator(), e.getKey());
                }
            }

            Color color = colors[rand.nextInt(colors.length)];
            for (RequestDSI r : e.getValue()) {
                GraphConnection connection = new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, nodes
                        .get(r.getSender().shortString() + e.getKey().shortString()), nodes.get(r
                                .getDestinator().shortString() +
                                e.getKey().shortString()));
                connection.setLineColor(color);
                connection.setLineWidth(2);
                connection.setData(Boolean.FALSE);
                connection.setText(r.getMethodName());
            }
        }
        //g.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);        
        g.setLayoutAlgorithm(new GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
        Console.getInstance(Activator.CONSOLE_NAME).log("Rendering graph...done");
        return dsi.keySet();
    }

    public void highlightDSI(String dsiID, boolean active){
        List<GraphNode> ns = dsiNodes.get(dsiID); 
        for(GraphNode n : ns){
            if(active)
                n.highlight();
            else
                n.unhighlight();
        }
    }
    

    public AbstractZoomableViewer getZoomableViewer() {
        // TODO Auto-generated method stub
        return null;
    }


}
