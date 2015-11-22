package com.musicg.streams.filter;

import com.musicg.streams.AudioFormatInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Create overlap amplitude values.
 * <p/>
 * Created by Scott on 9/18/2015.
 */
public class OverlapAmplitudeFilter extends PipedAudioFilter {

    private int overlapFactor;
    private int fftSampleSize;
    private int numSamples;
    private int markPosition;

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public OverlapAmplitudeFilter(PipedAudioFilter input, int overlapFactor, int fftSampleSize) {
        super(new AudioFormatInputStream(new BufferedInputStream(input, fftSampleSize * 2), input.getAudioFormat(), true), true);
        initialize(overlapFactor, fftSampleSize);
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public OverlapAmplitudeFilter(PipedAudioFilter input, boolean useLittleEndian, int overlapFactor, int fftSampleSize) {
        super(new AudioFormatInputStream(new BufferedInputStream(input, fftSampleSize * 2), input.getAudioFormat(), true), true);
        initialize(overlapFactor, fftSampleSize);
    }

    private void initialize(int overlapFactor, int fftSampleSize) {
        this.overlapFactor = overlapFactor;
        this.fftSampleSize = fftSampleSize;

        // mark reset for window. Sample size - back samples.
        if (overlapFactor > 0) {
            markPosition = fftSampleSize / overlapFactor;
        }
    }

    /**
     * Read a short value from the underlying stream.
     * This will mark and reset the underlying stream as appropriate based on the overlap factor.
     *
     * @return The short value read from the stream.
     * @throws IOException
     */
    public short readShort() throws IOException {
        // overlapping
        try {
            short value = super.readShort();
            if (overlapFactor > 1) {
                numSamples++;
                if (numSamples == markPosition) {
                    mark(fftSampleSize * 2);
                }
                if (numSamples % fftSampleSize == 0) {
                    // overlap
                    reset();
                    numSamples = 0;
                }
            }
            return value;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public int pipeValue() throws IOException {
        outputStream.writeShort(readShort());
        return Short.BYTES;
    }
}
