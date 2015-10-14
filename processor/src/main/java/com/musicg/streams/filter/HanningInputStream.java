package com.musicg.streams.filter;

import com.musicg.dsp.WindowFunction;
import com.musicg.spectrogram.Spectrogram;

import java.io.IOException;

/**
 * Created by Scott on 9/19/2015.
 */
public class HanningInputStream extends PipedAudioFilter {

    private int fftSampleSize = Spectrogram.SPECTROGRAM_DEFAULT_FFT_SAMPLE_SIZE;
    private double[] win;
    private int readOffset;

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public HanningInputStream(PipedAudioFilter input) {
        super(input);
        createWindow();
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public HanningInputStream(PipedAudioFilter input, int fftSampleSize) {
        super(input);
        this.fftSampleSize = fftSampleSize;
        createWindow();
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public HanningInputStream(PipedAudioFilter input, boolean useLittleEndian) {
        super(input, useLittleEndian);
        createWindow();
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public HanningInputStream(PipedAudioFilter input, boolean useLittleEndian, int fftSampleSize) {
        super(input, useLittleEndian);
        this.fftSampleSize = fftSampleSize;
        createWindow();
    }

    private void createWindow() {
        // set signals for fft
        WindowFunction window = new WindowFunction();
        window.setWindowType("Hamming");
        win = window.generate(fftSampleSize);
    }

    public double readDouble() throws IOException {
        // modify data with window data
        double value = inputStream.readShort() * win[readOffset++];
        if (readOffset >= fftSampleSize) {
            readOffset = 0;
        }
        return value;
    }

    public void pipeValue() throws IOException {
        outputStream.writeDouble(readDouble());
    }
}
