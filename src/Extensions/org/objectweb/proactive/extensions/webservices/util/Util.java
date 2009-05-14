package org.objectweb.proactive.extensions.webservices.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class Util {

    public static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    private static String simpleName(ZipEntry entry) {
        String entryName = entry.getName();
        if (entryName.lastIndexOf('/') != -1) {
            if (!entryName.endsWith("/")) {
                entryName = entryName.substring(entryName.lastIndexOf('/') + 1);
            } else {
                entryName = entryName.substring(0, entryName.length() - 1);
                if (entryName.lastIndexOf('/') != -1) {
                    entryName = entryName.substring(entryName.lastIndexOf('/') + 1);
                }
            }
        }
        return entryName;
    }

    public static String extractFileFromJar(String jarPath, String entryPath, String destPath,
            boolean insertRandom) {
        try {
            JarFile jar = new JarFile(jarPath);
            ZipEntry entry = jar.getEntry(entryPath);

            if (entry.isDirectory()) {
                logger.error("Entry is a directory");
                return null;
            }

            String entryName = Util.simpleName(entry);

            InputStream in = jar.getInputStream(entry);

            String createdFile;
            if (insertRandom) {
                createdFile = destPath + "/" + Math.random() + "-" + entryName;
            } else {
                createdFile = destPath + "/" + entryName;
            }
            FileOutputStream returnedFile = new FileOutputStream(createdFile);
            OutputStream out = new BufferedOutputStream(returnedFile);
            byte[] buffer = new byte[1024];

            int nBytes;
            while ((nBytes = in.read(buffer)) > 0) {
                out.write(buffer, 0, nBytes);
            }
            out.flush();
            out.close();
            in.close();

            logger.info("Extracted file " + entryName + " to " + destPath);
            return createdFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String extractFromJar(String jarPath, String entryPath, String destPath,
            boolean insertRandom) {

        try {
            JarFile jar = new JarFile(jarPath);
            ZipEntry entry = jar.getEntry(entryPath);

            if (!entry.isDirectory()) {
                return Util.extractFileFromJar(jarPath, entryPath, destPath, insertRandom);
            }

            String entrySimpleName = Util.simpleName(entry);
            String entryName = entry.getName();

            String createdDir = destPath;
            if (insertRandom) {
                createdDir += "/" + Math.random() + "-" + entrySimpleName;
            } else {
                createdDir += "/" + entrySimpleName;
            }
            new File(createdDir).mkdir();

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jEntry = entries.nextElement();
                String name = jEntry.getName();
                int index = name.indexOf(entryName);
                if (index != -1) {
                    String simpleName = name.substring(index + entryName.length());
                    if (!simpleName.endsWith("/") && simpleName.length() != 0) {
                        int slashIndex = simpleName.lastIndexOf('/');
                        String filePath = createdDir;
                        if (slashIndex != -1) {
                            filePath += "/" + simpleName.substring(0, simpleName.lastIndexOf('/'));
                            new File(filePath).mkdirs();
                            logger.info("Created the directory: " + filePath);
                        }
                        Util.extractFileFromJar(jarPath, name, filePath, false);
                    }
                }
            }

            return createdDir;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
