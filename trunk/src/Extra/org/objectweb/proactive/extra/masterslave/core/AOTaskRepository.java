package org.objectweb.proactive.extra.masterslave.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Logger;
import org.objectweb.proactive.api.ProActiveObject;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.masterslave.TaskAlreadySubmittedException;
import org.objectweb.proactive.extra.masterslave.interfaces.Task;
import org.objectweb.proactive.extra.masterslave.interfaces.internal.TaskIntern;
import org.objectweb.proactive.extra.masterslave.interfaces.internal.TaskRepository;


/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i><br>
 * This active object acts as a repository of tasks that are currently processed by the master<br>
 * The master asks this repository for the real task by giving the task id.<br>
 * The purpose of this class is to save having duplicated task objects within the framework <br>
 * @author fviale
 *
 */
public class AOTaskRepository implements TaskRepository<Task<?extends Serializable>>,
    Serializable {

    /**
         *
         */
    private static final long serialVersionUID = 6749695599768934980L;

    /**
    * logger of the task repository
    */
    protected static Logger logger = ProActiveLogger.getLogger(Loggers.MASTERSLAVE_REPOSITORY);

    /**
     * set whichs stores the hashcodes of all task objects from the client environment
     */
    protected HashSet<Integer> hashCodes = new HashSet<Integer>();

    /**
     * associations of task ids to hashcodes
     */
    protected HashMap<Long, Integer> idTohashCode = new HashMap<Long, Integer>();

    /**
     * associations of ids to actual tasks
     */
    protected HashMap<Long, TaskIntern<Serializable>> idToTaskIntern = new HashMap<Long, TaskIntern<Serializable>>();

    /**
     * associations of ids to zipped versions of the tasks
     */
    protected HashMap<Long, byte[]> idToZippedTask = new HashMap<Long, byte[]>();

    /**
     * counter of the last task id created
     */
    protected long taskCounter = 0;

    /**
     * Size of compression buffers
     */
    private static final int COMPRESSION_BUFFER_SIZE = 1024;

    /**
     * ProActive empty constructor
     */
    public AOTaskRepository() {
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public long addTask(final Task<?extends Serializable> task,
        final int hashCode) throws TaskAlreadySubmittedException {
        if (hashCodes.contains(hashCode)) {
            throw new TaskAlreadySubmittedException();
        }

        hashCodes.add(hashCode);
        idTohashCode.put(taskCounter, hashCode);
        TaskIntern<Serializable> ti = new TaskWrapperImpl(taskCounter,
                (Task<Serializable>) task);
        idToTaskIntern.put(taskCounter, ti);
        taskCounter = (taskCounter + 1) % (Long.MAX_VALUE - 1);
        return ti.getId();
    }

    /**
     * {@inheritDoc}
     */
    public TaskIntern<Serializable> getTask(final long id) {
        if (!idToTaskIntern.containsKey(id) && !idToZippedTask.containsKey(id)) {
            throw new NoSuchElementException("task unknown");
        }

        if (idToTaskIntern.containsKey(id)) {
            return idToTaskIntern.get(id);
        } else {
            return loadTask(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeTask(final long id) {
        if (!idToTaskIntern.containsKey(id) &&
                !(idToZippedTask.containsKey(id))) {
            throw new NoSuchElementException("task unknown");
        }

        if (idToTaskIntern.containsKey(id)) {
            idToTaskIntern.remove(id);
        } else {
            idToZippedTask.remove(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeId(final long id) {
        if (!idTohashCode.containsKey(id)) {
            throw new NoSuchElementException("unknown id");
        }

        int hashCode = idTohashCode.get(id);
        hashCodes.remove(hashCode);
    }

    /**
     * loads a task from a compressed version
     * @param id the task id
     * @return the loaded task
     */
    @SuppressWarnings("unchecked")
    protected TaskIntern<Serializable> loadTask(final long id) {
        TaskIntern<Serializable> task = null;
        if (!idToZippedTask.containsKey(id)) {
            throw new NoSuchElementException("task unknown");
        }

        byte[] compressedData = idToZippedTask.get(id);

        // Create the decompressor and give it the data to compress
        Inflater decompressor = new Inflater();
        decompressor.setInput(compressedData);

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);

        // Decompress the data
        byte[] buf = new byte[COMPRESSION_BUFFER_SIZE];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
                logger.error("Error during task decompression", e);
            }
        }

        try {
            bos.close();
        } catch (IOException e) {
            logger.error("Error during task decompression", e);
        }

        // Get the decompressed data
        byte[] decompressedData = bos.toByteArray();
        ByteArrayInputStream objectInput = new ByteArrayInputStream(decompressedData);
        try {
            ObjectInputStream ois = new ObjectInputStream(objectInput);
            task = (TaskIntern<Serializable>) ois.readObject();
            idToTaskIntern.put(id, task);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return task;
    }

    /**
     * {@inheritDoc}
     */
    public void saveTask(final long id) {
        if (!idToTaskIntern.containsKey(id)) {
            throw new NoSuchElementException("task unknown");
        }

        TaskIntern<Serializable> ti = idToTaskIntern.remove(id);

        // Serialize the task
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(ti);
            oos.flush();
            byte[] input = baos.toByteArray();

            // Compress the data
            Deflater compressor = new Deflater();
            compressor.setStrategy(Deflater.FILTERED);
            compressor.setLevel(Deflater.BEST_COMPRESSION);
            compressor.setInput(input);
            compressor.finish();
            // Create an expandable byte array to hold the compressed data.
            // You cannot use an array that's the same size as the orginal because
            // there is no guarantee that the compressed data will be smaller than
            // the uncompressed data.
            ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

            // Compress the data
            byte[] buf = new byte[COMPRESSION_BUFFER_SIZE];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }

            try {
                bos.close();
            } catch (IOException e) {
                logger.error("Error during task compression", e);
            }

            // Get the compressed data
            byte[] compressedData = bos.toByteArray();
            idToZippedTask.put(id, compressedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean terminate() {
        ProActiveObject.terminateActiveObject(true);
        return true;
    }
}
