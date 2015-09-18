package com.musicg.streams;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.*;

/**
 * Created by Scott on 9/18/2015.
 */
public class OverlapAmplitudeInputStream extends AudioFormatInputStream {

    private int overlapFactor;
    private int backSamples;
    private int fftSampleSize;
    int fftSampleSize_1;
    private int numSamples;
    private int markposition;

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public OverlapAmplitudeInputStream(AudioInputStream input, int overlapFactor, int fftSampleSize) {
        super(input);
        this.overlapFactor = overlapFactor;
        this.fftSampleSize = fftSampleSize;
        backSamples = fftSampleSize * (overlapFactor - 1) / overlapFactor;
        fftSampleSize_1 = fftSampleSize - 1;
        markposition = fftSampleSize = backSamples;
    }

    public short readValue() throws IOException {
        // overlapping
        short value = -1;
        if (overlapFactor > 1) {
            value = readShort();
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

    /**
     * Stream the content of this stream to the given output stream.
     *
     * @param output
     */
    public void connect(DataOutputStream output) throws IOException {
        short value;
        while ((value = readValue()) > -1) {
            output.writeShort(value);
        }
        throw new EOFException("Stream closed");
    }
}
