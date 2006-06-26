package org.objectweb.proactive.p2p.jung;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.p2p.test.Dumper;
import org.objectweb.proactive.p2p.test.Link;
import org.objectweb.proactive.p2p.test.P2PNode;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.ConstantEdgeStringer;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.ToolTipFunction;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.FadingVertexLayout;
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


public class JungGUI implements ToolTipFunction{
	
	//the time in ms we wait after a vertex has been added
	private final int UPDATE_PAUSE = 100;
	
    protected Graph graph;
    protected VisualizationViewer vv;
    protected PluggableRenderer pr;
    protected StringLabeller sl;
    protected ConstantEdgeStringer edgesLabeller;
    //protected Integer key = new Integer(1);
    protected Layout layout;
    protected boolean mutable;
    
  
    
    public JungGUI() {
        graph = new UndirectedSparseGraph(); //this.createGraph();
                                  
//layout = useNonMutableLayout(graph);
      //layout = useMutableLayout(graph);
        layout = useLayout(graph,0);
          
        //Layout layout = new ISOMLayout(graph);
        sl = StringLabeller.getLabeller(graph);
       edgesLabeller = new ConstantEdgeStringer(null);
       
        pr = new PluggableRenderer();
 //      pr = new CirclePluggableRenderer((CircleLayout) layout);
        pr.setVertexStringer(sl);
        pr.setVertexPaintFunction(new NOAVertexPaintFunction());
        
        // Creer un GraphLabelRenderer qui retourne notre RotateLabel
        
        //pr.setGraphLabelRenderer()
        // pr.setVertexLabelCentering(true);
        vv = new VisualizationViewer(layout, pr, new Dimension(1024, 768));
        vv.setPickSupport(new ShapePickSupport());
        pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
        vv.setBackground(Color.white);
        vv.setToolTipListener(this);
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(Mode.PICKING);
        vv.setGraphMouse(gm);
    }

  public void changeLayout(int i) {
      Layout l = useLayout(graph,i);
      vv.stop();
      vv.setGraphLayout(l);
      vv.restart();
      
  }
/**
 * Indicates which layout to use
 * 0 : circle layout
 * 1 : KK Layout
 * 2 : FR Layout
 * 3 : Spring Layout
 * @param g
 * @param i the number of the layout to use
 * @return
 */
    public Layout useLayout(Graph g, int i) {
    	
    	switch (i) {
    		case 0:	{
    			this.mutable = false;
    			return this.useCircleLayout(g);
    		}
    		case 1: {
    			this.mutable = false;
    			return this.useKKLayout(g);
    		}
    		case 2: {
    			this.mutable = true;
    			return this.useFRLayout(g);
    		}
    		case 3 : {
    			this.mutable = true;
    			return this.useSpringLayout(g);
    		} 
    		
    	//return this.useFadingVertexLayout(g);
//         return this.useTreeLayout(g);
    		default : return null;
    	}
    }
    
//    public Layout useMutableLayout(Graph g) {
//    	this.mutable = true;
//    	return this.useSpringLayout(g);
//    //	return  this.useFRLayout(g);
//    }
//    

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
                    	 // example of string
                    	// "trinidad.inria.fr:2410 current Noa =  1 max Noa= 3"
                        Pattern pattern = Pattern.compile("(.*) current .* =  (.*) max Noa= (.*)");
                        Matcher matcher = pattern.matcher(s);
                        boolean matchFound = matcher.find();
                        
                        if (matchFound) {
                            // Get all groups for this match
                        //    for (int i=1; i<=matcher.groupCount(); i++) {
                                s = matcher.group(1);
                                //System.out.println(groupStr);
                          //  }
                        //}
                    	
                    	
                    	
                        //s= s.substring(0, s.indexOf("current")-1);
                        dump.addAsSender(s, Integer.parseInt(matcher.group(2)),Integer.parseInt(matcher.group(3)));
                        current = s;
                        readingStatus = 2;
                        }
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
    P2PNode node = ((P2PNode) entry.getValue());
             this.addVertex(node);
         }
         layout.restart();
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
          this.updateView();
             
        }
    }
    
    protected void updateView() {
    	//vv.suspend();
    	if (mutable) {
              ((LayoutMutable) layout).update();
              if (!vv.isVisRunnerRunning())
                  vv.init();
              
              } else {
              	//vv.setGraphLayout(this.useNonMutableLayout(graph));
            	 // this.layout.restart();
              }
               try {
  				Thread.sleep(UPDATE_PAUSE);
  			} catch (InterruptedException e) {
  				e.printStackTrace();
  			}
    	
//    	  // make your changes to the graph here
//    	 	graph.addVertex(new SparseVertex());
//    	 
    		//vv.unsuspend();
    		vv.repaint();
    	 
    }
    
    protected void addVertex(P2PNode p) {
    	String s = p.getName();
    	   P2PUndirectedSparseVertex v = (P2PUndirectedSparseVertex) sl.getVertex(s);
         if (v == null) {
         	// we haven't seen this peer
         	System.out.println(" *** Adding peer  --" + s+"--");
         	v = (P2PUndirectedSparseVertex) graph.addVertex(new P2PUndirectedSparseVertex());
         	try {
				sl.setLabel(v, s);
			} catch (UniqueLabelException e) {
				e.printStackTrace();
			}
			v.setMaxNOA(p.getMaxNOA());
			v.setNoa(p.getNoa());
			
			
    }
    }
    
    protected void addEdge(String source, String dest) {
    	Vertex current = sl.getVertex(source);
    	Vertex v = sl.getVertex(dest);
    	graph.addEdge(new UndirectedSparseEdge(current, v));
    }
    
    public JPanel getPanel() {
    	return this.vv;
    }
    
    public void setRepulsionRange(int i) {
    	System.out.println("JungGUI.setRepulsionRange() " +i);
    	((SpringLayout) this.layout).setRepulsionRange(i);
    }
    
    public static void main(String[] args) {
    	JFrame f = new JFrame();
        JungGUI gui = new JungGUI();
        
   	f.add(gui.getPanel());
        f.pack();
        f.setVisible(true);
        gui.createGraphFromFile2(args[0]);
        //gui.createGraphFromFile(args[0]);
    }

	public String getToolTipText(Vertex v) {
		//System.out.println("JungGUI.getToolTipText() " + v);
	    return "<html> "+ sl.getLabel(v)+" <br> noa = " + ((P2PUndirectedSparseVertex)v).getNoa()+ "</html>";
		//return null;
	}

	public String getToolTipText(Edge e) {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public String getToolTipText(MouseEvent event) {
		// TODO Raccord de méthode auto-généré
		return null;
	}
}
