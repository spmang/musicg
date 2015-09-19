package com.musicg.streams;

import javax.sound.sampled.AudioInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * Create overlap amplitude values.
 * <p/>
 * Created by Scott on 9/18/2015.
 */
public class OverlapAmplitudeInputStream extends PipedAudioFormatInputStream {

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
    public OverlapAmplitudeInputStream(AudioInputStream input, int overlapFactor, int fftSampleSize) {
        super(input);
        this.overlapFactor = overlapFactor;
        this.fftSampleSize = fftSampleSize;
        backSamples = fftSampleSize * (overlapFactor - 1) / overlapFactor;
        fftSampleSize_1 = fftSampleSize - 1;
        markposition = fftSampleSize = backSamples;
    }

    public void readValue() throws IOException {
        // overlapping
        if (overlapFactor > 1) {
            short value = readShort();
            if (value == -1) {
                throw new EOFException("End of stream.");
            }
            outputStream.writeShort(value);
            if (++numSamples % fftSampleSize == fftSampleSize_1) {
                // overlap
                this.reset();
                numSamples = 0;
            }
            if (numSamples == markposition) {
                this.mark(backSamples);
            }
        }
    }
}
