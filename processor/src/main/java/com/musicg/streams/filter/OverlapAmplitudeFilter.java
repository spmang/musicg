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
    private int fftSampleSize_1;
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

        // offset for 0 index.
        fftSampleSize_1 = fftSampleSize - 1;

        // mark reset for window. Sample size - back samples.
        markPosition = fftSampleSize - (fftSampleSize * (overlapFactor - 1) / overlapFactor);
    }

    int readCount =0;
    public short readShort() throws IOException {
        // overlapping
        try {
            short value = super.readShort();
            if (overlapFactor > 1) {
                if (numSamples++ % fftSampleSize == fftSampleSize_1) {
                    // overlap
                    reset();
                    numSamples = 0;
                }
                if (numSamples == markPosition) {
                    mark(fftSampleSize * 2);
                }
            }
            readCount++;
            return value;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public void pipeValue() throws IOException {
        outputStream.writeShort(readShort());
    }
}
