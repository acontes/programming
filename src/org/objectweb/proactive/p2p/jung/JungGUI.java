package org.objectweb.proactive.p2p.jung;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.p2p.test.Dumper;
import org.objectweb.proactive.p2p.test.Link;
import org.objectweb.proactive.p2p.test.P2PNode;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.ConstantEdgeStringer;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseVertex;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.FadingVertexLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.LayoutMutable;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;


public class JungGUI extends JFrame {
	
	//the time in ms we wait after a vertex has been added
	private final int UPDATE_PAUSE = 100;
	
    protected Graph graph;
    protected VisualizationViewer vv;
    protected PluggableRenderer pr;
    protected StringLabeller sl;
    protected ConstantEdgeStringer edgesLabeller;
    protected Integer key = new Integer(1);
    protected Layout layout;
    protected boolean mutable;
    
    public JungGUI(String fileName) {
        graph = new UndirectedSparseGraph(); //this.createGraph();
                                  
//layout = useNonMutableLayout(graph);
      layout = useMutableLayout(graph);
          
        //Layout layout = new ISOMLayout(graph);
        sl = StringLabeller.getLabeller(graph);
       edgesLabeller = new ConstantEdgeStringer(null);
        pr = new PluggableRenderer();
        pr.setVertexStringer(sl);
        //  this.createGraph();
        vv = new VisualizationViewer(layout, pr, new Dimension(1024, 768));
        vv.setPickSupport(new ShapePickSupport());
        pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
        vv.setBackground(Color.white);

        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(Mode.PICKING);
        vv.setGraphMouse(gm);
        this.add(new GraphZoomScrollPane(vv));
        this.pack();
        this.setVisible(true);
        this.createGraphFromFile2(fileName);
  
    }


    public Layout useNonMutableLayout(Graph g) {
    	this.mutable = false;
    //	return this.useCircleLayout(g);
    	return this.useKKLayout(g);
    	//return this.useFadingVertexLayout(g);
//         return this.useTreeLayout(g);    	
    }
    
    public Layout useMutableLayout(Graph g) {
    	this.mutable = true;
    	return this.useSpringLayout(g);
    //	return  this.useFRLayout(g);
    }
    

    public LayoutMutable useFRLayout(Graph g) {
    	return new FRLayout(g);
    }
    

    public Layout useFadingVertexLayout(Graph g) {
    	return  new FadingVertexLayout( 10, new SpringLayout( g ));
    }
    /**
     * Mutable layout
     * @param g
     * @return
     */
    protected LayoutMutable useSpringLayout(Graph g) {
    SpringLayout l =  new SpringLayout(graph);
    l.setRepulsionRange(500);
    l.setStretch(0.5);
    return l;
    }
    
    
    /** 
     * Non mutable layout
     * @param g
     * @return
     */
    protected Layout useKKLayout(Graph g) {
       KKLayout kk =  new KKLayout(g);
       kk.setLengthFactor(1.3);
       return kk;
    }
    
    protected Layout useCircleLayout(Graph g) {
    	return new CircleLayout(g);
    }
    
    
    public void createGraph() {
        // Graph tmp = new UndirectedSparseGraph();
        Vertex[] v = new Vertex[3];
        for (int i = 0; i < v.length; i++) {
            v[i] = graph.addVertex(new UndirectedSparseVertex());
            try {
                sl.setLabel(v[i], "Noeud " + i);
            } catch (UniqueLabelException e) {
                e.printStackTrace();
            }
        }
        graph.addEdge(new UndirectedSparseEdge(v[0], v[1]));
        graph.addEdge(new UndirectedSparseEdge(v[1], v[2]));
    }
    
    public void createGraphFromFile2(String name) {
    	Dumper dump = new Dumper();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(name)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String s = null;
       String current =null;
        //what we are reading now
        //  0 = nothing interesting
        //  1 = awaiting peer name
        //  2 = reading acquaintances name
        int readingStatus = 0;

        try {
            while ((s = in.readLine()) != null) {
                // System.out.println(s);
                if (s.indexOf(">>>") >= 0) {
                    //begining of a new Peer
                    readingStatus = 1;
                } else if (s.indexOf("<<<") >= 0) {
                    //end of a new Peer
                    readingStatus = 0;
                } else {
                    //reading some peer name 
                    switch (readingStatus) {
                    case 1: {
                        s= s.substring(0, s.indexOf("current")-1);
                        dump.addAsSender(s);
                        current = s;
                        readingStatus = 2;
                        break;
                    }
                    case 2: {
                        //we are either reading a machine name or some garbage
                        if (s.indexOf("---") < 0) {
                        	s= this.cleanURL(s);
                            System.out.println(s);
                            dump.addAsSender(s);
                            dump.addLink(current, s);
                            try {
                            } catch (Exception e) {
                            	e.printStackTrace();
                            }
                        } else {
                        	readingStatus = 3;
                        }
                        break;
                    }
                    case 3: {
                        if (s.indexOf("---") >= 0) {
                            readingStatus = 2;	
                         }
                    	break;
                    }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        dump.dumpAsText();
        this.generateGraphNodes(dump);
        this.generateGraphLinks(dump);
    }
    
    
 
    public String cleanURL(String s) {
    	if (s.indexOf("Acquaintance:") > 0) {
    		s = s.substring("Acquaintance:".length() + 2);
    	}
        try {
            return UrlBuilder.getHostNameAndPortFromUrl(s);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return s;
    }

    protected void generateGraphNodes(Dumper dump) {
    	 Set<Map.Entry<String, P2PNode>> map = (Set<Map.Entry<String, P2PNode>>) dump.getSenders().entrySet();
         Iterator it = map.iterator();
         while (it.hasNext()) {
             Map.Entry<String, P2PNode> entry = (Map.Entry<String, P2PNode>) it.next();

             // the node might have a -1 index because has never sent anything
             this.addVertex(((P2PNode) entry.getValue()).getName());
         }
    }
    
    protected void generateGraphLinks(Dumper dump) {
   	   //now dump the links
        int i = 0;

        Set<Map.Entry<String, Link>> map2 = (Set<Map.Entry<String, Link>>) dump.getLinks().entrySet();

        Iterator it = map2.iterator();
        while (it.hasNext()) {
            Link entry = ((Map.Entry<String, Link>) it.next()).getValue();
            //  System.out.println("---- looking for sender " + entry.getSource());
            String source= entry.getSource();
            String dest = entry.getDestination();
            //this.addVertex(source);
            //this.addVertex(dest);
            this.addEdge(source, dest);
//            vv.repaint();
//System.out.println("JungGUI.generateGraph()");
            if (mutable) {
            ((LayoutMutable) layout).update();
            if (!vv.isVisRunnerRunning())
                vv.init();
            vv.repaint(); 
            } else {
            	vv.setGraphLayout(this.useNonMutableLayout(graph));
            }
             try {
				Thread.sleep(UPDATE_PAUSE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
             
            //            System.out.println(entry.getSource() + " <---> " + entry.getDestination());
        }
    }
    
    protected void addVertex(String s) {
    	   Vertex v = sl.getVertex(s);
         if (v == null) {
         	// we haven't seen this peer
         	System.out.println(" *** Adding peer  --" + s+"--");
         	v = graph.addVertex(new UndirectedSparseVertex());
         	try {
				sl.setLabel(v, s);
			} catch (UniqueLabelException e) {
				e.printStackTrace();
			}
    }
    }
    
    protected void addEdge(String source, String dest) {
    	Vertex current = sl.getVertex(source);
    	Vertex v = sl.getVertex(dest);
    	graph.addEdge(new UndirectedSparseEdge(current, v));
    }
    
    
    
    
    public static void main(String[] args) {
        JungGUI gui = new JungGUI(args[0]);

        //gui.createGraphFromFile(args[0]);
    }
}
