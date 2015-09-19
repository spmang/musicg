package com.musicg.streams;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

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
        initialize(overlapFactor, fftSampleSize);
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public OverlapAmplitudeInputStream(AudioInputStream input, boolean useLittleEndian, int overlapFactor, int fftSampleSize) {
        super(input, useLittleEndian);
        initialize(overlapFactor, fftSampleSize);
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in     The stream to wrap.
     * @param format
     */
    public OverlapAmplitudeInputStream(InputStream in, AudioFormat format, int overlapFactor, int fftSampleSize) {
        super(in, format);
        this.overlapFactor = overlapFactor;
        this.fftSampleSize = fftSampleSize;
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in              The stream to wrap.
     * @param format
     * @param useLittleEndian
     */
    public OverlapAmplitudeInputStream(InputStream in, AudioFormat format, boolean useLittleEndian, int overlapFactor, int fftSampleSize) {
        super(in, format, useLittleEndian);
        this.overlapFactor = overlapFactor;
        this.fftSampleSize = fftSampleSize;
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
        short value = super.readShort();
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

    public void readValue() throws IOException {
        outputStream.writeShort(readShort());
    }
}
