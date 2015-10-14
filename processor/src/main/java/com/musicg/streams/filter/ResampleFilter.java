package com.musicg.streams.filter;

import java.io.EOFException;
import java.io.IOException;

/**
 * Resample the audio stream. Currenly only works for short type on wave.
 * <p/>
 * <p/>
 * Created by Scott on 9/18/2015.
 */
public class ResampleFilter extends PipedAudioFilter {

    private float sampleRate;
    private long position;
    private int lastPosition;
    private float lengthMultiplier;

    public ResampleFilter(PipedAudioFilter input, float newSampleRate) {
        super(input);
        sampleRate = newSampleRate;
        lengthMultiplier = (float) sampleRate / audioFormat.getSampleRate();
    }

    public ResampleFilter(PipedAudioFilter input, boolean useLittleEndian, float newSampleRate) {
        super(input, useLittleEndian);
        sampleRate = newSampleRate;
        lengthMultiplier = (float) sampleRate / audioFormat.getSampleRate();
    }


    /**
     * Reads up to <code>len</code> bytes of data from this input stream
     * into an array of bytes. If <code>len</code> is not zero, the method
     * blocks until some input is available; otherwise, no
     * bytes are read and <code>0</code> is returned.
     * <p/>
     * This method simply performs <code>in.read(b, off, len)</code>
     * and returns the result.
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset in the destination array <code>b</code>
     * @param len the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or
     * <code>-1</code> if there is no more data because the end of
     * the stream has been reached.
     * @throws NullPointerException      If <code>b</code> is <code>null</code>.
     * @throws IndexOutOfBoundsException If <code>off</code> is negative,
     *                                   <code>len</code> is negative, or <code>len</code> is greater than
     *                                   <code>b.length - off</code>
     * @throws IOException               if an I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public int read(byte b[], int off, int len) throws IOException {
        int counter = off;
        for (; counter < off + len; counter += 2) {
            try {
                short value = readShort();
                if (littleEndian) {
                    b[counter] = (byte) ((value >>> 0) & 0xFF);
                    b[counter + 1] = (byte) ((value >>> 8) & 0xFF);
                } else {
                    b[counter] = (byte) ((value >>> 8) & 0xFF);
                    b[counter + 1] = (byte) ((value >>> 0) & 0xFF);
                }
            } catch (EOFException ioe) {
                if (counter == off) {
                    throw ioe;
                }
            }
        }
        return counter - off;
    }

    public short readShort() throws IOException {
        // do interpolation
        // TODO currently only works up to Long.MAX_VALUE. Fix to stream indefinitely.

        // get the nearest positions for the interpolated point
        float currentPosition = position++ / lengthMultiplier;
        int nearestLeftPosition = (int) currentPosition;

        skipBytes(((nearestLeftPosition - lastPosition) - 2) * 2);
        short nearestLeft = super.readShort();

        short value = (short) ((super.readShort() - nearestLeft) * (currentPosition - nearestLeftPosition) + nearestLeft);    // y=mx+c
        lastPosition = nearestLeftPosition;
        // end do interpolation

        // TODO: Remove the high frequency signals with a digital filter, leaving a signal containing only half-sample-rated frequency information, but still sampled at a rate of target sample rate. Usually FIR is used
        return value;
    }

    public void pipeValue() throws IOException {
        outputStream.writeShort(readShort());
    }
}
