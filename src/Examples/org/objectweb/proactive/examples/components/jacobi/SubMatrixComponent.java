package org.objectweb.proactive.examples.components.jacobi;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.examples.jacobi.SubMatrix;


public class SubMatrixComponent extends SubMatrix implements GathercastDataReceiver, Main,
        SubMatrixAttributes, BindingController {

    //	Map<Integer, double[]> bordersData = new HashMap<Integer, double[]>();
    List<LineData> bordersData = new ArrayList<LineData>();

    boolean useMulticast = true;

    Map<String, CollectionDataSender> collectionOfSenderItfs = new HashMap<String, CollectionDataSender>();

    MulticastDataSender bordersSender;

    String coordinates = "";

    long sendingDataTime = 0;

    int x = -1;
    int y = -1;

    String globalDimensions;

    int globalWidth = -1;
    int globalHeight = -1;

    public SubMatrixComponent() {
        super();
    }

    public String getNbIterations() {
        throw new RuntimeException("not implemented");
    }

    public void setNbIterations(String nbIterations) {
        iterationsToStop = Integer.parseInt(nbIterations);
    }

    public void setDimensions(String xy) {

        String[] values = xy.split(";");
        width = Integer.parseInt(values[0]);
        height = Integer.parseInt(values[1]);
        this.current = new double[width * height];
        this.old = new double[width * height];
        for (int i = 0; i < this.old.length; i++) {
            //            this.old[i] = Math.random() * 10000;
            //        	this.old[i] = value;
            this.old[i] = 1000000;
        }
        value++;
    }

    public String getDimensions() {
        throw new RuntimeException("not implemented");
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
        //		System.out.println("["+coordinates + "] coordinates set");
        String[] xy = coordinates.split(";");
        x = Integer.parseInt(xy[0]);
        y = Integer.parseInt(xy[1]);

    }

    public void useCollectionInsteadOfMulticast() {
        useMulticast = false;
    }

    public String getGlobalDimensions() {
        return globalDimensions;
    }

    public void setGlobalDimensions(String dimensions) {
        this.globalDimensions = dimensions;
        String[] globalDimensionsArray = dimensions.split(";");
        globalWidth = Integer.parseInt(globalDimensionsArray[0]);
        globalHeight = Integer.parseInt(globalDimensionsArray[1]);
        //		System.out.println("["+coordinates + "] globalDimensions set : " + globalDimensions);
    }

    @Override
    public void sendBordersToNeighbors() {
        //		try {
        //			Thread.sleep(1000);
        //		} catch (InterruptedException e) {
        //			e.printStackTrace();
        //		}
        //		System.out.println("["+coordinates + "] ("+iterationsToStop+") sending ");
        long init = System.currentTimeMillis();
        if (!collectionOfSenderItfs.isEmpty()) {
            // use the collection bindings
            if (y != 0) {
                collectionOfSenderItfs.get("sender-collection-NORTH").exchangeData(
                        new LineData(LineData.NORTH, buildNorthBorder(), iterationsToStop, coordinates));
            }
            if (x < (globalWidth - 1)) {
                collectionOfSenderItfs.get("sender-collection-EAST").exchangeData(
                        new LineData(LineData.EAST, buildEastBorder(), iterationsToStop, coordinates));
            }
            if (y < (globalHeight - 1)) {
                collectionOfSenderItfs.get("sender-collection-SOUTH").exchangeData(
                        new LineData(LineData.SOUTH, buildSouthBorder(), iterationsToStop, coordinates));
            }
            if (x != 0) {
                collectionOfSenderItfs.get("sender-collection-WEST").exchangeData(
                        new LineData(LineData.WEST, buildWestBorder(), iterationsToStop, coordinates));
            }

        } else {

            bordersData.clear();

            if (y != 0) {
                bordersData.add(new LineData(LineData.NORTH, buildNorthBorder(), iterationsToStop,
                    coordinates));
            }
            if (x < (globalWidth - 1)) {
                bordersData
                        .add(new LineData(LineData.EAST, buildEastBorder(), iterationsToStop, coordinates));
            }
            if (y < (globalHeight - 1)) {
                bordersData.add(new LineData(LineData.SOUTH, buildSouthBorder(), iterationsToStop,
                    coordinates));
            }
            if (x != 0) {
                bordersData
                        .add(new LineData(LineData.WEST, buildWestBorder(), iterationsToStop, coordinates));
            }
            bordersSender.exchangeData(bordersData);
        }

        sendingDataTime += (System.currentTimeMillis() - init);

    }

    public void exchangeData(List<LineData> borders) {

        //		System.out.println("["+coordinates + "] ("+iterationsToStop+") receiving ");

        for (Iterator iter = borders.iterator(); iter.hasNext();) {

            LineData element = (LineData) iter.next();
            //			System.out.println("["+coordinates + "] ("+iterationsToStop+") receiving from [" + element.getCoordinates()  + "] ("+element.getIteration() + ")" );
            if (element.getPosition().equals(LineData.EAST)) {
                westNeighborBorder = element.getData();
            }
            if (element.getPosition().equals(LineData.WEST)) {
                eastNeighborBorder = element.getData();
            }
            if (element.getPosition().equals(LineData.NORTH)) {
                southNeighborBorder = element.getData();
            }
            if (element.getPosition().equals(LineData.SOUTH)) {
                northNeighborBorder = element.getData();
            }
        }

        loop();

    }

    @Override
    public void loop() {

        if (iterationsToStop <= 0) {

            endTime = System.currentTimeMillis();
            System.out.println("[" + coordinates + "] computation time = " + (endTime - startTime));
            System.out.println("[" + coordinates + "] terminated iterations \n");
            System.out.println("[" + coordinates + "] sending data took " + sendingDataTime + " \n");
            System.out.println("[" + coordinates + "] top left and bottom right elements are " + get(0, 0) +
                " ; " + get(width - 1, height - 1) + "\n");
            try {
                //System.out.println("writing results to file Matrix_"+coordinates.replace(";", "_"));
                PrintWriter pw = new PrintWriter(new FileWriter("Matrix_" + coordinates.replace(";", "_")));
                pw.write(matrixToString());
                pw.close();
                //System.out.println("FINISH writing results to file Matrix_"+coordinates.replace(";", "_"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //			if (minDiff>Jacobi.MINDIFF) {
            //			System.out.println("[" + coordinates +
            //	                "] Computation over :\n      " + this.minDiff +
            //	                " (asked less than " + Jacobi.MINDIFF + ")");
            ////	            if (this.minDiff < Jacobi.MINDIFF) {
            ////	                System.out.println("[" + this.name +
            ////	                    "] sent the \"end signal\"");
            //////	                this.matrix.stop();
            ////	            }
            //		
            //			}
            //			 else {

            internalCompute();

            borderCompute();

            exchange();

            sendBordersToNeighbors();

            iterationsToStop--;

        }

    }

    public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        if ("sender".equals(clientItfName)) {
            bordersSender = (MulticastDataSender) serverItf;
        } else if (clientItfName.startsWith("sender-collection-")) {
            collectionOfSenderItfs.put(clientItfName, (CollectionDataSender) serverItf);
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public String[] listFc() {
        return new String[] { "sender", "sender-collection-" };
    }

    public Object lookupFc(String clientItfName) throws NoSuchInterfaceException {
        if ("sender".equals(clientItfName)) {
            return bordersSender;
        } else if (clientItfName.startsWith("sender-collection-")) {
            return collectionOfSenderItfs.get(clientItfName);
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }

    }

    public void unbindFc(String clientItfName) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        if ("sender".equals(clientItfName)) {
            bordersSender = null;
        } else if (clientItfName.startsWith("sender-collection-")) {
            collectionOfSenderItfs.remove(clientItfName);
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }

    }

    public void go() {
        try {
            System.out.println("[" + coordinates + "] starting computation for " + iterationsToStop +
                " iterations on machine " + InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        startTime = System.currentTimeMillis();

        // initialize borders
        northNeighborBorder = buildFakeBorder(width);
        southNeighborBorder = buildFakeBorder(width);
        westNeighborBorder = buildFakeBorder(height);
        eastNeighborBorder = buildFakeBorder(height);

        sendBordersToNeighbors();

    }

    @Override
    public String matrixToString() {
        //System.err.println("matrixToStringXXXXXXXXXXXXXXXXXXXXXXXXXXx");
        String result = "";
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //				result += MathUtils.round(get(i, j),2) + " ";
                result += get(i, j) + " ";
            }
            result += "\n";
        }
        result += "\n";
        //System.err.println("ENDmatrixToStringXXXXXXXXXXXXXXXXXXXXXXXXXXx");
        return result;
    }

    public int getIteration() {
        return iterationsToStop;
    }

}
