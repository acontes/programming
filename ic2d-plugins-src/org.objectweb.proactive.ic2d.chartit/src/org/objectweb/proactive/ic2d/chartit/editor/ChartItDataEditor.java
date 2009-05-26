/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s): ActiveEon Team - http://www.activeeon.com
 *
 * ################################################################
 * $$ACTIVEEON_CONTRIBUTOR$$
 */
package org.objectweb.proactive.ic2d.chartit.editor;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.objectweb.proactive.ic2d.chartit.Activator;
import org.objectweb.proactive.ic2d.chartit.data.resource.IResourceDescriptor;
import org.objectweb.proactive.ic2d.chartit.data.resource.ResourceData;
import org.objectweb.proactive.ic2d.chartit.data.resource.ResourceDataBuilder;
import org.objectweb.proactive.ic2d.chartit.editor.page.ChartsPage;
import org.objectweb.proactive.ic2d.chartit.editor.page.OverviewPage;
import org.objectweb.proactive.ic2d.console.Console;


/**
 * A multi-page form editor that uses Eclipse Forms support.
 * <p>
 * Two pages are available; the first is an overview page composed of multiple
 * sections for charts configuration, the second is the charts page.
 * 
 * @author <a href="mailto:support@activeeon.com">ActiveEon Team</a>.
 */
public final class ChartItDataEditor extends FormEditor {

    /**
     * The id of this editor
     */
    public static final String ID = "org.objectweb.proactive.ic2d.chartit.editors.ChartItDataEditor";

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.forms.editor.FormEditor#createToolkit(org.eclipse.swt.
     * widgets.Display)
     */
    protected FormToolkit createToolkit(final Display display) {
        // Create a toolkit that shares colors between editors.
        return new FormToolkit(Activator.getDefault().getFormColors(display));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages() {
        try {
            super.addPage(new OverviewPage(this));
            super.addPage(new ChartsPage(this));
        } catch (PartInitException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#dispose()
     */
    @Override
    public void dispose() {
        // Don't forget to close the model
        ((ChartItDataEditorInput) this.getEditorInput()).getModelsContainer().stopCollector();
        // Free all controls in editor input
        ((ChartItDataEditorInput) this.getEditorInput()).controlsToDisable.clear();
        super.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor
     * )
     */
    public void doSave(IProgressMonitor monitor) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * Opens a new ChartItEditor from a resource descriptor.
     * 
     * @param resourceDescriptor
     *            The descriptor of the resource
     * @throws PartInitException
     *             Thrown if the part can not be activated
     */
    public static ChartItDataEditor openNewFromResourceDescriptor(final IResourceDescriptor resourceDescriptor)
            throws PartInitException {
        // First create a resource data from descriptor
        final ResourceData resourceData = ResourceDataBuilder
                .buildResourceDataFromDescriptor(resourceDescriptor);
        // Open an editor from
        ChartItDataEditor editor = (ChartItDataEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().openEditor(new ChartItDataEditorInput(resourceData), ChartItDataEditor.ID,
                        true);
        // The name of the editor is the name of the resource descriptor
        editor.setPartName(resourceDescriptor.getName());

        String configFilename = resourceDescriptor.getConfigFilename();
        // If a configuration file was specified load it
        if (configFilename != null) {
            // Locate default config file in 'config' directory of chartit
            // plugin
            final URL configURL = FileLocator.find(Activator.getDefault().getBundle(), new Path("config/" +
                configFilename), null);

            // Get the overview page
            final OverviewPage overviewPage = (OverviewPage) editor.getActivePageInstance();
            try {
                overviewPage.getChartsSW().loadConfigFromXML(FileLocator.toFileURL(configURL).getPath());
                if (overviewPage.canLeaveThePage()) {
                    editor.setActivePage(ChartsPage.CHARTS_PAGE_NAME);
                }
            } catch (Exception e) {
                Console.getInstance(Activator.CONSOLE_NAME).log(
                        "Cannot locate the config file : " + configFilename +
                            " in the config directory of ChartIt plugin ! " + e);
                e.printStackTrace();
            }

        }
        return editor;
    }
}