package com.musicg.streams.filter;

import java.io.EOFException;
import java.io.IOException;

/**
 * Create overlap amplitude values.
 * <p/>
 * Created by Scott on 9/18/2015.
 */
public class OverlapAmplitudeFilter extends PipedAudioFilter {

    private int overlapFactor;
    private int backSamples;
    private int fftSampleSize;
    private int fftSampleSize_1;
    private int numSamples;
    private int markposition;

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public OverlapAmplitudeFilter(PipedAudioFilter input, int overlapFactor, int fftSampleSize) {
        super(input);
        initialize(overlapFactor, fftSampleSize);
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public OverlapAmplitudeFilter(PipedAudioFilter input, boolean useLittleEndian, int overlapFactor, int fftSampleSize) {
        super(input, useLittleEndian);
        initialize(overlapFactor, fftSampleSize);
    }

    private void initialize(int overlapFactor, int fftSampleSize) {
        this.overlapFactor = overlapFactor;
        this.fftSampleSize = fftSampleSize;
        backSamples = fftSampleSize * (overlapFactor - 1) / overlapFactor;
        fftSampleSize_1 = fftSampleSize - 1;
        markposition = fftSampleSize = backSamples;
    }

    public short readShort() throws IOException {
        // overlapping
        short value = inputStream.readShort();
        if (overlapFactor > 1) {
            if (value == -1) {
                throw new EOFException("End of stream.");
            }
            if (++numSamples % fftSampleSize == fftSampleSize_1) {
                // overlap
                this.reset();
                numSamples = 0;
            }
            if (numSamples == markposition) {
                this.mark(backSamples);
            }
        }
        return value;
    }

    public void pipeValue() throws IOException {
        outputStream.writeShort(readShort());
    }
}
