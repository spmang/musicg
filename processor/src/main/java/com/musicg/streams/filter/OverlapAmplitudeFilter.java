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
        markPosition = fftSampleSize / overlapFactor;
    }

    int readCount = 0;
    int markCount =0;
    int resetCount =0;

    public short readShort() throws IOException {
        // overlapping
        try {
            short value = super.readShort();
            numSamples ++;
            readCount++;
            if (overlapFactor > 1) {
                if (numSamples == markPosition) {
                    markCount ++;
                    mark(fftSampleSize * 2);
                }
                if (numSamples % fftSampleSize == 0) {
                    // overlap
                    resetCount ++;
                    reset();
                    numSamples = 0;
                }
            }
            return value;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public void pipeValue() throws IOException {
        outputStream.writeShort(readShort());
    }
}
