/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC,
 * Canada. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group, University of Victoria IBM CAS, IBM Toronto
 * Lab
 ******************************************************************************/
package org.objectweb.proactive.ic2d.debug.dsi.views;

import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.objectweb.proactive.core.UniqueID;

/**
 * This class encapsulates the process of creating the form view in the PDE
 * visualization tool.
 * 
 * @author Ian Bull
 * 
 */
/* package */class VisualizationForm {

    /*
     * These are all the strings used in the form. These can probably be
     * abstracted for internationalization
     */
    private static String Services_Flow = "Services Communication Flow";
    private static String Controls = "Controls";
    private static String DSI_selection = "DSI Selection";
    private static String Refresh_graph = "Refresh view";

    /*
     * Some parts of the form we may need access to
     */
    private ScrolledForm form;
    private FormToolkit toolkit;
    private GraphViewer viewer;
    private CommunicationGraphView view;

    /*
     * Some buttons that we need to access in local methods
     */
    private Button refreshView = null;

    //	private String currentPathAnalysis = null;
    private SashForm sash;
    private Text searchBox;
    //    private ToolItem cancelIcon;
    private Label searchLabel;

    private Set<UniqueID> dsiList;

    /**
     * Creates the form.
     * 
     * @param toolKit
     * @return
     */
    VisualizationForm(Composite parent, FormToolkit toolkit, CommunicationGraphView view) {
        this.toolkit = toolkit;
        this.view = view;
        form = this.toolkit.createScrolledForm(parent);
        createHeaderRegion(form);
        FillLayout layout = new FillLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 4;
        form.getBody().setLayout(layout);

        this.toolkit.decorateFormHeading(this.form.getForm());
        createSash(form.getBody());
    }

    public void setFocusedNodeName(String nodeName) {
        form.setText(Services_Flow + ": " + nodeName);
        searchBox.setText("");
        form.reflow(true);
    }

    /**
     * Creates the header region of the form, with the search dialog, background
     * and title.  It also sets up the error reporting
     * @param form
     */
    private void createHeaderRegion(ScrolledForm form) {
        Composite headClient = new Composite(form.getForm().getHead(), SWT.NULL);
        GridLayout glayout = new GridLayout();
        glayout.marginWidth = glayout.marginHeight = 0;
        glayout.numColumns = 3;
        headClient.setLayout(glayout);
        headClient.setBackgroundMode(SWT.INHERIT_DEFAULT);
        searchLabel = new Label(headClient, SWT.NONE);
        searchLabel.setText("Search specific flow:");
        searchBox = toolkit.createText(headClient, "");
        GridData data = new GridData();
        data.widthHint = 300;
        searchBox.setLayoutData(data);
        toolkit.paintBordersFor(headClient);
        form.setHeadClient(headClient);
        form.setText(Services_Flow);
        enableSearchBox(false);
    }

    /**
     * Creates the sashform to separate the graph from the controls.
     * 
     * @param parent
     */
    private void createSash(Composite parent) {
        sash = new SashForm(parent, SWT.NONE);
        this.toolkit.paintBordersFor(parent);

        createGraphSection(sash);
        createControlsSection(sash);
        sash.setWeights(new int[] { 10, 2 });
    }

    private class MyGraphViewer extends GraphViewer {
        public MyGraphViewer(Composite parent, int style) {
            super(parent, style);
            Graph graph = new Graph(parent, style) {
                public Point computeSize(int hint, int hint2, boolean changed) {
                    return new Point(0, 0);
                }
            };
            setControl(graph);
        }
    }

    /**
     * Creates the section of the form where the graph is drawn
     * 
     * @param parent
     */
    private void createGraphSection(Composite parent) {
        Section section = this.toolkit.createSection(parent, Section.TITLE_BAR);
        viewer = new MyGraphViewer(section, SWT.NONE);
        section.setClient(viewer.getControl());
    }

    /**
     * Creates the section holding the analysis controls.
     * 
     * @param parent
     */
    private void createControlsSection(Composite parent) {

        SashForm sash = new SashForm(parent, SWT.VERTICAL);
        this.toolkit.paintBordersFor(parent);

        Section controls = this.toolkit.createSection(sash, Section.TITLE_BAR | Section.EXPANDED);
        final Composite dsiComposite = CreateDSISelectionSection(sash);

        controls.setText(Controls);
        Composite controlComposite = new Composite(controls, SWT.NONE) {
            public Point computeSize(int hint, int hint2, boolean changed) {
                return new Point(0, 0);
            }
        };
        this.toolkit.adapt(controlComposite);
        controlComposite.setLayout(new GridLayout());
        refreshView = this.toolkit.createButton(controlComposite, Refresh_graph, SWT.PUSH);
        refreshView.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        refreshView.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        dsiList = view.refresh();
                        UpdateDSISelectionSection(dsiComposite);
                        enableSearchBox(true);
                    }
                });
            }
        });
        sash.setWeights(new int[] { 1, 12 });
        sash.pack();
        controls.setClient(controlComposite);
    }

    private Composite CreateDSISelectionSection(Composite parent){
        Section dsiSelect = this.toolkit.createSection(parent, Section.TITLE_BAR | Section.EXPANDED);
        dsiSelect.setText(DSI_selection);
        Composite dsiSelectComposite = new Composite(dsiSelect, SWT.NONE) {
            public Point computeSize(int hint, int hint2, boolean changed) {
                return new Point(0, 0);
            }
        };

        this.toolkit.adapt(dsiSelectComposite);
        dsiSelectComposite.setLayout(new GridLayout());

        dsiSelect.setClient(dsiSelectComposite);
        return dsiSelectComposite;
    }

    private void UpdateDSISelectionSection(Composite parent){
        for (Control c : parent.getChildren()){
            c.dispose();
        }
        for(UniqueID dsi : dsiList){
            final Button b = this.toolkit.createButton(parent, dsi.shortString(), SWT.CHECK);
            b.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
            b.addSelectionListener(new SelectionAdapter() {
                private boolean highlighted = false;
                
                public void widgetSelected(SelectionEvent e) {
                    highlighted = !highlighted;
                    view.highlightDSI(b.getText(),highlighted);
                }
            });
        }
        parent.layout(true);
    }

    /**
     * Gets the currentGraphViewern
     * 
     * @return
     */
    public GraphViewer getGraphViewer() {
        return viewer;
    }

    /**
     * Gets the form we created.
     */
    public ScrolledForm getForm() {
        return form;
    }


    public void enableSearchBox(boolean enable) {
        this.searchLabel.setEnabled(enable);
        this.searchBox.setEnabled(enable);
    }

    private Image getImage(int type) {
        switch (type) {
            case IMessageProvider.ERROR:
                return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
            case IMessageProvider.WARNING:
                return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
            case IMessageProvider.INFORMATION:
                return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
        }
        return null;
    }
}
