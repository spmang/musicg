package com.musicg.streams;

import com.musicg.dsp.WindowFunction;
import com.musicg.spectrogram.Spectrogram;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Scott on 9/19/2015.
 */
public class HammingInputStream extends PipedAudioFormatInputStream {

    private int fftSampleSize = Spectrogram.SPECTROGRAM_DEFAULT_FFT_SAMPLE_SIZE;
    private double[] win;
    private int readOffset;

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public HammingInputStream(AudioInputStream input) {
        super(input);
        createWindow();
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public HammingInputStream(AudioInputStream input, int fftSampleSize) {
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
    public HammingInputStream(AudioInputStream input, boolean useLittleEndian) {
        super(input, useLittleEndian);
        createWindow();
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public HammingInputStream(AudioInputStream input, boolean useLittleEndian, int fftSampleSize) {
        super(input, useLittleEndian);
        this.fftSampleSize = fftSampleSize;
        createWindow();
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in     The stream to wrap.
     * @param format
     */
    public HammingInputStream(InputStream in, AudioFormat format, int fftSampleSize) {
        super(in, format);
        this.fftSampleSize = fftSampleSize;
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in              The stream to wrap.
     * @param format
     * @param useLittleEndian
     */
    public HammingInputStream(InputStream in, AudioFormat format, boolean useLittleEndian, int fftSampleSize) {
        super(in, format, useLittleEndian);
        this.fftSampleSize = fftSampleSize;
    }

    private void createWindow() {
        // set signals for fft
        WindowFunction window = new WindowFunction();
        window.setWindowType("Hamming");
        win = window.generate(fftSampleSize);
    }

    public double readDouble() throws IOException {
        // modify data with window data
        double value = super.readShort() * win[readOffset++];
        if (readOffset > fftSampleSize) {
            readOffset = 0;
        }
        return value;
    }

    public void readValue() throws IOException {
        outputStream.writeDouble(readDouble());
    }
}
