package com.musicg.streams;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Custom stream read to simplify the reading of audio data from InputStreams.
 * Setting littleEndian to true will cause the stream to read littleEndian, but still write bigEndian.
 * <p/>
 * // TODO fix the stream to write littleEndian.
 * <p/>
 * Created by scottmangan on 9/18/15.
 */
public class AudioFormatInputStream extends FilterInputStream {

    protected AudioFormat audioFormat;

    protected boolean littleEndian;

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public AudioFormatInputStream(AudioInputStream input) {
        super(input);
        audioFormat = input.getFormat();
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in The stream to wrap.
     */
    public AudioFormatInputStream(InputStream in, AudioFormat format) {
        super(new AudioInputStream(in, format, AudioSystem.NOT_SPECIFIED));
        audioFormat = format;
    }

    /**
     * Get the AudioInputStream this stream wraps.
     *
     * @return The AudioInputStream
     */
    public AudioInputStream getAudioInputStream() {
        return (AudioInputStream) in;
    }

    /**
     * Get the AudioFormat header for the stream.
     *
     * @return The AudioFormat header.
     */
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    /**
     * See the general contract of the <code>readFully</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @param b the buffer into which the data is read.
     * @throws EOFException if this input stream reaches the end before
     *                      reading all the bytes.
     * @throws IOException  the stream has been closed and the contained
     *                      input stream does not support reading after close, or
     *                      another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final void readFully(byte b[]) throws IOException {
        readFully(b, 0, b.length);
    }

    /**
     * See the general contract of the <code>readFully</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the number of bytes to read.
     * @throws EOFException if this input stream reaches the end before
     *                      reading all the bytes.
     * @throws IOException  the stream has been closed and the contained
     *                      input stream does not support reading after close, or
     *                      another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final void readFully(byte b[], int off, int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }

    /**
     * See the general contract of the <code>skipBytes</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes for this operation are read from the contained
     * input stream.
     *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @throws IOException if the contained input stream does not support
     *                     seek, or the stream has been closed and
     *                     the contained input stream does not support
     *                     reading after close, or another I/O error occurs.
     */
    public final int skipBytes(int n) throws IOException {
        int total = 0;
        int cur = 0;

        while ((total < n) && ((cur = (int) in.skip(n - total)) > 0)) {
            total += cur;
        }

        return total;
    }

    /**
     * See the general contract of the <code>readByte</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @return the next byte of this input stream as a signed 8-bit
     * <code>byte</code>.
     * @throws EOFException if this input stream has reached the end.
     * @throws IOException  the stream has been closed and the contained
     *                      input stream does not support reading after close, or
     *                      another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final byte readByte() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return (byte) (ch);
    }

    /**
     * See the general contract of the <code>readUnsignedByte</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @return the next byte of this input stream, interpreted as an
     * unsigned 8-bit number.
     * @throws EOFException if this input stream has reached the end.
     * @throws IOException  the stream has been closed and the contained
     *                      input stream does not support reading after close, or
     *                      another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final int readUnsignedByte() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }

    /**
     * See the general contract of the <code>readShort</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @return the next two bytes of this input stream, interpreted as a
     * signed 16-bit number.
     * @throws EOFException if this input stream reaches the end before
     *                      reading two bytes.
     * @throws IOException  the stream has been closed and the contained
     *                      input stream does not support reading after close, or
     *                      another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final short readShort() throws IOException {
        byte[] shortBytes = new byte[2];
        this.readFully(shortBytes, 0, 2);
        return (short) (littleEndian ? ((shortBytes[1] << 8) + (shortBytes[0] << 0)) :
                ((shortBytes[0] << 8) + (shortBytes[1] << 0)));
    }

    /**
     * See the general contract of the <code>readUnsignedShort</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @return the next two bytes of this input stream, interpreted as an
     * unsigned 16-bit integer.
     * @throws EOFException if this input stream reaches the end before
     *                      reading two bytes.
     * @throws IOException  the stream has been closed and the contained
     *                      input stream does not support reading after close, or
     *                      another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final int readUnsignedShort() throws IOException {
        byte[] shortBytes = new byte[2];
        readFully(shortBytes, 0, 2);
        return littleEndian ? ((shortBytes[1] << 8) + (shortBytes[0] << 0)) :
                ((shortBytes[0] << 8) + (shortBytes[1] << 0));
    }

    /**
     * See the general contract of the <code>readInt</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @return the next four bytes of this input stream, interpreted as an
     * <code>int</code>.
     * @throws EOFException if this input stream reaches the end before
     *                      reading four bytes.
     * @throws IOException  the stream has been closed and the contained
     *                      input stream does not support reading after close, or
     *                      another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final int readInt() throws IOException {
        byte[] intBytes = new byte[4];
        readFully(intBytes, 0, 4);
        return littleEndian ? ((intBytes[3] << 24) + (intBytes[2] << 16) + (intBytes[1] << 8) + (intBytes[0] << 0)) :
                ((intBytes[0] << 24) + (intBytes[1] << 16) + (intBytes[2] << 8) + (intBytes[3] << 0));
    }

    /**
     * See the general contract of the <code>readLong</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @return the next eight bytes of this input stream, interpreted as a
     * <code>long</code>.
     * @throws EOFException if this input stream reaches the end before
     *                      reading eight bytes.
     * @throws IOException  the stream has been closed and the contained
     *                      input stream does not support reading after close, or
     *                      another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final long readLong() throws IOException {
        byte[] readBuffer = new byte[8];
        readFully(readBuffer, 0, 8);
        return littleEndian ?
                (((long) readBuffer[7] << 56) +
                        ((long) (readBuffer[6] & 255) << 48) +
                        ((long) (readBuffer[5] & 255) << 40) +
                        ((long) (readBuffer[4] & 255) << 32) +
                        ((long) (readBuffer[3] & 255) << 24) +
                        ((readBuffer[2] & 255) << 16) +
                        ((readBuffer[1] & 255) << 8) +
                        ((readBuffer[0] & 255) << 0)) :
                (((long) readBuffer[0] << 56) +
                        ((long) (readBuffer[1] & 255) << 48) +
                        ((long) (readBuffer[2] & 255) << 40) +
                        ((long) (readBuffer[3] & 255) << 32) +
                        ((long) (readBuffer[4] & 255) << 24) +
                        ((readBuffer[5] & 255) << 16) +
                        ((readBuffer[6] & 255) << 8) +
                        ((readBuffer[7] & 255) << 0));
    }

    /**
     * See the general contract of the <code>readFloat</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @return the next four bytes of this input stream, interpreted as a
     * <code>float</code>.
     * @throws EOFException if this input stream reaches the end before
     *                      reading four bytes.
     * @throws IOException  the stream has been closed and the contained
     *                      input stream does not support reading after close, or
     *                      another I/O error occurs.
     * @see java.io.DataInputStream#readInt()
     * @see java.lang.Float#intBitsToFloat(int)
     */
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * See the general contract of the <code>readDouble</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @return the next eight bytes of this input stream, interpreted as a
     * <code>double</code>.
     * @throws EOFException if this input stream reaches the end before
     *                      reading eight bytes.
     * @throws IOException  the stream has been closed and the contained
     *                      input stream does not support reading after close, or
     *                      another I/O error occurs.
     * @see java.io.DataInputStream#readLong()
     * @see java.lang.Double#longBitsToDouble(long)
     */
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public boolean isLittleEndian() {
        return littleEndian;
    }

    public void setLittleEndian(boolean littleEndian) {
        this.littleEndian = littleEndian;
    }
}
