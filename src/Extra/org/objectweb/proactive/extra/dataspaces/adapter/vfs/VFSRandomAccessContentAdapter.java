package org.objectweb.proactive.extra.dataspaces.adapter.vfs;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.proactive.extra.dataspaces.api.RandomAccessContent;


public class VFSRandomAccessContentAdapter implements RandomAccessContent {

    final private org.apache.commons.vfs.RandomAccessContent adaptee;

    public VFSRandomAccessContentAdapter(org.apache.commons.vfs.RandomAccessContent randomAccessContent) {
        adaptee = randomAccessContent;
    }

    public void close() throws IOException {
        adaptee.close();
    }

    public long getFilePointer() throws IOException {
        return adaptee.getFilePointer();
    }

    public InputStream getInputStream() throws IOException {
        return adaptee.getInputStream();
    }

    public long length() throws IOException {
        return adaptee.length();
    }

    public void seek(long pos) throws IOException {
        adaptee.seek(pos);
    }

    public void write(int b) throws IOException {
        adaptee.write(b);
    }

    public void write(byte[] b) throws IOException {
        adaptee.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        adaptee.write(b, off, len);
    }

    public void writeBoolean(boolean v) throws IOException {
        adaptee.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
        adaptee.writeByte(v);
    }

    public void writeBytes(String s) throws IOException {
        adaptee.writeBytes(s);
    }

    public void writeChar(int v) throws IOException {
        adaptee.writeChar(v);
    }

    public void writeChars(String s) throws IOException {
        adaptee.writeChars(s);
    }

    public void writeDouble(double v) throws IOException {
        adaptee.writeDouble(v);
    }

    public void writeFloat(float v) throws IOException {
        adaptee.writeFloat(v);
    }

    public void writeInt(int v) throws IOException {
        adaptee.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        adaptee.writeLong(v);
    }

    public void writeShort(int v) throws IOException {
        adaptee.writeShort(v);
    }

    public void writeUTF(String s) throws IOException {
        adaptee.writeUTF(s);
    }

    public boolean readBoolean() throws IOException {
        return adaptee.readBoolean();
    }

    public byte readByte() throws IOException {
        return adaptee.readByte();
    }

    public char readChar() throws IOException {
        return adaptee.readChar();
    }

    public double readDouble() throws IOException {
        return adaptee.readDouble();
    }

    public float readFloat() throws IOException {
        return adaptee.readFloat();
    }

    public void readFully(byte[] b) throws IOException {
        adaptee.readFully(b);
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        adaptee.readFully(b, off, len);
    }

    public int readInt() throws IOException {
        return adaptee.readInt();
    }

    public String readLine() throws IOException {
        return adaptee.readLine();
    }

    public long readLong() throws IOException {
        return adaptee.readLong();
    }

    public short readShort() throws IOException {
        return adaptee.readShort();
    }

    public String readUTF() throws IOException {
        return adaptee.readUTF();
    }

    public int readUnsignedByte() throws IOException {
        return adaptee.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
        return adaptee.readUnsignedShort();
    }

    public int skipBytes(int n) throws IOException {
        return adaptee.skipBytes(n);
    }
}
