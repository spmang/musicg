package com.musicg.streams;

import javax.sound.sampled.AudioFormat;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Scott on 9/19/2015.
 */
public class AudioFormatOutputStream extends FilterOutputStream {

    private AudioFormat header;

    private boolean littleEndian;

    public AudioFormatOutputStream(OutputStream output, AudioFormat format) {
        super(output);
        header = format;
    }

    public AudioFormatOutputStream(OutputStream output, AudioFormat format, boolean useLittleEndian) {
        this(output, format);
        littleEndian = useLittleEndian;
    }

    /**
     * Writes the specified byte (the low eight bits of the argument
     * <code>b</code>) to the underlying output stream. If no exception
     * is thrown, the counter <code>written</code> is incremented by
     * <code>1</code>.
     * <p/>
     * Implements the <code>write</code> method of <code>OutputStream</code>.
     *
     * @param b the <code>byte</code> to be written.
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public synchronized void write(int b) throws IOException {
        out.write(b);
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to the underlying output stream.
     * If no exception is thrown, the counter <code>written</code> is
     * incremented by <code>len</code>.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public synchronized void write(byte b[], int off, int len)
            throws IOException {
        out.write(b, off, len);
    }

    /**
     * Flushes this data output stream. This forces any buffered output
     * bytes to be written out to the stream.
     * <p/>
     * The <code>flush</code> method of <code>DataOutputStream</code>
     * calls the <code>flush</code> method of its underlying output stream.
     *
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     * @see java.io.OutputStream#flush()
     */
    public void flush() throws IOException {
        out.flush();
    }

    /**
     * Writes a <code>boolean</code> to the underlying output stream as
     * a 1-byte value. The value <code>true</code> is written out as the
     * value <code>(byte)1</code>; the value <code>false</code> is
     * written out as the value <code>(byte)0</code>. If no exception is
     * thrown, the counter <code>written</code> is incremented by
     * <code>1</code>.
     *
     * @param v a <code>boolean</code> value to be written.
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public final void writeBoolean(boolean v) throws IOException {
        out.write(v ? 1 : 0);
    }

    /**
     * Writes out a <code>byte</code> to the underlying output stream as
     * a 1-byte value. If no exception is thrown, the counter
     * <code>written</code> is incremented by <code>1</code>.
     *
     * @param v a <code>byte</code> value to be written.
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public final void writeByte(int v) throws IOException {
        out.write(v);
    }

    /**
     * Writes a <code>short</code> to the underlying output stream as two
     * bytes, high byte first. If no exception is thrown, the counter
     * <code>written</code> is incremented by <code>2</code>.
     *
     * @param v a <code>short</code> to be written.
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public final void writeShort(int v) throws IOException {
        if (littleEndian) {
            out.write((v >>> 0) & 0xFF);
            out.write((v >>> 8) & 0xFF);
        } else {
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 0) & 0xFF);
        }
    }

    /**
     * Writes an <code>int</code> to the underlying output stream as four
     * bytes, high byte first. If no exception is thrown, the counter
     * <code>written</code> is incremented by <code>4</code>.
     *
     * @param v an <code>int</code> to be written.
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public final void writeInt(int v) throws IOException {
        if (littleEndian) {
            out.write((v >>> 0) & 0xFF);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 16) & 0xFF);
            out.write((v >>> 24) & 0xFF);

        } else {
            out.write((v >>> 24) & 0xFF);
            out.write((v >>> 16) & 0xFF);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 0) & 0xFF);
        }
    }

    private byte writeBuffer[] = new byte[8];

    /**
     * Writes a <code>long</code> to the underlying output stream as eight
     * bytes, high byte first. In no exception is thrown, the counter
     * <code>written</code> is incremented by <code>8</code>.
     *
     * @param v a <code>long</code> to be written.
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public final void writeLong(long v) throws IOException {
        if (littleEndian) {
            writeBuffer[0] = (byte) (v >>> 0);
            writeBuffer[1] = (byte) (v >>> 8);
            writeBuffer[2] = (byte) (v >>> 16);
            writeBuffer[3] = (byte) (v >>> 24);
            writeBuffer[4] = (byte) (v >>> 32);
            writeBuffer[5] = (byte) (v >>> 40);
            writeBuffer[6] = (byte) (v >>> 48);
            writeBuffer[7] = (byte) (v >>> 56);
            out.write(writeBuffer, 0, 8);
        } else {
            writeBuffer[0] = (byte) (v >>> 56);
            writeBuffer[1] = (byte) (v >>> 48);
            writeBuffer[2] = (byte) (v >>> 40);
            writeBuffer[3] = (byte) (v >>> 32);
            writeBuffer[4] = (byte) (v >>> 24);
            writeBuffer[5] = (byte) (v >>> 16);
            writeBuffer[6] = (byte) (v >>> 8);
            writeBuffer[7] = (byte) (v >>> 0);
            out.write(writeBuffer, 0, 8);
        }
    }

    /**
     * Converts the float argument to an <code>int</code> using the
     * <code>floatToIntBits</code> method in class <code>Float</code>,
     * and then writes that <code>int</code> value to the underlying
     * output stream as a 4-byte quantity, high byte first. If no
     * exception is thrown, the counter <code>written</code> is
     * incremented by <code>4</code>.
     *
     * @param v a <code>float</code> value to be written.
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     * @see java.lang.Float#floatToIntBits(float)
     */
    public final void writeFloat(float v) throws IOException {
        // TODO fix me
        writeInt(Float.floatToIntBits(v));
    }

    /**
     * Converts the double argument to a <code>long</code> using the
     * <code>doubleToLongBits</code> method in class <code>Double</code>,
     * and then writes that <code>long</code> value to the underlying
     * output stream as an 8-byte quantity, high byte first. If no
     * exception is thrown, the counter <code>written</code> is
     * incremented by <code>8</code>.
     *
     * @param v a <code>double</code> value to be written.
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     * @see java.lang.Double#doubleToLongBits(double)
     */
    public final void writeDouble(double v) throws IOException {
        // TODO fix me
        writeLong(Double.doubleToLongBits(v));
    }
}
