/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package testsuite.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.data.TableXYDataset;
import org.jfree.data.XYSeries;
import org.xml.sax.SAXException;

import testsuite.group.Group;
import testsuite.result.AbstractResult;
import testsuite.result.BenchmarkResult;
import testsuite.result.ResultsCollections;
import testsuite.test.Benchmark;
import testsuite.timer.Timeable;
import testsuite.timer.ms.MsTimer;
import testsuite.xslt.TransformerXSLT;


/**
 * @author Alexandre di Costanzo
 *
 */
public abstract class BenchmarkManager extends AbstractManager {
    protected Timeable timer = new MsTimer();

    /**
     *
     */
    public BenchmarkManager() {
        super("BenchmarkManager with no name",
            "BenchmarkManager with no description");
    }

    /**
     * @param name
     * @param description
     */
    public BenchmarkManager(String name, String description) {
        super(name, description);
    }

    public BenchmarkManager(File xmlDescriptor)
        throws IOException, SAXException {
        super(xmlDescriptor);
        this.loadAttributes(this.getProperties());
    }

    /**
     * @see testsuite.manager.AbstractManager#execute()
     */
    public void execute(boolean useAttributesFile) {
        if (logger.isInfoEnabled()) {
            logger.info("Starting ...");
        }
        ResultsCollections results = getResults();

        if (useAttributesFile) {
            try {
                loadAttributes();
            } catch (IOException e1) {
                if (logger.isInfoEnabled()) {
                    logger.info(e1);
                }
            }
        }

        results.add(AbstractResult.IMP_MSG, "Starting ...");
        try {
            initManager();
        } catch (Exception e) {
            logger.fatal("Can't init the manager", e);
            results.add(AbstractResult.ERROR, "Can't init the manager", e);
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("Init Manager with success");
        }
        results.add(AbstractResult.IMP_MSG, "Init Manager with success");

        Iterator itGroup = iterator();
        while (itGroup.hasNext()) {
            Group group = (Group) itGroup.next();
            ResultsCollections resultsGroup = group.getResults();

            try {
                group.initGroup(this.timer);
            } catch (Exception e) {
                logger.warn("Can't init group of benchmarks: " +
                    group.getName(), e);
                resultsGroup.add(AbstractResult.ERROR,
                    "Can't init group of benchmarks: " + group.getName(), e);
                results.addAll(resultsGroup);
                continue;
            }

            int runs = 0;
            int errors = 0;
            long sum = 0;
            Iterator itTest = group.iterator();
            while (itTest.hasNext()) {
                Benchmark benchmark = (Benchmark) itTest.next();
                long[] set = new long[getNbRuns()];
                for (int i = 0; i < getNbRuns(); i++) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("lauching benchmark: " +
                            benchmark.getName());
                    }
                    AbstractResult result = benchmark.runTest();

                    if (benchmark.isFailed()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Result from benchmark " +
                                benchmark.getName() + " is [FAILED]");
                        }

                        errors++;
                    } else {
                        set[i] = benchmark.getResultTime();
                        if (logger.isDebugEnabled()) {
                            logger.debug("Result from benchmark " +
                                benchmark.getName() + " is run in " +
                                benchmark.getResultTime() +
                                this.timer.getUnit());
                        }
                        runs++;
                        sum += benchmark.getResultTime();
                    }
                }
                resultsGroup.add(new BenchmarkResult(benchmark,
                        AbstractResult.GLOBAL_RESULT, "no message", set));
            }
            resultsGroup.add(AbstractResult.GLOBAL_RESULT,
                "Group : " + group.getName() + ", Moy in " +
                ((runs == 0) ? "Failed"
                             : ((sum / (double) runs) + this.timer.getUnit())) +
                " Runs : " + runs + " Errors : " + errors);

            try {
                group.endGroup();
            } catch (Exception e) {
                logger.warn("Can't ending group of benchmarks: " +
                    group.getName(), e);
                resultsGroup.add(AbstractResult.ERROR,
                    "Can't ending group of benchmarks: " + group.getName(), e);
                results.addAll(resultsGroup);
                continue;
            }
            results.addAll(resultsGroup);
        }

        try {
            endManager();
        } catch (Exception e) {
            logger.fatal("Can't ending the manager", e);
            results.add(AbstractResult.ERROR, "Can't ending the manager", e);
            return;
        }

        results.add(AbstractResult.IMP_MSG, "... Finish");
        if (logger.isInfoEnabled()) {
            logger.info("... Finish");
        }

        this.showResult();
    }

    /**
     * @see testsuite.result.ResultsExporter#toHTML(java.io.File)
     */
    public void toHTML(File location)
        throws ParserConfigurationException, TransformerException, IOException {
        createBenchGraph(location.getParentFile());
        createBarCharts(location.getParentFile());
        if (logger.isInfoEnabled()) {
            logger.info("Creating HTML file ...");
        }
        String xslPath = "/" +
            AbstractManager.class.getName().replace('.', '/').replaceAll("manager.*",
                "/xslt/benchmark.xsl");
        TransformerXSLT.transformerTo(toXML(), location, xslPath);

        // copy css
        String cssPath = "/" +
            AbstractManager.class.getName().replace('.', '/').replaceAll("manager.*",
                "/css/stylesheet.css");
        InputStream css = getClass().getResourceAsStream(cssPath);
        File copy = new File(location.getParent() + File.separator +
                "stylesheet.css");
        FileOutputStream out = new FileOutputStream(copy);
        byte[] buffer = new byte[1024];
        int nbBytes = 0;

        while (nbBytes != -1) {
            nbBytes = css.read(buffer);
            if (nbBytes > 0) {
                out.write(buffer, 0, nbBytes);
            }
        }
        css.close();
        out.close();
        if (logger.isInfoEnabled()) {
            logger.info("... Finish creating HTML file");
        }
    }

    private void createBarCharts(File location) throws IOException {
        if (!location.isDirectory()) {
            throw new IOException(location.getName() + " is not a directory !");
        }

        if (logger.isInfoEnabled()) {
            logger.info("Create all bar charts ...");
        }

        File graphDir = new File(location, "bench_results_files");
        if (!graphDir.exists()) {
            graphDir.mkdir();
        }

        Iterator itGroup = getGroups().iterator();
        int numGroup = 0;
        while (itGroup.hasNext()) {
            numGroup++;
            Group group = (Group) itGroup.next();
            Iterator itResult = group.getResults().iterator();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            while (itResult.hasNext()) {
                AbstractResult absResult = (AbstractResult) itResult.next();
                if (absResult instanceof BenchmarkResult) {
                    BenchmarkResult result = (BenchmarkResult) absResult;
                    dataset.addValue(result.getTimeResult(),
                        result.getTest().getName(), group.getName());
                }
            }
            JFreeChart chart = ChartFactory.createBarChart3D(group.getName(),
                    "Benchmarks", this.timer.getUnit(), dataset,
                    PlotOrientation.VERTICAL, true, true, false);
            ChartUtilities.saveChartAsPNG(new File(graphDir.getPath() +
                    File.separator + "Group" + numGroup + ".png"), chart, 800,
                600);
            if (logger.isInfoEnabled()) {
                logger.info("Bar Chart " + graphDir.getPath() + File.separator +
                    "Group" + numGroup + ".png created");
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("... Finish to create bar charts");
        }
    }

    private void createBenchGraph(File location) throws IOException {
        if (!location.isDirectory()) {
            throw new IOException(location.getName() + " is not a directory !");
        }

        if (logger.isInfoEnabled()) {
            logger.info("Create all charts ...");
        }

        File graphDir = new File(location, "bench_results_files");
        if (!graphDir.exists()) {
            graphDir.mkdir();
        }

        ResultsCollections results = this.getResults();
        Iterator it = results.iterator();
        int numBench = 0;
        while (it.hasNext()) {
            AbstractResult absResult = (AbstractResult) it.next();
            if (absResult instanceof BenchmarkResult) {
                BenchmarkResult result = (BenchmarkResult) absResult;
                XYSeries series = new XYSeries(result.getTest().getName());
                for (int i = 0; i < result.getSet().length; i++) {
                    series.add(i + 1, result.getSet()[i]);
                }
                TableXYDataset table = new TableXYDataset(series);
                XYPlot plot = new XYPlot(table, new NumberAxis("Nb Runs"),
                        new NumberAxis("Time in " + this.timer.getUnit()),
                        new StandardXYItemRenderer(
                            StandardXYItemRenderer.SHAPES_AND_LINES));
                JFreeChart chart = new JFreeChart(result.getTest().getName(),
                        plot);
                ChartUtilities.saveChartAsPNG(new File(graphDir.getPath() +
                        File.separator + "Bench" + numBench + ".png"), chart,
                    800, 600);
                numBench++;
                if (logger.isInfoEnabled()) {
                    logger.info("Chart " + graphDir.getPath() + File.separator +
                        "Bench" + numBench + ".png created");
                }
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("... Finish to create charts");
        }
    }

    /**
     * @return
     */
    public Timeable getTimer() {
        return this.timer;
    }

    public void setTimer(String className) {
        try {
            Class c = getClass().getClassLoader().loadClass(className);
            this.timer = (Timeable) c.newInstance();
        } catch (ClassNotFoundException e) {
            logger.warn(className + " was not found. Use default timer", e);
        } catch (InstantiationException e) {
            logger.warn(className +
                " could't be instancied. Use default timer", e);
        } catch (IllegalAccessException e) {
            logger.warn(className + " illegal access. Use default timer", e);
        }
    }

    public void setTimer(Timeable timer) {
        this.timer = timer;
    }
}
